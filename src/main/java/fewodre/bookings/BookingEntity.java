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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Entity
public class BookingEntity extends Order {

	/* Attribute für Datenbankeinträge */

	@NotBlank
	private String uuidHome;

	@NotBlank
	private String uuidTenant; //? For Filtering in Repository

	@NotBlank
	private String uuidHost; //? For Filtering in Repository

	@ElementCollection
	private List<String> uuidEvents;

	/* Attribute für extra Logik */

	private LocalDate arrivalDate;
	private LocalDate departureDay;

	public BookingEntity(UserAccount userAccount, @NotBlank String uuidHome, PaymentMethod paymentMethod) {
		super(userAccount, paymentMethod);
		//if(uuidHome.isBlank()){throw new NullPointerException("Blank UUID Home");}
		this.uuidHome = uuidHome;
		// hollidayHome home = GetBy(uuidHome)
		//addOrderLine(home, arrivalDate.);
		//ChronoUnit.DAYS.between(arrivalDate, departureDay);
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
	public boolean isNotOverlapping(LocalDate arrival, LocalDate departure){
		return !(arrival.isBefore(departureDay) && departure.isAfter(arrivalDate));
	}

	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
	}


}
