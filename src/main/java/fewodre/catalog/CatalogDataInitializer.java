package fewodre.catalog;

import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.utils.Place;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final HolidayHomeEventCatalog holidayHomeEventCatalog;

	CatalogDataInitializer(HolidayHomeEventCatalog holidayHomeEventCatalog) {
		this.holidayHomeEventCatalog = holidayHomeEventCatalog;
	}

	@Override
	public void initialize() {

		if (holidayHomeEventCatalog.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");
		HolidayHome test = new HolidayHome();
		HolidayHome test2 = new HolidayHome();
		test.setName("Anus");
		test.setDescription("Haha");
		test.setPlace(new Place("straße", "nr", "plz", "dresden", 1, 1));
		test.setBookable(true);
		test.setHostUuid("abcd-efgh-jkli");
		test.setImage("house2.png");

		test2.setName("Anusa");
		test2.setDescription("Hahaaaa");
		test2.setPlace(new Place("straße", "nr", "plz", "dresden", 1, 1));
		test2.setBookable(true);
		test2.setHostUuid("abcd-efgh-jkli");
		test2.setImage("house2.png");
		holidayHomeEventCatalog.save(test);
		holidayHomeEventCatalog.save(test2);

	}
}
