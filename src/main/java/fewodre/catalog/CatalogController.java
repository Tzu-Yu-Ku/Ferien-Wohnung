package fewodre.catalog;

import fewodre.catalog.events.*;
import fewodre.catalog.events.Event.EventType;
import fewodre.catalog.events.Event.*;
import fewodre.catalog.holidayhomes.*;
import fewodre.utils.Place;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
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

	CatalogController(HolidayHomeCatalog Hcatalog, EventCatalog Ecatalog, BusinessTime businessTime) {
		this.Hcatalog = Hcatalog;
		this.Ecatalog = Ecatalog;
		this.businessTime = businessTime;
	}

	@GetMapping("/holidayhomes")
	String holidayHomeCatalog(Model model) {

		model.addAttribute("holidayhomeCatalog", Hcatalog.findAll());

		return "holidayhomes";
	}

	@GetMapping("/events")
	String EventCatalog(Model model) {

		model.addAttribute("eventCatalog", Ecatalog.findAll());

		return "events";
	}

	// Weg zur addEvent-Seite --> muss noch auf der Event-Seite eingefügt werden
	@GetMapping("/addevents")
	String addEventPage() {
		return "addevent";
	}

	// fügt ein Event hinzu --> in arbeit...
	@PostMapping(path = "/addEvent")
	String addEvent(@RequestParam(value = "name", required = true) String name, @RequestParam(value = "description", required = true) String description, @RequestParam(value = "eventCompanyUuid", required = true) String eventCompanyUuid, Model model) {

		Ecatalog.save(createEvent(name, eventCompanyUuid, description, "event1.png", new Place("An der Frauenkirche", "1", "01234", "Dresden", 1, 1), true, EventType.SMALL, 5, Money.parse("EUR 3")));

		return "redirect:/events";
	}

	// erschafft ein Event
	// steht eigentlich schon genauso in der Event.java, aber muss warum auch immer damit es funktioniert hier stehen... 
	public Event createEvent(String title, String eventCompanyUuid, String description, String image, Place place, boolean eventStatus, EventType eventType, int capacity, MonetaryAmount price) {
		return new Event(title, eventCompanyUuid, description, image, place, eventStatus, eventType, capacity, price);
	}
}
