package fewodre.bookings;

import fewodre.useraccounts.AccountManagement;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("isAuthenticated()")
public class BookingController {

	private final BookingRepository bookingRepository;
	private final AccountManagement accountManagement;
	private UserAccount userAccount;
	private StringFormatter formatter;

	public BookingController(AccountManagement accountManagement,BookingRepository bookingRepository){
		Assert.notNull(accountManagement, "AccountManagement must not be null!");
		Assert.notNull(bookingRepository, "BookingRepository must not be null!");
		this.accountManagement = accountManagement;
		this.bookingRepository = bookingRepository;
		this.formatter = new StringFormatter();
	}

	@GetMapping("/bookings")
	@PreAuthorize("hasRole('TENANT')")
	public String bookings(Model model, @LoggedIn UserAccount userAccount){
		if(!bookingRepository.findBookingEntityByUserAccount(userAccount).iterator().hasNext()){
			return "redirect:/holidayhomes";
		}else {
			model.addAttribute("bookings", bookingRepository.findBookingEntityByUserAccount(userAccount));
			model.addAttribute("formatter", formatter);
			return "bookings";
		}
	}

}
