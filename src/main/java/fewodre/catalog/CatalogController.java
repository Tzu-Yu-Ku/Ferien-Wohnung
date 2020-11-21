package fewodre.catalog;

import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
<<<<<<< HEAD
=======
		//model.addAttribute("holidayhomeCatalog", Hcatalog.findByCategory("home"));
		//model.addAttribute("holidayhomeCatalog", Hcatalog);
>>>>>>> 647cb9bb6093fadff7d6ba4507ed6e9af00be585

		return "itemlist";
	}

	@GetMapping("/events")
	String EventCatalog(Model model) {

		model.addAttribute("eventCatalog", Ecatalog.findAll());

		return "itemlist";
	}

}
