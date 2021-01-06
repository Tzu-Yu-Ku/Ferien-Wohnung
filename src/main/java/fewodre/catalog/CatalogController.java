package fewodre.catalog;

import fewodre.catalog.events.*;
import fewodre.catalog.holidayhomes.*;
import fewodre.useraccounts.*;

import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import fewodre.utils.Place;

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
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import javax.money.MonetaryAmount;

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

	CatalogController(HolidayHomeCatalog Hcatalog, EventCatalog Ecatalog, BusinessTime businessTime,
			UniqueInventory<UniqueInventoryItem> holidayHomeStorage, AccountManagement accountManagement,
			AccountRepository accountRepository) {
		this.Hcatalog = Hcatalog;
		this.Ecatalog = Ecatalog;
		this.businessTime = businessTime;
		this.holidayHomeStorage = holidayHomeStorage;
		this.accountManagement = accountManagement;
		this.accountRepository = accountRepository;
	}

	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!authentication.getPrincipal().equals("anonymousUser") && !authentication.getName().equals("admin")) {
			System.out.println("authentication: ");
			System.out.println(authentication.getPrincipal());
			model.addAttribute("firstname",
					accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		}
	}

	// HolidayHomes------------------------------------------------------------------------------------------------------------------------
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
	String editHolidayhomePage(@RequestParam("holidayHome") HolidayHome holidayHome, Model model) {
		System.out.println("HolidayHome welches editiert werden soll: " + holidayHome);
		model.addAttribute("holidayHome", holidayHome);
		return "editholidayhome";
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/editHolidayHome")
	String editHolidayHomes(Model model, @RequestParam("holidayHomeId") ProductIdentifier holidayHomeId, @RequestParam("name") String name,
			@RequestParam("description") String description, @RequestParam("price") int price, @RequestParam("capacity") int capacity, 
			@RequestParam("street") String street, @RequestParam("houseNumber") String houseNumber, @RequestParam("city") String city,
			@RequestParam("postalCode") String postalCode) {
		System.out.println(holidayHomeId);
		Hcatalog.findById(holidayHomeId);

		if (Hcatalog.findById(holidayHomeId).isPresent()) {
			HolidayHome holidayHomeToChange = Hcatalog.findById(holidayHomeId).get();
			if (!name.isBlank()) {
				holidayHomeToChange.setName(name);
			}
			
			if (!description.isBlank()) {
				holidayHomeToChange.setDescription(description);
			}
			if ((int) price >= 0) {
				holidayHomeToChange.setPrice(Money.of(price, "EUR"));
			}
			if (capacity >= 0) {
				holidayHomeToChange.setCapacity(capacity);
			}
			Place changedPlace = holidayHomeToChange.getPlace();
			if (!street.isBlank()) {
				changedPlace.setStreet(street);
			}
			
			if (!houseNumber.isEmpty()) {
				changedPlace.setHouseNumber(houseNumber);
			}
			/*
			if (!postalCode.isBlank()) {
				changedPlace.setPostalCode(postalCode);
			}
			*/
			holidayHomeToChange.setPlace(changedPlace);
			System.out.println(holidayHomeToChange);
			Hcatalog.save(holidayHomeToChange);

		}
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
	String detail(Model model) {
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
	String editEventPage(@RequestParam("event") Event event, Model model) {
		model.addAttribute("event", event);
		return "editevent";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/editEvent")
	String editEvent(Model model, @RequestParam("eventId") ProductIdentifier eventId, @RequestParam("name") String name,
			@RequestParam("description") String description, @RequestParam("price") int price,
			@RequestParam("date") String date, @RequestParam("time") String time, @RequestParam("repeats") int repeats,
			@RequestParam("repeateRate") int repeateRate, @RequestParam("capacity") int capacity,
			@RequestParam("street") String street, @RequestParam("houseNumber") String houseNumber,
			@RequestParam("postalCode") String postalCode, @RequestParam("city") String city) {
		System.out.println(eventId);
		Ecatalog.findById(eventId);
		if (Ecatalog.findById(eventId).isPresent()) {
			Event EventToChange = Ecatalog.findById(eventId).get();
			if (!name.isBlank()) {
				EventToChange.setName(name);
			}
			if (!description.isBlank()) {
				EventToChange.setDescription(description);
			}
			if ((int) price >= 0) {
				EventToChange.setPrice(Money.of(price, "EUR"));
			}
			if (!date.isBlank()) {
				EventToChange.setDate(LocalDate.parse(date));
			}
			if (!time.isBlank()) {
				EventToChange.setTime(LocalTime.parse(time));
			}
			if (repeats >= 0) {
				EventToChange.setRepeats(repeats);
			}
			if (repeateRate >= 0) {
				EventToChange.setRepeateRate(repeateRate);
			}
			if (capacity >= 0) {
				EventToChange.setCapacity(capacity);
			}
			Place changedPlace = EventToChange.getPlace();
			if (!street.isBlank()) {
				changedPlace.setStreet(street);
			}
			// if (!houseNumber.isEmpty()) {
			// changedPlace.setHouseNumber(houseNumber);
			// }
			// if (!postalCode.isBlank()) {
			// changedPlace.setPostalCode(postalCode);
			// }
			// if (!city.isBlank()) {
			// changedPlace.setCity(city);
			// }
			EventToChange.setPlace(changedPlace);
			System.out.println(EventToChange);
			Ecatalog.save(EventToChange);
			// holidayHomeStorage.save(UniqueInventoryItem(EventToChange,
			// EventToChange.getCapacity()));
		}
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
