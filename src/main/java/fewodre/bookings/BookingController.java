package fewodre.bookings;


import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Controller
@PreAuthorize("isAuthenticated()")
public class BookingController {

	@NotNull(message = "HolidayHomeCatalog muss not be null!")
	private final HolidayHomeCatalog holidayHomeCatalog;

	@NotNull(message = "EventCatalog muss not be null!")
	private final EventCatalog eventCatalog;

	@NotNull(message = "AccountManagement muss not be null!")
	private final AccountManagement accountManagement;

	@NotNull(message = "BookingRepository muss not be null!")
	private final BookingManagement bookingManagement;



	private HashMap<Event, Integer> events= new HashMap<Event, Integer>();

	BookingController(HolidayHomeCatalog holidayHomeCatalog, EventCatalog eventCatalog,
					  AccountManagement accountManagement, BookingManagement bookingManagement){
		this.holidayHomeCatalog = holidayHomeCatalog;
		this.eventCatalog = eventCatalog;
		this.accountManagement = accountManagement;
		this.bookingManagement = bookingManagement;
	}


	@PostMapping("/cart")
	String addBooking(@LoggedIn UserAccount user, @RequestParam("hid")HolidayHome home){
		PaymentMethod paymentMethod = Cash.CASH;
		LocalDate arrivalDate = LocalDate.now();
		LocalDate depatureDate = arrivalDate.plusDays(1);
		bookingManagement.createBookingEntity(user, home, paymentMethod, arrivalDate, depatureDate);
		return "redirect:/cart";
	}

	@GetMapping("/cart")
	String printCart(Model model,@LoggedIn UserAccount user/*,BookingEntity bookingEntity*/){
		//model.addAttribute("booking",bookingRepository.findById(bookingEntity.getId()));
		model.addAttribute("user", accountManagement.findUserByAccount(user));
		model.addAttribute("eventCatalog",eventCatalog.findAll());
		model.addAttribute("date", LocalDate.now());
		return "cart";
	}

	@PostMapping("/eventcart")
	String addEvent(@RequestParam("eid") Event event, @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate bookedDate,
					@RequestParam("number") Quantity anzahl){

	}

}
