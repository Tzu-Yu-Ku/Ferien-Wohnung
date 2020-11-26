package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
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

	@DateTimeFormat(pattern = "dd.mm.yyyy")
	private LocalDate arrivalDate, depatureDate;


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
		model.addAttribute("departureDate", depatureDate);
		this.userAccount = userAccount;
		//if(userAccount.getAccount().getFirstname().isBlank()){this.userAccount.getAccount().setFirstname("Mr.");}
		//if(userAccount==null){System.out.println("!!!!");}
		//else{System.out.println("??????");}
		model.addAttribute("account", this.userAccount);
		return "cart"; }

	@PostMapping("/cart")
	public String addHolidayHome(@RequestParam("hid") HolidayHome holidayHome, @RequestParam("arrivaldate")LocalDate startDate,
								 @RequestParam("depaturedate")LocalDate endDate, @ModelAttribute Cart cart){
		this.holidayHome = holidayHome;
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
			this.depatureDate = endDate;
			Quantity interval = Quantity.of(ChronoUnit.DAYS.between(this.arrivalDate, this.depatureDate));
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
		LocalDate depatureDate = arrivalDate.plusDays(2);
		return addHolidayHome(holidayHome, arrivalDate, depatureDate,cart);
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

		if(bookedDate.isBefore(LocalDate.now())|| bookedDate.isBefore(arrivalDate)|| bookedDate.isAfter(depatureDate)){
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


	// i am too tired to do this now!!! it's 2:30!!!!!!!
	@PostMapping("/purchase")
	public String buy(@ModelAttribute Cart cart, @LoggedIn Optional<AccountEntity> userAccount){

		return "default"; //!!
	}

	//private class CartInformation{
	//	public CartInformation()
	//}
}

