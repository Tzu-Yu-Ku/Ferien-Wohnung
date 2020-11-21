package fewodre.bookings;

import org.salespointframework.order.OrderManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class BookingManagement {

	private final BookingRepository bookings;
	private final OrderManagement orderManagement;

	@Autowired
	BookingManagement(BookingRepository bookings, OrderManagement orderManagement){

		Assert.notNull(bookings, "BookingRepository must not be null!");
		Assert.notNull(orderManagement, "OrderManagement must not be null!");

		this.bookings = bookings;
		this.orderManagement = orderManagement;
	}

}
