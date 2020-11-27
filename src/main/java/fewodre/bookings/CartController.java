package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
import org.javamoney.moneta.Money;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class CartController {

	private final AccountManagement accountManagement;
	private final BookingManagement bookingManagement;
	private final EventCatalog eventcatalog;
	private final HolidayHomeCatalog holidayHomeCatalog;

	private HolidayHome holidayHome;
	private UserAccount userAccount;

	private StringFormatter formatter;

	@DateTimeFormat(pattern = "dd.mm.yyyy")
	private LocalDate arrivalDate, departureDate;


	CartController(AccountManagement accountManagement, BookingManagement bookingManagement,
				   EventCatalog eventCatalog, HolidayHomeCatalog holidayHomeCatalog) {

		Assert.notNull(accountManagement, "AccountManagement must not be null!");
		Assert.notNull(bookingManagement, "BookingManagement must not be null!");
		Assert.notNull(eventCatalog, "EventCatalog must not be null!");
		Assert.notNull(holidayHomeCatalog, "HolidayHomeCatalog must not be null!");

		this.accountManagement = accountManagement;
		this.bookingManagement = bookingManagement;
		this.eventcatalog = eventCatalog;
		this.holidayHomeCatalog = holidayHomeCatalog;
	}

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@GetMapping("/cart")
	@PreAuthorize("hasRole('TENANT')")
	public String basket(Model model, @ModelAttribute Cart cart, @LoggedIn UserAccount userAccount){
		model.addAttribute("eventCatalog", eventcatalog.findAll());
		model.addAttribute("holidayHome", holidayHome);
		model.addAttribute("arrivalDate", arrivalDate);
		model.addAttribute("departureDate", departureDate);
		this.userAccount = userAccount;
		model.addAttribute("formatter", formatter);
		model.addAttribute("account", this.userAccount);
		return "cart"; }

	@PostMapping("/cart")
	public String addHolidayHome(@RequestParam("hid") HolidayHome holidayHome, @RequestParam("arrivaldate")LocalDate startDate,
								 @RequestParam("departuredate")LocalDate endDate, @ModelAttribute Cart cart){
		this.holidayHome = holidayHome;
		this.formatter = new StringFormatter();
		//cart.addOrUpdateItem(holidayHome, Quantity.of(1));
		if(!cart.isEmpty()){ //checkt ob schon ein HolidayHome im WarenKorb liegt
			Iterator<CartItem> iter = cart.iterator();
			while(iter.hasNext()) {
				CartItem cartItem = iter.next();
				if (cartItem.getProduct().getClass() == HolidayHome.class) {
					return "redirect:/cart";
				}
			}
		}
		//if(startDate != null && endDate != null) {
			if(startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)) {
				//send message "Please choose the correct day"
				return "redirect:/housedetails";
			}

			//check if it's available
			ArrayList<BookingEntity> bookedList = new ArrayList<BookingEntity>();
			bookingManagement.findAll().filter(booking -> booking.getHome().equals(holidayHome)).forEach(item -> bookedList.add(item));
			for (BookingEntity b : bookedList) {
				//!!!!!!help!!!
				if(startDate.isBefore(b.getDepartureDate()) && endDate.isAfter(b.getArrivalDate())){
					//send message "the chosed duration is not avalible"
					return "redirect:/housedetails";
				}
			}
			this.arrivalDate = startDate;
			this.departureDate = endDate;
			Quantity interval = Quantity.of(ChronoUnit.DAYS.between(this.arrivalDate, this.departureDate));
			cart.addOrUpdateItem(holidayHome, interval);

			return "redirect:/cart";
		//} else { // Es wurde kein Zeitraum angegeben
		//	cart.addOrUpdateItem(holidayHome, Quantity.of(1));
		//	return "redirect:/cart";
		//}
	}
	@PostMapping("/defaultcart")
	public String addHolidayHome(@RequestParam("hid") HolidayHome holidayHome,@ModelAttribute Cart cart){
		LocalDate arrivalDate = LocalDate.now();
		LocalDate departureDate = arrivalDate.plusDays(2);
		return addHolidayHome(holidayHome, arrivalDate, departureDate,cart);
	}



	@PostMapping("/eventcart")
	public String addEvent(@RequestParam("eid") Event event, @RequestAttribute("joinday") LocalDate bookedDate,
						   @RequestParam("number") Quantity anzahl, Cart cart){
		if(cart.isEmpty()){
			return"redirect:/holdayhomes";
		}
		HolidayHome home;
		Iterator it = cart.iterator();
		while(it.hasNext()) {
			CartItem cartItem = (CartItem) it.next();
			if (cartItem.getProduct().getClass() == HolidayHome.class) {
				home = (HolidayHome)cartItem.getProduct();
			}
		}

		if(bookedDate.isBefore(LocalDate.now())|| bookedDate.isBefore(arrivalDate)|| bookedDate.isAfter(departureDate)){
			//send to customer "Please choose the right day"
			return "error";
		}

		// check if still available
		if(anzahl.isGreaterThan(Quantity.of(event.getCapacity())) || anzahl.isLessThan(Quantity.of(0))){
			//"Please give in a correct number"
			return"error";
		}
		// check if it's already full or anzahl > updated capacity
		else{
			cart.addOrUpdateItem(event, anzahl);
			return "rediect:/cart";
		}
	}


	@GetMapping("/removeProduct/{id}")
	public String removeItem(Model model, @PathVariable("id") String id, @ModelAttribute Cart cart) {
		cart.removeItem(id);
		return "redirect:/cart";
	}

	@GetMapping("/clear")
	public String clear(Model model, @ModelAttribute Cart cart) {
		cart.clear();
		return "redirect:/cart";
	}


	@PostMapping("/purchase")
	public String buy(@ModelAttribute Cart cart, @ModelAttribute HolidayHome holidayHome,
					  @ModelAttribute HashMap<Event, Integer> events, @LoggedIn UserAccount userAccount){
		System.out.println(cart.getPrice());
		System.out.println("Buchungszeitraum0: ");
		System.out.println(arrivalDate.toString() + " - " +departureDate.toString());
		if (bookingManagement.createBookingEntity(userAccount, holidayHome, cart, arrivalDate, departureDate, events) == null){
			return "redirect:/cart"; //es gab Probleme
		}
		return "bookingdetails"; //!!
	}

	private class StringFormatter{
		private Map<String, String> MonthTransDict = new HashMap<String, String>();
		public StringFormatter(){
			MonthTransDict.put("JANUARY", "Januar");
			MonthTransDict.put("FEBRUARY", "Februar");
			MonthTransDict.put("MARCH", "März");
			MonthTransDict.put("APRIL", "April");
			MonthTransDict.put("May", "Mai");
			MonthTransDict.put("JUNE", "Juni");
			MonthTransDict.put("JULY", "Juli");
			MonthTransDict.put("AUGUST", "August");
			MonthTransDict.put("SEPTEMBER", "September");
			MonthTransDict.put("NOVEMBER", "November");
			MonthTransDict.put("DECEMBER", "Dezember");
		}
		public String formatDate(LocalDate date){
			return date.getDayOfMonth() + ". " + MonthTransDict.get(date.getMonth().toString()) + " " + date.getYear();
		}
		public String parsePrice(MonetaryAmount Price){
			return Price.getNumber() + (Price.getCurrency().toString().equals("EUR") ? " €" : Price.getCurrency().toString());
		}
	}
}
