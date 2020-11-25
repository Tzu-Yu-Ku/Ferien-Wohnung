package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
import org.aspectj.weaver.ast.Or;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import javax.persistence.OneToOne;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;

@Controller
@SessionAttributes("cart")
@PreAuthorize("isAuthenticated()")
public class CartController {

	private final AccountManagement userManagement;
	private final BookingManagement bookingManagement;
	private LocalDate arrivalDate, depatureDate;

	CartController(AccountManagement userManagement, BookingManagement bookingManagement) {
		Assert.notNull(userManagement, "UserManagement must not be null!");
		Assert.notNull(bookingManagement);

		this.userManagement = userManagement;
		this.bookingManagement = bookingManagement;
	}

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@PostMapping("/homecart")
	public String addHolidayHome(@RequestParam("hid") HolidayHome holidayHome, @RequestParam LocalDate startDate,
								 @RequestParam LocalDate endDate, Cart cart){

		if(!cart.isEmpty()){
			Iterator it = cart.iterator();
			while(it.hasNext()) {
				CartItem cartItem = (CartItem) it.next();
				if (cartItem.getProduct().getClass() == HolidayHome.class) {
					return "redirect:/cart";
				}
			}
		}else{
			if(startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)) {

				//send message "Please choose the correct day"
				return "redirect:/housedetails";
			}
		}
		//check if it's available
		ArrayList<BookingEntity> bookedList = new ArrayList<BookingEntity>();
		bookingManagement.findAll().filter(booking -> booking.getHome().equals(holidayHome)).forEach(item ->bookedList.add(item));
		for(BookingEntity b: bookedList){
			//!!!!!!help!!!
			if(endDate.isAfter(b.getArrivalDate())||startDate.isBefore(b.getDepartureDay())||
					startDate.isBefore(b.getArrivalDate()) && endDate.isAfter(b.getDepartureDay())){
				//send message "the chosed duration is not avalible"
				return "redirect:/housedetails";
			}
		}
		Quantity interval = Quantity.of(ChronoUnit.DAYS.between(startDate, endDate));
		this.arrivalDate = startDate;
		this.depatureDate = endDate;
		cart.addOrUpdateItem(holidayHome, interval);

		return "cart";
	}



	@PostMapping("/eventcart")
	public String addEvent(@RequestParam("eid") Event event, LocalDate bookDate, Quantity anzahl, Cart cart){
		if(cart.isEmpty()){
			return"redirect:/itemlist";
		}
		HolidayHome home;
		Iterator it = cart.iterator();
		while(it.hasNext()) {
			CartItem cartItem = (CartItem) it.next();
			if (cartItem.getProduct().getClass() == HolidayHome.class) {
				home = (HolidayHome)cartItem.getProduct();
			}
		}

		if(bookDate.isBefore(LocalDate.now())|| bookDate.isBefore(arrivalDate)|| bookDate.isAfter(depatureDate)){
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

	@GetMapping("/cart")
	public String basket(){ return "cart"; }

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

}
