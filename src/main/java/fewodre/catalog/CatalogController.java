package fewodre.catalog;

import fewodre.catalog.events.*;
import fewodre.catalog.holidayhomes.*;
import fewodre.useraccounts.*;

import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.salespointframework.catalog.ProductIdentifier;
import java.util.ArrayList;

@Controller
public class CatalogController {

	private static final Quantity NONE = Quantity.of(0);

	private final HolidayHomeCatalog Hcatalog;
	private final EventCatalog Ecatalog;
	private final BusinessTime businessTime;
	private UniqueInventory<UniqueInventoryItem> holidayHomeStorage;
	private final AccountManagement accountManagement;
	private final AccountRepository accountRepository;
	private Authentication authentication;
	ArrayList<ProductIdentifier> holidayHomeIdList = new ArrayList<ProductIdentifier>();

	CatalogController(HolidayHomeCatalog Hcatalog, EventCatalog Ecatalog, BusinessTime businessTime, UniqueInventory<UniqueInventoryItem> holidayHomeStorage, AccountManagement accountManagement, AccountRepository accountRepository) {
		this.Hcatalog = Hcatalog;
		this.Ecatalog = Ecatalog;
		this.businessTime = businessTime;
		this.holidayHomeStorage = holidayHomeStorage;
		this.accountManagement = accountManagement;
		this.accountRepository = accountRepository;
	}
	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (! authentication.getPrincipal().equals("anonymousUser") &&  ! authentication.getName().equals("admin") ) {
			System.out.println("authentication: ");
			System.out.println(authentication.getPrincipal());
			model.addAttribute("firstname", accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		}
	}
//HolidayHomes------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/holidayhomes")
	String holidayHomeCatalog(Model model) {
		firstname(model);

		model.addAttribute("holidayhomeCatalog", Hcatalog.findAll().filter(holidayHome -> holidayHome.getIsBookable()));
		return "holidayhomes";
	}

	// add HolidayHome-----------------
	@GetMapping("/addholidayhome")
	String addHolidayhomePage(Model model) {
		firstname(model);
		return "addholidayhome";
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping(path = "/addHolidayHome")
	String addHolidayHomes(@ModelAttribute("form") HolidayHomeForm form, Model model,
			@LoggedIn UserAccount userAccount) {

		Hcatalog.save(form.toNewHolidayHome(userAccount.getEmail()));

		return "redirect:/holidayhomes";
	}

	// edit HolidayHome----------------
	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/editholidayhome")
	String editHolidayhomePage(@RequestParam("holidayHome") HolidayHome holidayHome) {
		System.out.println("HolidayHome welches editiert werden soll: " + holidayHome);

		return "editholidayhome";
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/editHolidayHome")
	String editHolidayHomes(@RequestParam("holidayHome") HolidayHome holidayHome, Model model, String name, String description, String price, String capacity, String street, String number, String city, String postalnumber) {
		System.out.println("Name der neu eingefügt werden soll '" + name + "'");
		holidayHome.setDescription(name);

		//System.out.println(versuch.getName());
		return "redirect:/holidayhomes";
	}

	// delete HolidayHome--------------
	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/deleteholidayhome")
	String deleteHolidayHome(@RequestParam("holidayHome") HolidayHome holidayHome) {
		System.out.println("Zu löschende HolidayHome: " + holidayHome);
		holidayHome.setIsBookable(false);
		System.out.println("isBookable ist nun auf '" + holidayHome.getIsBookable() + "' gesetzt");
		return "redirect:/holidayhomes";
	}

	// HolidayHome housedetails---------
	@GetMapping("/housedetails")
	String detail(Model model){
		firstname(model);
		return "housedetails";
	}



// Events------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/events")
	String EventCatalog(Model model) {
		firstname(model);
		model.addAttribute("eventCatalog", Ecatalog.findAll());

		return "events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/deleteevent")
	String deleteEvent(@RequestParam("event") Event event) {
		System.out.println(event);
		if (holidayHomeStorage.findByProduct(event).isPresent()) {
			holidayHomeStorage.delete(holidayHomeStorage.findByProduct(event).get());
			Ecatalog.delete(event);
		}
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/editeventpage")
	String editEventPage(@RequestParam("event") Event event) {
		System.out.println(event);
		return "editevent";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/editEvent")
	String editEvent(@RequestParam("event") Event event, @ModelAttribute("form") EventForm form, Model model) {
		form.editEvent(event);
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@GetMapping("/addevents")
	String addEventPage(Model model) {
		firstname(model);
		return "addevent";
	}

	@PostMapping(path = "/addEvent")
	String addEvent(@LoggedIn UserAccount userAccount, @ModelAttribute("form") EventForm form, Model model) {
		System.out.println(userAccount.getId().getIdentifier());
		Event event = form.toNewEvent(userAccount.getId().getIdentifier());
		Ecatalog.save(event);
		holidayHomeStorage.save(new UniqueInventoryItem(event, Quantity.of(event.getCapacity())));
		return "redirect:/events";
	}
}
