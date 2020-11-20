package fewodre.catalog;

import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CatalogController {

	private static final Quantity NONE = Quantity.of(0);

	private final HolidayHomeEventCatalog catalog;
	private final BusinessTime businessTime;

	CatalogController(HolidayHomeEventCatalog catalog, BusinessTime businessTime) {
		this.catalog = catalog;
		this.businessTime = businessTime;
	}

	@GetMapping("/holidayhomes")
	String holidayHomeCatalog(Model model) {

		model.addAttribute("holidayhomeCatalog", catalog.findByCategory("home"));

		return "itemlist";
	}

}
