package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.holidayhomes.HolidayHome;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Entity
public class BookingEntity extends Order {

	@NotBlank
	private String uuidHome;

	@NotBlank
	private String uuidTenant; //? For Filtering in Repository

	@NotBlank
	private String uuidHost; //? For Filtering in Repository

	@ElementCollection
	private List<String> uuidEvents;


	private Date arrivalDate;
	private Date departureDay;

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

	@Deprecated
	public  HolidayHome getHome(){
		//!! from HollidayHomeManager
		return null;
	}

	/**
	 * Überprüft ob der übergebene Zeitraum sich nicht mit dem Zeitraum dieser Buchung überlappt
	 * Wenn nicht gibt wahr zurück ansonsten falsch.
	 * Sollte sich nur Anreise des neuen Mieters und Abreise des alten Mieters überlappen
	 * wird dennoch wahr zurückgegeben denn es wird angenommen (der Norm entsprechen) das es einen genauen
	 * Auscheckzeit gibt die immer vor der Eincheckzeit liegt.
	 *
	 * @param arrival
	 * @param departure
	 * @return
	 */
	public boolean isNotOverlapping(Date arrival, Date departure){
		return !(arrival.before(departureDay) && departure.after(arrivalDate));
	}


}
