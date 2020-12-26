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
import java.util.HashMap;


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
											 /*PaymentMethod paymentMethod,*/ LocalDate arrivalDate,
											 LocalDate departureDate, HashMap<Event, Integer> events){
		Quantity nights = Quantity.of(ChronoUnit.DAYS.between(arrivalDate, departureDate));
		//HolidayHome home = catalog.findFirstByProductIdentifier(uuidHome);
		BookingEntity bookingEntity = new BookingEntity(userAccount, this.accounts.findByAccount_Email(home.getHostUuid()),home, nights, arrivalDate, departureDate, events, Cash.CASH);
		cart.addItemsTo(bookingEntity);
		//order open()
		//will update quantity one time
		//cart.getItem(home.getId().toString());
		if(!holidayHomeStorage.findByProduct(home).isPresent()){
			holidayHomeStorage.save(new UniqueInventoryItem(home, nights.add(nights)));
		}else{
			holidayHomeStorage.findByProduct(home).get().increaseQuantity(nights.add(nights));
		}

		cart.clear();
		System.out.println("cart is empty: "+cart.isEmpty());
		BookingEntity result = bookings.save(bookingEntity);
		System.out.println(result == bookingEntity);
		return result ;
	}

	public boolean pay(BookingEntity bookingEntity){
		if(orderManagement.payOrder(bookingEntity)){
			orderManagement.completeOrder(bookingEntity);
			return bookingEntity.pay();
		}
		return false;
	}

	public int getStockCountOf(Product product){
		if(holidayHomeStorage.findByProduct(product).isEmpty()){
			return 0;
		}
		return holidayHomeStorage.findByProduct(product).get().getQuantity().getAmount().intValue();
	}

	public Streamable<BookingEntity> findAll(){return bookings.findAll();}

	//after createBookingEntity, we can already save in bookingRepository
	//need to create findByStatusPaid

	public Streamable<BookingEntity> findBookingsByUuidHome(ProductIdentifier holidayHome){return  bookings.findBookingsByUuidHome(holidayHome.toString());}

	public Iterable<BookingEntity> findBookingEntityByUserAccount(UserAccount userAccount){return bookings.findBookingEntityByUserAccount(userAccount);}

	public  BookingEntity findFirstByOrderIdentifier(OrderIdentifier orderIdentifier){return bookings.findFirstByOrderIdentifier(orderIdentifier);}
}
