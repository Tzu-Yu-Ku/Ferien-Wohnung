package fewodre.bookings;

import fewodre.catalog.Event;
import fewodre.catalog.HolidayHome;
import fewodre.events.EventController;
import fewodre.holidayhomes.HolidayHomeController;
import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BookingEntity extends Order {

	@NotBlank
	private String uuidHome;

	@ElementCollection
	private List<String> uuidEvents;

	public BookingEntity(UserAccount userAccount, @NotBlank String uuidHome, PaymentMethod paymentMethod) {
		super(userAccount, paymentMethod);
		//if(uuidHome.isBlank()){throw new NullPointerException("Blank UUID Home");}
		this.uuidHome = uuidHome;
	}

	public BookingEntity(UserAccount userAccount, @NotBlank String uuidHome) {
		super(userAccount);
		this.uuidHome = uuidHome;
	}

	@Deprecated
	public BookingEntity() {
		super();
	}

	public OrderLine addEvent(Event event, Quantity quantity){
		return addOrderLine(event, quantity);
	}

	public OrderLine addEvent(Event event){
		return addOrderLine(event, Quantity.of(1));
	}

}
