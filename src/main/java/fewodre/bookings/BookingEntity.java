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

	@NotNull
	private Paymethod paymethod;

	private transient MonetaryAmount price;

	private int depositInCent;

	//Handy Attributes for html
	private String hostName;

	public BookingEntity(UserAccount userAccount, AccountEntity host, HolidayHome home, Quantity nights,
	                     LocalDate arrivalDate, LocalDate departureDate,
	                     HashMap<Event, Integer> events, String paymentMethod) {
		super(userAccount, Cash.CASH);
		//if(uuidHome.isBlank()){throw new NullPointerException("Blank UUID Home");}
		this.uuidHome = Objects.requireNonNull(home.getId()).getIdentifier();
		this.uuidHost = host == null || host.getAccount() == null ? home.getHostMail() : host.getAccount().getEmail();
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
		System.out.println("payment method is:" + paymethod.toString().toLowerCase());
		/*
		Iterator<Event> iter = events.keySet().iterator();
		while(iter.hasNext()){
			Event event = iter.next();
			addOrderLine(event, Quantity.of(events.get(event)));
			depositInCent += event.getPrice().multiply(events.get(event)).multiply(100).getNumber().intValue();
		}

		 */
		price = getTotal();
		//need to find out from Home
		this.hostName = host == null || host.getAccount() == null
				? " " : host.getAccount().getUsername();
	}

	public BookingEntity(UserAccount userAccount, @NotBlank ProductIdentifier uuidHome) {
		super(userAccount);
		this.uuidHome = uuidHome.getIdentifier();
		//this.homeIdentifier = uuidHome;
	}

	public BookingEntity(AccountEntity userAccount, HolidayHome holidayHome, Quantity nights, LocalDate arrivalDate,
	                     LocalDate depatureDate, PaymentMethod paymentMethod) {
		super();
	}

	public BookingEntity() {
		super();
	}

	public OrderLine addEvent(Event event, Quantity quantity) {
		return addOrderLine(event, quantity);
	}

	public OrderLine addEvent(Event event) {
		return addOrderLine(event, Quantity.of(1));
	}

	@Deprecated
	public HolidayHome getHome() {
		//!! from HollidayHomeManager
		return null;
	}

	public MonetaryAmount getPrice() {
		if (price == null) {
			price = getTotal();
		}
		//if(getTotal().isLessThan(price)){
		//	price = getTotal();
		//}
		return getTotal();
	}

	//What i added for checking if it's availible
	public LocalDate getArrivalDate() {
		return arrivalDate;
	}

	//What i added for checking if it's availible
	public LocalDate getDepartureDate() {
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

	public BookingStateEnum getState() {
		if (state == null) {
			state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(), this.arrivalDate);
		}
		stateToSave = state.toEnum();
		return stateToSave;
	}

	public boolean cancel() {
		if (state == null) {
			state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(), this.arrivalDate);
		}
		if (!state.cancel(this)) {
			throw new IllegalStateException();
		} else {
			System.out.println(state.toEnum());
			this.stateToSave = state.toEnum();
			//System.out.println(stateToSave);
			return true;
		}
	}

	public boolean confirm() {
		if (state == null) {
			state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(), this.arrivalDate);
		}
		if (!state.confirm()) {
			throw new IllegalStateException();
		} else {
			stateToSave = state.toEnum();
			return true;
		}
	}

	public boolean checkTime() {
		if (state == null) {
			state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(), this.arrivalDate);
		}
		if (!state.checkTime()) {
			//nothing changed
			return false;
		} else {
			stateToSave = state.toEnum();
			return true;
		}
	}

	public boolean pay() {
		if (state == null) {
			state = new BookingState(stateToSave, this.getDateCreated().toLocalDate(), this.arrivalDate);
		}
		if (!state.pay()) {
			throw new IllegalStateException();
		} else {
			stateToSave = state.toEnum();
			super.addChargeLine(Money.of(BigDecimal.valueOf(0.01 * depositInCent), "EUR").multiply(-1),
					"Anzahlung");
			return true;
		}
	}

	public Paymethod getPaymethod() {
		return paymethod;
	}

	public int getDepositInCent() {
		if (this.getState().compareTo(BookingStateEnum.ORDERED) == 0) {
			List<OrderLine> events = this.getOrderLines()
					.filter(orderLine -> !orderLine.getProductName().equals(homeName)).toList();
			events.forEach(event -> depositInCent += event.getPrice().multiply(100).getNumber().intValue());
		}
		return depositInCent;
	}

	public boolean cancelEvent(Product event) {
		List<OrderLine> lines = this.getOrderLines(event).toList();
		boolean result = lines.size() > 0;
		Iterator<ChargeLine> iter = this.getChargeLines().iterator();
		while (iter.hasNext()) {
			ChargeLine charge = iter.next();
			;
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

	public float getPriceOf(Product product) {
		return this.getOrderLines(product).getTotal().getNumber().floatValue();
	}

	public List<Event> getEvents(EventCatalog catalog) {
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

	public float getEventTotalPrice(EventCatalog catalog) {
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

	public String getUuidTenant() {
		return uuidTenant;
	}
}
