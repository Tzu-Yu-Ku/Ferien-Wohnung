package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.catalog.events.EventCatalog;

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


	public BookingEntity createBookingEntity(UserAccount userAccount, HolidayHome home, Cart cart,
											 LocalDate arrivalDate, LocalDate departureDate,
											 HashMap<Event, Integer> events, String paymethod){
		Quantity nights = Quantity.of(ChronoUnit.DAYS.between(arrivalDate, departureDate));
		BookingEntity bookingEntity = new BookingEntity(userAccount,
				this.accounts.findByAccount_Email(home.getHostMail()),home,
				nights, arrivalDate, departureDate, events, paymethod);
		cart.addItemsTo(bookingEntity);
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
		BookingEntity result = bookings.save(bookingEntity);
		return result ;
	}

	public boolean payDeposit(BookingEntity bookingEntity){

		if(getMoney(bookingEntity.getDepositInCent()*0.01f,
				bookingEntity.getPaymethod(), bookingEntity.getUuidTenant())){
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

	public boolean cancelEvent(Event event){
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

	public int getStockCountOf(Product product){
		if(holidayHomeStorage.findByProduct(product).isEmpty()){
			return 0;
		}
		return holidayHomeStorage.findByProduct(product).get().getQuantity().getAmount().intValue();
	}

	/**
	 * Interface for the Paying-Framework of the customer.
	 * Shall return true when the transaction of the money from
	 * the tenant to the host or us was succesfull
	 * @return
	 */
	public boolean getMoney(float bill, Paymethod choosenPaymethod, String uuidTenant){
		System.out.println(uuidTenant + " paid: " + bill + "€");
		return true;
	}

	/**
	 * Interface for the Paying-Framework of the customer.
	 * Shall return true when the transaction of the money from
	 * the host or us to the tenant was succesfull
	 * @return
	 */
	public boolean giveMoney(float bill, Paymethod choosenPaymethod, String uuidTenant){
		System.out.println("depaid: " + bill + "€ to: " + uuidTenant);
		return true;
	}

	public Streamable<BookingEntity> findAll(){return bookings.findAll();}

	public Streamable<BookingEntity> findBookingsByUuidHome(ProductIdentifier holidayHome){return  bookings.findBookingsByUuidHome(holidayHome.toString());}

	public Iterable<BookingEntity> findBookingEntityByUserAccount(UserAccount userAccount){return bookings.findBookingEntityByUserAccount(userAccount);}

	public  BookingEntity findFirstByOrderIdentifier(OrderIdentifier orderIdentifier){return bookings.findFirstByOrderIdentifier(orderIdentifier);}

	public Streamable<BookingEntity> findByState(String state,String host){
		if(state.equals("ALL")){
			return bookings.findAllByUuidHost(host);
		}
		List<BookingEntity> all = bookings.findAllByUuidHost(host).toList();
		List<BookingEntity> stream = new ArrayList<>();
		all.stream().filter(bookingEntity -> bookingEntity.getState().toString().equals(state))
				.forEach(bookingEntity -> stream.add(bookingEntity));
		return Streamable.of(stream);
	}

	public List<BookingEntity> findByTenantName(String name, String host){
		List<BookingEntity> bookingsFromTenant = bookings.findAllByUuidHost(host).filter(bookingEntity ->
				bookingEntity.getUserAccount().getLastname().equals(name)).toList();
		return bookingsFromTenant;
	}

	public List<BookingEntity> findByHomeName(String home,String host){
		List<BookingEntity> bookingsFromHome = bookings.findAllByUuidHost(host).filter(bookingEntity ->
				bookingEntity.getHomeName().equals(home)).toList();
		return bookingsFromHome;
	}

}
