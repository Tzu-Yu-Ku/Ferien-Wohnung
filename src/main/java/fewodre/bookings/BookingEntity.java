package fewodre.bookings;

import fewodre.catalog.Event;
import fewodre.catalog.HolidayHome;
import fewodre.events.EventController;
import fewodre.holidayhomes.HolidayHomeController;
import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.time.BusinessTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BookingEntity extends Order {
	/*
	Was wurde gemacht/ derzeitiger Stand:
		* grob Entwurf mit viel Pseudo Code
		* grundlegende Ideen zu Attributen
		* Funktionen in bearbeitung mit //!! gekennzeichnet
	Was muss alls nächstes gemacht werden :
		* gegebennenfalls neue Fuktionen/Attribute hinzufügen
		* DatenTypen überprüfen gegebenenfalls anpassen
		* //!! gekennzeichnete Funktionen implementieren/kontrollieren
		* An Spring/Thymleaf/HTML anpassen
	 */

	private String uuidThisOrder;
	private String uuidHome;
	private String uuidHost;
	private List<String> uuidEvents;
	private String uuidTenant;
	private Money price;
	private Order order; // for allem für den BookingState
	private BusinessTime bookingTimestamp;
	private BusinessTime  arrivalDay;
	private BusinessTime  departureDay;


	public String getUuidThisOrder() {
		return uuidThisOrder;
	}

	public void setUuidThisOrder(String uuidThisOrder) {
		this.uuidThisOrder = uuidThisOrder;
	}

	public String getUuidHome() {
		return uuidHome;
	}

	public void setUuidHome(String uuidHome) {
		this.uuidHome = uuidHome;
	}

	public  String getPhoto(){ //!!
		// return HolidayHomeController.getByUUID(uuidHome).getProfile();
	}

	public String getUuidHost() {
		return uuidHost;
	}

	public void setUuidHost(String uuidHost) {
		this.uuidHost = uuidHost;
	}

	public List<String> getUuidEvents() {
		return uuidEvents;
	}

	public void setUuidEvents(List<String> uuidEvents) {
		this.uuidEvents = uuidEvents;
	}

	public List<Event> getEvents(){ //!!
		List<Event> events = new ArrayList<Event>();
		for (String uuidEvent : uuidEvents) {
			// events.add(EventController.getByUUID(uuidEvent));
		}
		return  events;
	}

	public boolean containsEvents(){ //!!
		return !uuidEvents.isEmpty();
	}

	public String getUuidTenant() {
		return uuidTenant;
	}

	public void setUuidTenant(String uuidTenant) {
		this.uuidTenant = uuidTenant;
	}

	public Money getPrice() { //!!!
		// Money marketPrice = checkPrice();
		// if(marketPrice < price){ price = marketPrice}
		return price;
	}

	private Money checkPrice(){ //!!
		// int deltaTime = departureDay - arrivalDay
		// Money marketPrice = deltaTime * getHome.getPrice();
		// for event in uuidEvents{ marketPrice += EventManager.getByUUID(event).price();
		// return marketPrice
	}

	public HolidayHome getHome(){ //!!
		//return HollidayHomeController.getByUUID(uuidHome);
	}

	// An Tzu: Preise sollen nicht setzbar sein von außerhalb, wenn du gleicher Meinung bist kannst du es löschen
	//public void setPrice(Money price) {
	//	this.price = price;
	//}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public BusinessTime getBookingTimestamp() {
		return bookingTimestamp;
	}

	public void setBookingTimestamp(BusinessTime bookingTimestamp) {
		this.bookingTimestamp = bookingTimestamp;
	}

	public BusinessTime getArrivalDay() {
		return arrivalDay;
	}

	public void setArrivalDay(BusinessTime arrivalDay) {
		this.arrivalDay = arrivalDay;
	}

	public BusinessTime getDepartureDay() {
		return departureDay;
	}

	public void setDepartureDay(BusinessTime departureDay) {
		this.departureDay = departureDay;
	}
}
