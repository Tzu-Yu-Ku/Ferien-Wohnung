package fewodre.bookings;

import fewodre.catalog.Event;
import fewodre.catalog.HolidayHome;
import fewodre.catalog.HolidayHomeEventCatalog;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class BookingManagement {

	private final BookingRepository bookings;
	private final OrderManagement orderManagement;
	private final HolidayHomeEventCatalog catalog;

	@Autowired
	BookingManagement(BookingRepository bookings, OrderManagement orderManagement, HolidayHomeEventCatalog catalog){

		Assert.notNull(bookings, "BookingRepository must not be null!");
		Assert.notNull(orderManagement, "OrderManagement must not be null!");
		Assert.notNull(catalog, "Catalog Must not be Null!");

		this.bookings = bookings;
		this.orderManagement = orderManagement;
		this.catalog = catalog;
	}

	public BookingEntity createBookingEntity(UserAccount userAccount, HolidayHome home,
											 PaymentMethod paymentMethod, LocalDate arrivalDate,
											 LocalDate departureDate, HashMap<Event, Integer> events){
		Quantity nights = Quantity.of(ChronoUnit.DAYS.between(arrivalDate, departureDate));
		//HolidayHome home = catalog.findFirstByProductIdentifier(uuidHome);
		return  bookings.save(new BookingEntity(userAccount, home, nights, arrivalDate, departureDate, events, paymentMethod));
	}

}
