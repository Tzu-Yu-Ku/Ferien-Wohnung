package fewodre.catalog;

import fewodre.catalog.events.*;
import fewodre.catalog.events.Event.EventType;
import fewodre.catalog.events.Event.*;
import fewodre.catalog.holidayhomes.*;
import fewodre.utils.Place;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogController {

	private static final Quantity NONE = Quantity.of(0);

	private final HolidayHomeCatalog Hcatalog;
	private final EventCatalog Ecatalog;
	private final BusinessTime businessTime;
	private UniqueInventory<UniqueInventoryItem> holidayHomeStorage;

	CatalogController(HolidayHomeCatalog Hcatalog, EventCatalog Ecatalog, BusinessTime businessTime, UniqueInventory<UniqueInventoryItem> holidayHomeStorage) {
		this.Hcatalog = Hcatalog;
		this.Ecatalog = Ecatalog;
		this.businessTime = businessTime;
		this.holidayHomeStorage = holidayHomeStorage;
	}

	@GetMapping("/holidayhomes")
	String holidayHomeCatalog(Model model) {

		model.addAttribute("holidayhomeCatalog", Hcatalog.findAll());

		return "holidayhomes";
	}

	@GetMapping("/addholidayhome")
	String addHolidayhomePage() {
		return "addholidayhome";
	}

	@PostMapping(path = "/addHolidayHome")
	String addHolidayhomes(@ModelAttribute("form") HolidayHomeForm form, Model model) {

		Hcatalog.save(form.toNewHolidayHome());

		return "redirect:/holidayhomes";
	}



	@GetMapping("/events")
	String EventCatalog(Model model) {

		model.addAttribute("eventCatalog", Ecatalog.findAll());

		return "events";
	}

	// Weg zur addEvent-Seite --> muss noch auf der Event-Seite eingefügt werden
	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@GetMapping("/addevents")
	String addEventPage() {
		return "addevent";
	}

	@PostMapping(path = "/addEvent")
	String addEvent(@ModelAttribute("form") EventForm form, Model model) {

		Event event = form.toNewEvent();
		Ecatalog.save(event);
		holidayHomeStorage.save(new UniqueInventoryItem(event, Quantity.of(event.getCapacity())));

		return "redirect:/events";
	}

	@GetMapping("/housedetails")
	String detail(){
		return "housedetails";
	}
}
