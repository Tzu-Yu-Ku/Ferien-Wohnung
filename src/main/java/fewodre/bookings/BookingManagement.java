package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.catalog.events.EventCatalog;

import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class BookingManagement {

	private final BookingRepository bookings;
	private final OrderManagement orderManagement;
	private final HolidayHomeCatalog homeCatalog;
	private final EventCatalog eventCatalog;
	private final UniqueInventory<UniqueInventoryItem> holidayHomeStorage;
	private final AccountRepository accounts;

	/**
	 * Creates a new {@link BookingManagement} with the given Parameters. {@link BookingManagement}
	 *
	 * @param bookings must not be {@literal null}
	 * @param orderManagement must not be {@literal null}
	 * @param homeCatalog must not be {@literal null}
	 * @param eventCatalog must not be {@literal null}
	 * @param holidayHomeStorage must not be {@literal null}
	 * @param accountManagement must not be {@literal null}
	 */
	@Autowired
	BookingManagement(BookingRepository bookings, OrderManagement<Order> orderManagement,
					  HolidayHomeCatalog homeCatalog, EventCatalog eventCatalog,
					  UniqueInventory<UniqueInventoryItem> holidayHomeStorage, AccountManagement accountManagement){

		Assert.notNull(bookings, "BookingRepository must not be null!");
		Assert.notNull(orderManagement, "OrderManagement must not be null!");
		Assert.notNull(homeCatalog, "HomeCatalog Must not be Null!");
		Assert.notNull(eventCatalog, "EventCatalog Must not be Null!");
		Assert.notNull(accountManagement, "accountManager Must not be Null!");

		this.bookings = bookings;
		this.orderManagement = orderManagement;
		this.homeCatalog = homeCatalog;
		this.eventCatalog = eventCatalog;
		this.holidayHomeStorage = holidayHomeStorage;
		this.accounts = accountManagement.getRepository();
	}

	/**
	 * creat a {@link BookingEntity} with the required informations.
	 *
	 * @param userAccount the user who make the order, muss not be {@literal null}
	 * @param home the house which is booked in this BookingEntity, muss not be {@literal null}
	 * @param cart the cart in used, muss not be {@literal null}
	 * @param arrivalDate the arrival date given from the user, muss not be {@literal null}
	 * @param departureDate the departure date given from the user, muss not be {@literal null}
	 * @param events the booked events, muss not be {@literal null}
	 * @param paymethod the chosen paymethod from the user, muss not be {@literal null}
	 * @return a BookingEntity
	 */
	public BookingEntity createBookingEntity(UserAccount userAccount, HolidayHome home, Cart cart,
											 /*PaymentMethod paymentMethod,*/ LocalDate arrivalDate,
											 LocalDate departureDate, HashMap<Event, Integer> events, String paymethod){
		Quantity nights = Quantity.of(ChronoUnit.DAYS.between(arrivalDate, departureDate));
		//HolidayHome home = catalog.findFirstByProductIdentifier(uuidHome);
		BookingEntity bookingEntity = new BookingEntity(userAccount, this.accounts.findByAccount_Email(home.getHostMail()),home, nights, arrivalDate, departureDate, events, paymethod);
		cart.addItemsTo(bookingEntity);
		//order open()
		//will update quantity one time
		//cart.getItem(home.getId().toString());
		if(!holidayHomeStorage.findByProduct(home).isPresent()){
			holidayHomeStorage.save(new UniqueInventoryItem(home, nights.add(nights)));
		}else{
			holidayHomeStorage.findByProduct(home).get().increaseQuantity(nights.add(nights));
		}
		Iterator<Event> eventIterator = bookingEntity.getEvents(eventCatalog).iterator();
		while (eventIterator.hasNext()){
			eventCatalog.findFirstByProductIdentifier(eventIterator.next().getId()).addSubscriber(bookingEntity);
		}
		cart.clear();
		System.out.println("cart is empty: "+cart.isEmpty());
		BookingEntity result = bookings.save(bookingEntity);
		System.out.println(result == bookingEntity);
		return result ;
	}


	/**
	 * check if the {@link BookingEntity} is paid.
	 *
	 * @param bookingEntity muss not be {@literal null}
	 * @return true if is paid, false is not
	 */
	public boolean payDeposit(BookingEntity bookingEntity) {
		if(getMoney(bookingEntity.getDepositInCent()*0.01f,
				bookingEntity.getPaymethod(), bookingEntity.getUuidTenant())){
			//orderManagement.payOrder(bookingEntity)
			//orderManagement.completeOrder(bookingEntity);
			if(bookingEntity.pay()){return true;}
			else {
				System.out.println("etwas ist bei der Bezahlung schiefgelaufen whr. falscher State");
				giveMoney(bookingEntity.getDepositInCent()*0.01f, bookingEntity.getPaymethod(),
						bookingEntity.getUuidTenant()); //return Money
				return false;
			}

		}
		return false;
	}

	/**
	 * when the event be cancelled, call the Paying-Framework from banks to refund the paid
	 *
	 * @param event muss not be {@literal null}
	 * @return true
	 */
	public boolean cancelEvent(Event event) {
		Iterator<String> bookingIdentifier = event.getSubscriber().iterator();
		List<BookingEntity> bookingEntities = new LinkedList<BookingEntity>();
		while (bookingIdentifier.hasNext()){
			bookingEntities.add(this.bookings.findFirstByOrderIdentifier(bookingIdentifier.next()));
		}
		Iterator<BookingEntity> bookingIter = bookingEntities.iterator();
		while (bookingIter.hasNext()){
			BookingEntity booking = bookingIter.next();
			if(booking.getState().compareTo(BookingStateEnum.PAID) == 0 ||
			   booking.getState().compareTo(BookingStateEnum.ACQUIRED) == 0 ||
				booking.getState().compareTo(BookingStateEnum.CONFIRMED) == 0){
					giveMoney(booking.getPriceOf(event), booking.getPaymethod(), booking.getUuidTenant());
			}
			booking.cancelEvent(event);
		}
		return true;
	}

	/**
	 * check if the rest of the unpaid from the booking is paid
	 *
	 * @param bookingEntity muss not be {@literal null}
	 * @return if is paid return true, otherwise false
	 */
	public boolean payRest(BookingEntity bookingEntity){
	if(orderManagement.payOrder(bookingEntity)) {
		if (getMoney(bookingEntity.getTotal().getNumber().floatValue(),
				bookingEntity.getPaymethod(), bookingEntity.getUuidTenant())) {
			orderManagement.completeOrder(bookingEntity);
			return true;
		}
	}
		return false;
	}

	/**
	 * get how much the product being booked from the {@link AccountEntity}(who hat the role of TENANT.)
	 *
	 * @param product muss not be {@literal null}
	 * @return the amount of the being booked product in int type
	 */
	public int getStockCountOf(Product product) {
		if (holidayHomeStorage.findByProduct(product).isEmpty()) {
			return 0;
		}
		return holidayHomeStorage.findByProduct(product).get().getQuantity().getAmount().intValue();
	}

	/**
	 * Interface for the Paying-Framework of the customer.
	 * Shall return true when the transaction of the money from
	 * the tenant to the host or us was successful
	 *
	 * @param bill the amount which will be paid, muss not be {@literal null}
	 * @param chosenPaymethod the chose paymethod, muss not be {@literal null}
	 * @param uuidTenant the ID from the current TENANT
	 * @return true
	 */
	public boolean getMoney(float bill, Paymethod chosenPaymethod, String uuidTenant) {
		System.out.println(uuidTenant + " paid: " + bill + "€");
		return true;
	}


	/**
	 * Interface for the Paying-Framework of the customer.
	 * Shall return true when the transaction of the money from
	 * the host or us to the tenant was successful
	 *
	 * @param bill the amount which will be refunded, muss not be {@literal null}
	 * @param uuidTenant the ID from the current TENANT
	 * @return true when the transaction successful
	 */
	public boolean giveMoney(float bill, Paymethod choosenPaymethod, String uuidTenant){
		System.out.println("depaid: " + bill + "€ to: " + uuidTenant);
		return true;
	}

	/**
	 * get all of the {@link BookingEntity} in the system
	 *
	 * @return all bookings
	 */
	public Streamable<BookingEntity> findAll() {
		return bookings.findAll();
	}

	//after createBookingEntity, we can already save in bookingRepository
	//need to create findByStatusPaid

	/**
	 * get all of the {@link BookingEntity} by the given {@link HolidayHome} ID in the system
	 *
	 * @param holidayHome muss not be {@literal null}
	 * @return all bookings which contains the given house's ID
	 */
	public Streamable<BookingEntity> findBookingsByUuidHome(ProductIdentifier holidayHome) {
		return bookings.findBookingsByUuidHome(holidayHome.toString());
	}


	/**
	 * get all of the {@link BookingEntity} by the given {@link UserAccount} in the system
	 *
	 * @param userAccount muss not be {@literal null}
	 * @return all bookings which booked from the given UserAccount
	 */
	public Iterable<BookingEntity> findBookingEntityByUserAccount(UserAccount userAccount) {
		return bookings.findBookingEntityByUserAccount(userAccount);
	}


	/**
	 * get the {@link BookingEntity} by the given {@link OrderIdentifier}
	 *
	 * @param orderIdentifier muss not be {@literal null}
	 * @return the wanted BookingEntity
	 */
	public BookingEntity findFirstByOrderIdentifier(OrderIdentifier orderIdentifier) {
		return bookings.findFirstByOrderIdentifier(orderIdentifier);
	}


	/**
	 * get all of the {@link BookingEntity} by the given state and host's ID in the system
	 *
	 * @param state muss not be {@literal null}
	 * @param host muss not be {@literal null}
	 * @return the bookingEntities of the given state and host's IS
	 */
	public Streamable<BookingEntity> findByState(String state, String host) {
		if (state.equals("ALL")) {
			return bookings.findAllByUuidHost(host);
		}
		List<BookingEntity> all = bookings.findAllByUuidHost(host).toList();
		List<BookingEntity> stream = new ArrayList<>();
		all.stream().filter(bookingEntity -> bookingEntity.getState().toString().equals(state))
				.forEach(bookingEntity -> stream.add(bookingEntity));
		return Streamable.of(stream);
	}

	/**
	 * get all of the {@link BookingEntity} by the given tenant's lastname and host's ID in the system
	 *
	 * @param name muss not be {@literal null}
	 * @param host muss not be {@literal null}
	 * @return the bookingEntities of the given lastname of tenant and host's IS
	 */
	public List<BookingEntity> findByTenantName(String name, String host) {
		List<BookingEntity> bookingsFromTenant = bookings.findAllByUuidHost(host).filter(bookingEntity ->
				bookingEntity.getUserAccount().getLastname().equals(name)).toList();
		return bookingsFromTenant;
	}

	/**
	 * get all of the {@link BookingEntity} by the given house's name and host's ID in the system
	 *
	 * @param home muss not be {@literal null}
	 * @param host muss not be {@literal null}
	 * @return the bookingEntities of the given house's name and host's IS
	 */
	public List<BookingEntity> findByHomeName(String home, String host) {
		List<BookingEntity> bookingsFromHome = bookings.findAllByUuidHost(host).filter(bookingEntity ->
				bookingEntity.getHomeName().equals(home)).toList();
		return bookingsFromHome;
	}


}
