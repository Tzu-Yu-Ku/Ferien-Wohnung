package fewodre.bookings;

import fewodre.catalog.Event;
import fewodre.catalog.HolidayHome;
import fewodre.events.EventEntity;
import fewodre.holidayhomes.HolidayHomeEntity;
import fewodre.useraccounts.AccountManagement;
import org.aspectj.weaver.ast.Or;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.quantity.Quantity;
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
import java.util.Date;
import java.util.Iterator;

@Controller
@SessionAttributes("cart")
@PreAuthorize("isAuthenticated()")
public class CartController {

	private final AccountManagement userManagement;

	CartController(AccountManagement userManagement) {
		Assert.notNull(userManagement, "UserManagement must not be null!");
		this.userManagement = userManagement;

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
					return "redirect:/error";
				}
			}
		}else{
			if(startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)) {
				//send message "Please choose the correct day"
				return "redirect:/housedetails";
			}
		}
		//check if it's availble

		long interval = startDate.until(endDate, ChronoUnit.DAYS);
		cart.addOrUpdateItem(holidayHome, interval);

		//not confirm jet.
		return "redirect:/events";
	}

	// how to check if there

	@PostMapping("/eventcart")
	public String addEvent(@RequestParam("eid") Event event, LocalDate bookDate, Quantity anzahl, Cart cart){
		if(bookDate.isBefore(LocalDate.now())){
			//send to customer "Please choose the right day"
			return "error";
		}

		//check if the date is exclusive from the booked HoolidayHome date
		//                updated Capacity
		if(anzahl > event.getCapacity()|| anzahl < 0){
			//"Please give in a correct nummber"
			return"error";
		}
		// check if it's already full or anzahl > updated capacity
		else{
			cart.addOrUpdateItem(event, anzahl);
			return "rediect:/event";
		}
	}

	@GetMapping("/cart")
	public String basket(){ return "cart"; }

	@GetMapping("/removeProduct/{id}")
	String removeItem(Model model, @PathVariable("id") String id, @ModelAttribute Cart cart) {
		cart.removeItem(id);
		return "redirect:/cart";
	}

}
