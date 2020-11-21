package fewodre.catalog;

import fewodre.utils.Place;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final HolidayHomeCatalog holidayHomeCatalog;

	CatalogDataInitializer(HolidayHomeCatalog holidayHomeCatalog) {
		this.holidayHomeCatalog = holidayHomeCatalog;
	}

	@Override
	public void initialize() {

		if (holidayHomeCatalog.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");
		HolidayHome test = new HolidayHome();
		HolidayHome test2 = new HolidayHome();
		test.setName("Villa Kunterbund");
		test.setDescription("Einfach nice.");
		test.setPlace(new Place("An der Goldgrube", "1", "01099", "Dresden", 1, 1));
		test.setBookable(true);
		test.setHostUuid("abcd-efgh-jkli");
		test.setImage("house2.png");

		test2.setName("Schloss am Wasserfall");
		test2.setDescription("Geiles plätschern");
		test2.setPlace(new Place("Am Wasserfall", "1", "01099", "Dresden", 1, 1));
		test2.setBookable(true);
		test2.setHostUuid("abcd-efgh-jkli");
		test2.setImage("house2.png");
		holidayHomeCatalog.save(test);
		holidayHomeCatalog.save(test2);

<<<<<<< HEAD
		Event eventTest = new Event();
		eventTest.setName("Villa Kunterbund");
		eventTest.setDescription("Einfach nice.");
		eventTest.setPlace(new Place("An der Goldgrube", "1", "01099", "Dresden", 1, 1));
		EventCatalog.save(eventTest);
=======
		
>>>>>>> 647cb9bb6093fadff7d6ba4507ed6e9af00be585
	}
}
