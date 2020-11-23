package fewodre.bookings;

import fewodre.useraccounts.AccountManagement;
import org.aspectj.weaver.ast.Or;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import javax.persistence.OneToOne;

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

	@PostMapping("/cart")
	public String addItem(@RequestParam("pid") Product product, @RequestParam int nummber, Cart cart){
		//if pid == HolidayHome
			//check if there is already a HolidayHome being added
			//yes ->  return false information
			//no  -> cart.addOrUpdateItem(hid, nights) return event Webseite
		//if pid == Event
			//check the event capacities
			//full -> return not bookable
			//not full -> cart.addOrUpdateItem(pid, nummber)
	}

	@GetMapping("/cart")
	public String basket(){ return "cart"; }

	@GetMapping("/removeProduct/{id}")
	String removeItem(Model model, @PathVariable("id") String id, @ModelAttribute Cart cart) {
		cart.removeItem(id);
		return "redirect:/cart";
	}

}
