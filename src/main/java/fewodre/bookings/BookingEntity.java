package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.useraccounts.AccountEntity;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.ChargeLine;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.Totalable;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.format.annotation.DateTimeFormat;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalDate;
import java.time.ZoneId;


@Entity
public class BookingEntity extends Order {

	/* Attribute für Datenbankeinträge */

	@NotBlank
	private String uuidHome;

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

	@NotNull
	private Paymethod paymethod;

	private transient MonetaryAmount price;

	private int depositInCent;

	//Handy Attributes for html
	private String hostName;

	/**
	 * Creates a new {@link BookingEntity} with the given Parameters.
	 * @param userAccount
	 * @param host
	 * @param home
	 * @param nights
	 * @param arrivalDate
	 * @param departureDate
	 * @param events
	 * @param paymentMethod
	 */
	public BookingEntity(UserAccount userAccount, AccountEntity host,HolidayHome home, Quantity nights,
						 LocalDate arrivalDate, LocalDate departureDate,
						 HashMap<Event, Integer> events, String paymentMethod) {
		super(userAccount, Cash.CASH);
		this.uuidHome = home.getId().getIdentifier();
		this.uuidHost = (host==null || host.getAccount() == null|| host.getAccount().getEmail() == null) ? home.getHostMail() : host.getAccount().getEmail();
		this.uuidTenant = userAccount.getId().getIdentifier();
		this.arrivalDate = arrivalDate;
		this.departureDay = departureDate;
		this.homeName = home.getName();
		this.depositInCent = home.getPrice().multiply(ChronoUnit.DAYS.between(arrivalDate, departureDate))
				.multiply(0.1 * 100).getNumber().intValue();
		this.state = new BookingState(this.getDateCreated().toLocalDate(), this.arrivalDate);
		System.out.println("new State: " + this.state.toEnum());
		this.stateToSave = this.state.toEnum();
		System.out.println(stateToSave);
		this.paymethod = Paymethod.valueOf(paymentMethod.toUpperCase());
		System.out.println("payment method is:"+ paymethod.toString().toLowerCase());
		price = getTotal();
		//need to find out from Home
		this.hostName = host == null || host.getAccount() == null
				? " " : host.getAccount().getUsername();
	}

	@Deprecated
	public BookingEntity(UserAccount userAccount, @NotBlank ProductIdentifier uuidHome) {
		super(userAccount);
		this.uuidHome = uuidHome.getIdentifier();
	}

	@Deprecated
	public BookingEntity(AccountEntity userAccount, HolidayHome holidayHome, Quantity nights, LocalDate arrivalDate, LocalDate depatureDate, PaymentMethod paymentMethod) {
		super();
	}

	@Deprecated
	public BookingEntity() {
		super();
	}

	/**
	 * Adds a new Event to the {@link BookingEntity}.
	 *
	 * @param event
	 * @param quantity
	 * @return
	 */
	public OrderLine addEvent(Event event, Quantity quantity){
		return addOrderLine(event, quantity);
	}

	/**
	 * Adds a new Event to the {@link BookingEntity} with a default {@link Quantity} of 1.
	 *
	 * @param event
	 * @return
	 */
	public OrderLine addEvent(Event event){
		return addOrderLine(event, Quantity.of(1));
	}

	@Deprecated
	public HolidayHome getHome() {
		//!! from HollidayHomeManager
		return null;
	}

	/**
	 * Returns the complete price of the Booking as {@link MonetaryAmount}.
	 * @return
	 */
	public MonetaryAmount getPrice() {
		if(price == null){price = getTotal();} // Legacy
		return  getTotal();
	}

	/**
	 * Returns the arrival date of the booking period as {@link LocalDate}.
	 * @return
	 */
	public LocalDate getArrivalDate(){
		return arrivalDate;
	}

	/**
	 * Returns the departure date of the booking period as {@link LocalDate}.
	 * @return
	 */
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
	public boolean isNotOverlapping(LocalDate arrival, LocalDate departure) {
		return !(arrival.isBefore(departureDay) && departure.isAfter(arrivalDate));
	}

	@Deprecated
	private LocalDate convertToLocalDate(java.util.Date dateToConvert) {
		return dateToConvert.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
	}

	/**
	 * Returns the name of the host
	 *
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Returns the identifier of the host.
	 *
	 * @return
	 */
	public String getUuidHost() {
		return uuidHost;
	}

	/**
	 * Returns the identifier of the {@link HolidayHome}.
	 * @return
	 */
	public String getUuidHome() {
		return uuidHome;
	}

	/**
	 * Returns the name of the {@link HolidayHome}.
	 * @return
	 */
	public String getHomeName() {
		return homeName;
	}

	/**
	 * Returns the current {@link BookingState} of this booking.
	 * @return
	 */
	public BookingStateEnum getState(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		stateToSave = state.toEnum();
		return stateToSave;
	}

	/**
	 * Cancels the Booking.
	 * @return
	 */
	public boolean cancel(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		if(!state.cancel(this)){
			throw new IllegalStateException();
		}
		else {
			System.out.println(state.toEnum());
			this.stateToSave = state.toEnum();
			return true;
		}
	}

	/**
	 * Confirms the receipt of the deposit by the host.
	 *
	 * @return
	 */
	public boolean confirm(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		if(!state.confirm()){
			throw new IllegalStateException();
		} else {
			stateToSave = state.toEnum();
			return true;
		}
	}

	/**
	 * Checks current Time and changes State accordingly.
	 * @return
	 */
	public boolean checkTime(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		if(!state.checkTime()){
			//nothing changed
			return false;
		} else {
			stateToSave = state.toEnum();
			return true;
		}
	}

	/**
	 * Pays the booking.
	 *
	 * @return
	 */
	public boolean pay(){
		if(state == null){state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(),this.arrivalDate); }
		if(!state.pay()){
			throw new IllegalStateException();
		} else {
			stateToSave = state.toEnum();
			super.addChargeLine(Money.of(BigDecimal.valueOf(0.01 * depositInCent), "EUR").multiply(-1),
					"Anzahlung");
			return true;
		}
	}

	/**
	 * Returns the chosen payment method.
	 * @return
	 */
	public Paymethod getPaymethod() {
		return paymethod;
	}

	/**
	 * Returns the Deposit in Cents.
	 * @return
	 */
	public int getDepositInCent() {
		if (this.getState().compareTo(BookingStateEnum.ORDERED) == 0) {
			List<OrderLine> events = this.getOrderLines()
					.filter(orderLine -> !orderLine.getProductName().equals(homeName)).toList();
			events.forEach(event -> depositInCent += event.getPrice().multiply(100).getNumber().intValue());
		}
		return depositInCent;
	}

	/**
	 * Returns the rest in Cents.
	 * @return
	 */
	public int getRestInCent() {
		int restInCent = this.getTotal().multiply(100).getNumber().intValue() - depositInCent;
		return restInCent;
	}

	/**
	 * Cancels the given Event and refunds the according amount of money when necessary.
	 * @param event
	 * @return
	 */
	public boolean cancelEvent(Product event){
		List<OrderLine> lines = this.getOrderLines(event).toList();
		boolean result = lines.size() > 0;
		Iterator<ChargeLine> iter = this.getChargeLines().iterator();
		while (iter.hasNext()) {
			ChargeLine charge = iter.next();
			if (charge.getPrice().isEqualTo(Money.of(BigDecimal.valueOf(0.01 * depositInCent),
					"EUR").multiply(-1))) {
				System.out.println("Tried to remove Charge");
				this.remove(charge);
				break;
			}
		}
		for (int i = 0; i < lines.size(); i++) {
			OrderLine line = lines.get(i);
			depositInCent -= line.getPrice().getNumber().floatValue() * 100;
			this.remove(line);
		}
		this.addChargeLine(Money.of(depositInCent * (-0.01), "EUR"), "new Deposit");
		return result;
	}

	/**
	 * Returns the price of the given {@link Product} in the Booking.
	 * @param product
	 * @return
	 */
	public float getPriceOf(Product product){
		return this.getOrderLines(product).getTotal().getNumber().floatValue();
	}

	/**
	 * Reurns a list with all events of the {@link BookingEntity} which are also in the given {@link EventCatalog}.
	 * @param catalog
	 * @return
	 */
	public List<Event> getEvents(EventCatalog catalog){
		Iterator<OrderLine> orderLineIterator = this.getOrderLines().iterator();
		List<Event> events = new ArrayList<Event>();
		while (orderLineIterator.hasNext()) {
			OrderLine orderLine = orderLineIterator.next();
			Event event = catalog.findFirstByProductIdentifier(orderLine.getProductIdentifier());
			if (event != null) {
				events.add(event);
			}
		}
		return events;
	}

	/**
	 * Reurns the sum of the prices of all events of the
	 * {@link BookingEntity} which are also in the given {@link EventCatalog}.
	 * @param catalog
	 * @return
	 */
	public float getEventTotalPrice(EventCatalog catalog){
		Iterator<OrderLine> orderLineIterator = this.getOrderLines().iterator();
		float price = 0;
		while (orderLineIterator.hasNext()) {
			OrderLine orderLine = orderLineIterator.next();
			Event event = catalog.findFirstByProductIdentifier(orderLine.getProductIdentifier());
			if (event != null) {
				price += orderLine.getPrice().getNumber().floatValue();
			}
		}
		return price;
	}

	/**
	 * Returns the Identifier of the Tenant.
	 * @return
	 */
	public String getUuidTenant() {
		return uuidTenant;
	}
}
