package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.useraccounts.AccountEntity;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.Totalable;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.format.annotation.DateTimeFormat;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.*;
import java.time.LocalDate;
import java.time.ZoneId;


@Entity
public class BookingEntity extends Order {

	/* Attribute für Datenbankeinträge */

	@NotBlank
	private String uuidHome;

	//private ProductIdentifier homeIdentifier;

	@NotBlank
	private String homeName;

	@NotBlank
	private String uuidTenant; //? For Filtering in Repository

	@NotBlank
	private String uuidHost; //? For Filtering in Repository

	/* Attribute für extra Logik */
	@NotNull
	@DateTimeFormat(pattern = "dd.mm.yyyy")
	private LocalDate arrivalDate;

	@NotNull
	@DateTimeFormat(pattern = "dd.mm.yyyy")
	private LocalDate departureDay;

	private BookingStateEnum stateToSave;

	private transient BookingState state;


	private transient MonetaryAmount price;

	//Handy Attributes for html
	private String hostName;

	public BookingEntity(UserAccount userAccount, HolidayHome home, Quantity nights,
						 LocalDate arrivalDate, LocalDate departureDate ,
						 HashMap<Event, Integer> events, PaymentMethod paymentMethod) {
		super(userAccount, paymentMethod);
		//if(uuidHome.isBlank()){throw new NullPointerException("Blank UUID Home");}
		this.uuidHome = home.getId().getIdentifier();
		this.uuidHost = home.getHostUuid();
		this.uuidTenant = userAccount.getId().getIdentifier();
		this.arrivalDate = arrivalDate;
		this.departureDay = departureDate;
		this.homeName = home.getName();
		this.state = new BookingState(this.getDateCreated().toLocalDate(),this.arrivalDate);
		System.out.println("new State: "+this.state.toEnum());
		this.stateToSave = this.state.toEnum();
		System.out.println(stateToSave);
		Iterator<Event> iter = events.keySet().iterator();
		while(iter.hasNext()){
			Event event = iter.next();
			addOrderLine(event, Quantity.of(events.get(event)));
		}
		price = getTotal();
		//need to find out from Home
		this.hostName = "!!Mr. Test Name";
	}

	@Deprecated
	public BookingEntity(UserAccount userAccount, @NotBlank ProductIdentifier uuidHome) {
		super(userAccount);
		this.uuidHome = uuidHome.getIdentifier();
		//this.homeIdentifier = uuidHome;
	}

	@Deprecated
	public BookingEntity(AccountEntity userAccount, HolidayHome holidayHome, Quantity nights, LocalDate arrivalDate, LocalDate depatureDate, PaymentMethod paymentMethod) {
		super();
	}

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

	public MonetaryAmount getPrice() {
		if(price == null){price = getTotal();}
		//if(getTotal().isLessThan(price)){
		//	price = getTotal();
		//}
		return  getTotal();
	}

	//What i added for checking if it's availible
	public LocalDate getArrivalDate(){
		return arrivalDate;
	}
	//What i added for checking if it's availible
	public LocalDate getDepartureDate(){
		return departureDay;
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
	@Deprecated
	public boolean isNotOverlapping(LocalDate arrival, LocalDate departure){
		return !(arrival.isBefore(departureDay) && departure.isAfter(arrivalDate));
	}

	@Deprecated
	private LocalDate convertToLocalDate(java.util.Date dateToConvert) {
		return dateToConvert.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
	}

	public String getHostName() {
		return hostName;
	}

	public String getUuidHost() {
		return uuidHost;
	}

	public String getUuidHome() {
		return uuidHome;
	}

	public String getHomeName() {
		return homeName;
	}

	public BookingStateEnum getState(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		return state.toEnum();
	}

	public boolean cancel(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		if(!state.cancel()){
			throw new IllegalStateException();
		}
		else {
			System.out.println(state.toEnum());
			this.stateToSave = state.toEnum();
			//System.out.println(stateToSave);
			return true;
		}
	}

	public boolean confirm(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		if(!state.confirm()){
			throw new IllegalStateException();
		}
		else {
			stateToSave = state.toEnum();
			return true;
		}
	}

	public boolean checkTime(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		if(!state.checkTime()){
			//nothing changed
			return false;
		}
		else {
			stateToSave = state.toEnum();
			return true;
		}
	}

	public boolean pay(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		if(!state.pay()){
			throw new IllegalStateException();
		}
		else {
			stateToSave = state.toEnum();
			return true;
		}
	}
}
