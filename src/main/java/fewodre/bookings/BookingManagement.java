package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.catalog.events.EventCatalog;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional
public class BookingManagement {

	private final BookingRepository bookings;
	private final OrderManagement orderManagement;
	private final HolidayHomeCatalog homeCatalog;
	private final EventCatalog eventCatalog;
	private final UniqueInventory<UniqueInventoryItem> holidayHomeStorage;

	@Autowired
	BookingManagement(BookingRepository bookings, OrderManagement<Order> orderManagement,
					  HolidayHomeCatalog homeCatalog, EventCatalog eventCatalog, UniqueInventory<UniqueInventoryItem> holidayHomeStorage){

		Assert.notNull(bookings, "BookingRepository must not be null!");
		Assert.notNull(orderManagement, "OrderManagement must not be null!");
		Assert.notNull(homeCatalog, "HomeCatalog Must not be Null!");
		Assert.notNull(eventCatalog, "EventCatalog Must not be Null!");

		this.bookings = bookings;
		this.orderManagement = orderManagement;
		this.homeCatalog = homeCatalog;
		this.eventCatalog = eventCatalog;
		this.holidayHomeStorage = holidayHomeStorage;
	}


	public BookingEntity createBookingEntity(UserAccount userAccount, HolidayHome home, Cart cart,
											 /*PaymentMethod paymentMethod,*/ LocalDate arrivalDate,
											 LocalDate departureDate, HashMap<Event, Integer> events){
		Quantity nights = Quantity.of(ChronoUnit.DAYS.between(arrivalDate, departureDate));
		//HolidayHome home = catalog.findFirstByProductIdentifier(uuidHome);
		BookingEntity bookingEntity = new BookingEntity(userAccount, home, nights, arrivalDate, departureDate, events, Cash.CASH);
		cart.addItemsTo(bookingEntity);
		//order open()
		//will update quantity one time
		//cart.getItem(home.getId().toString());
		holidayHomeStorage.save(new UniqueInventoryItem(home, nights.add(nights)));

		orderManagement.payOrder(bookingEntity);
		//try {
		//update quantity one more time here (don't understand what exactly in completeOrder do)
		//but this should be at the other place (Host should aprrove)
			orderManagement.completeOrder(bookingEntity);
			cart.clear();
		/*} catch (Exception e){
			System.out.println("Buchungszeitraum: ");
			System.out.println(arrivalDate.toString() + " - " +departureDate.toString());
			return null;
		}*/
		return  bookings.save(bookingEntity);
	}

	public Streamable<BookingEntity> findAll(){return bookings.findAll();}

	//after createBookingEntity, we can already save in bookingRepository
	//need to create findByStatusPaid


}
