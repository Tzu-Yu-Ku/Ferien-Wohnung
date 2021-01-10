package fewodre.catalog;

import fewodre.catalog.events.*;
import fewodre.catalog.events.Event.EventType;
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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
		HolidayHome myHolidayHome = form.toNewHolidayHome(userAccount.getEmail());
		Hcatalog.save(myHolidayHome);
		for (int i = 0; i < Ecatalog.findAll().toList().size(); i++) {
			System.out.println(
					Ecatalog.findAll().toList().get(i).getPlace().distanceToOtherPlaces(myHolidayHome.getPlace()));
		}
		return "redirect:/holidayhomes";
	}

	// edit HolidayHome----------------
	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/editholidayhome")
	String editHolidayhomePage(@RequestParam("holidayHome") HolidayHome holidayHome, Model model) {
		model.addAttribute("holidayHome", holidayHome);
		return "editholidayhome";
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/editHolidayHome")
	String editHolidayHomes(Model model, @RequestParam("holidayHomeId") ProductIdentifier holidayHomeId,
			@RequestParam("name") String name, @RequestParam("description") String description,
			@RequestParam("price") String price, @RequestParam("capacity") String capacity,
			@RequestParam("street") String street, @RequestParam("houseNumber") String houseNumber,
			@RequestParam("city") String city, @RequestParam("postalCode") String postalCode,
			@RequestParam("coordinates_x") String coordinates_x, @RequestParam("coordinates_y") String coordinates_y) {
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
			if (!price.isBlank()) {
				int price2 = Integer.parseInt(price);
				holidayHomeToChange.setPrice(Money.of(price2, "EUR"));
			}
			if (!capacity.isBlank()) {
				int capacityHH = Integer.parseInt(capacity);
				holidayHomeToChange.setCapacity(capacityHH);
			}
			Place changedPlace = holidayHomeToChange.getPlace();
			if (!street.isBlank()) {
				changedPlace.setStreet(street);
			}
			if (!houseNumber.isBlank()) {
				changedPlace.setHouseNumber(houseNumber);
			}
			if (!postalCode.isBlank()) {
				changedPlace.setPostalCode(postalCode);
			}
			if (!city.isBlank()) {
				changedPlace.setCity(city);
			}
			if (!coordinates_x.isBlank()) {
				int coordinates_x2 = Integer.parseInt(coordinates_x);
				changedPlace.setCoordX(coordinates_x2);
			}
			if (!coordinates_y.isBlank()) {
				int coordinates_y2 = Integer.parseInt(coordinates_y);
				changedPlace.setCoordY(coordinates_y2);
			}

			holidayHomeToChange.setPlace(changedPlace);
			System.out.println(holidayHomeToChange);
			Hcatalog.save(holidayHomeToChange);

		}
		return "redirect:/holidayhomes";
	}

	// delete HolidayHome--------------
	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/deleteholidayhome")
	String deleteHolidayHome(@RequestParam("holidayHome") HolidayHome holidayHome, Model model) {

		ProductIdentifier holidayHomeId = holidayHome.getId();
		System.out.println(holidayHomeId);
		Hcatalog.findById(holidayHomeId);

		if (Hcatalog.findById(holidayHomeId).isPresent()) {
			HolidayHome holidayHomeToChange = Hcatalog.findById(holidayHomeId).get();
			holidayHomeToChange.setIsBookable(false);
			Hcatalog.save(holidayHomeToChange);
		}

		return "redirect:/holidayhomes";
	}

	// HolidayHome housedetails---------
	@PostMapping("/housedetails")
	String detail(@RequestParam("holidayHome") HolidayHome holidayHome, Model model) {
		model.addAttribute("holidayHome", holidayHome);
		return "housedetails";
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping(path = "/activateEventPage")
	String activateEventPage(Model model, @RequestParam("holidayHome") ProductIdentifier holidayHomeId) {
		System.out.println("BEREITS AKTIVE EVENTS: " + Hcatalog.findById(holidayHomeId).get().acceptedEvents);
		List<Event> nonActivtedEvents = new LinkedList<Event>();
		List<Event> allEvents = Ecatalog.findAll().toList();
		for (int i = 0; i < Ecatalog.findAll().toList().size(); i++) {
			if (!Hcatalog.findById(holidayHomeId).get().getAcctivatEvents().contains(allEvents.get(i))) {
				nonActivtedEvents.add(allEvents.get(i));
			}
		}
		model.addAttribute("nonActivatedEventCatalog", nonActivtedEvents);
		model.addAttribute("holidayHome", Hcatalog.findById(holidayHomeId).get());
		return "houseEventaccepts";
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping(path = "/activateEventForHouse")
	String activateEventForHolidayHome(Model model, @RequestParam("holidayHome") ProductIdentifier holidayHomeId,
			@RequestParam("event") ProductIdentifier eventId) {
		Hcatalog.findById(holidayHomeId).get().acceptEvent(Ecatalog.findById(eventId).get());
		Hcatalog.save(Hcatalog.findById(holidayHomeId).get());
		return "redirect:/holidayhomes";
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping(path = "/activateAllEventsForHouse")
	String activateAllEventsForHolidayHome(Model model, @RequestParam("holidayHome") ProductIdentifier holidayHomeId) {
		List<Event> nonActivtedEvents = new LinkedList<Event>();
		for (int i = 0; i < Ecatalog.findAll().toList().size(); i++) {
			if (!Hcatalog.findById(holidayHomeId).get().getAcctivatEvents()
					.contains(Ecatalog.findAll().toList().get(i))) {
				nonActivtedEvents.add(Ecatalog.findAll().toList().get(i));
			}
		}
		Hcatalog.findById(holidayHomeId).get().acceptedEvents.addAll(nonActivtedEvents);
		Hcatalog.save(Hcatalog.findById(holidayHomeId).get());
		return "redirect:/holidayhomes";
	}

	// Events------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/events")
	String EventCatalog(Model model) {
		firstname(model);
		model.addAttribute("eventCatalog", Ecatalog.findAll());

		return "events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/cancelevent")
	String cancelEvent(@RequestParam("event") Event event) {
		event.setEventStatus(false);
		Ecatalog.save(event);
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/activateevent")
	String activateEvent(@RequestParam("event") Event event) {
		event.setEventStatus(true);
		Ecatalog.save(event);
		return "redirect:/events";
	}

	// es muss noch kontrolliert werden ob das Event in einer Buchung ist
	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/deleteevent")
	String deleteEvent(@RequestParam("event") Event event) {
		System.out.println("zu löschendes Event " + event);
		if (holidayHomeStorage.findByProduct(event).isPresent()) {
			if (!event.isEventStatus()) {
				holidayHomeStorage.delete(holidayHomeStorage.findByProduct(event).get());
				Ecatalog.delete(event);
			}
		}
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/editeventpage")
	String editEventPage(@RequestParam("event") Event event, Model model) {
		model.addAttribute("event", event);
		return "editevent";
	}

	// überarbeiten...
	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/editEvent")
	String editEvent(Model model, @RequestParam("eventId") ProductIdentifier eventId, @RequestParam("name") String name,
			@RequestParam("description") String description, @RequestParam("price") String price,
			@RequestParam("date") String date, @RequestParam("time") String time,
			@RequestParam("repeats") String repeats, @RequestParam("repeateRate") String repeateRate,
			@RequestParam("capacity") String capacity, @RequestParam("street") String street,
			@RequestParam("houseNumber") String houseNumber, @RequestParam("postalCode") String postalCode,
			@RequestParam("city") String city, @RequestParam("coordinates_x") String coordinates_x,
			@RequestParam("coordinates_y") String coordinates_y) {
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
			if (!price.isBlank()) {
				int price2 = Integer.parseInt(price);
				EventToChange.setPrice(Money.of(price2, "EUR"));
			}
			if (!date.isBlank()) {
				EventToChange.setDate(LocalDate.parse(date));
			}
			if (!time.isBlank()) {
				EventToChange.setTime(LocalTime.parse(time));
			}
			if (!repeats.isBlank()) {
				int repeats2 = Integer.parseInt(repeats);
				EventToChange.setRepeats(repeats2);
				if (repeats2 > 0) {
					EventToChange.setEventType(EventType.SMALL);
				}
			}
			if (!repeateRate.isBlank()) {
				int repeateRate2 = Integer.parseInt(repeateRate);
				EventToChange.setRepeateRate(repeateRate2);
			}
			if (!capacity.isBlank()) {
				int capacity2 = Integer.parseInt(capacity);
				EventToChange.setCapacity(capacity2);
			}
			Place changedPlace = EventToChange.getPlace();
			if (!street.isBlank()) {
				changedPlace.setStreet(street);
			}
			if (!houseNumber.isBlank()) {
				changedPlace.setHouseNumber(houseNumber);
			}
			if (!postalCode.isBlank()) {
				changedPlace.setPostalCode(postalCode);
			}
			if (!city.isBlank()) {
				changedPlace.setCity(city);
			}
			if (!coordinates_x.isBlank()) {
				int coordinates_x2 = Integer.parseInt(coordinates_x);
				changedPlace.setCoordX(coordinates_x2);
			}
			if (!coordinates_y.isBlank()) {
				int coordinates_y2 = Integer.parseInt(coordinates_y);
				changedPlace.setCoordY(coordinates_y2);
			}
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
		// for (int i = 0; i < Hcatalog.findAll().toList().size(); i++) {
		// System.out.println(Hcatalog.findAll().toList().get(i).getPlace().distanceToOtherPlaces(event.getPlace()));
		// }
		return "redirect:/events";
	}

}
