package fewodre.catalog;

import fewodre.catalog.events.*;
import fewodre.catalog.holidayhomes.*;
import fewodre.catalog.events.Event.EventType;
import fewodre.utils.Place;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final EventCatalog eventCatalog;
	private final HolidayHomeCatalog holidayHomeCatalog;

	CatalogDataInitializer(EventCatalog eventCatalog, HolidayHomeCatalog holidayHomeCatalog) {
		this.eventCatalog = eventCatalog;
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

		if (eventCatalog.findAll().iterator().hasNext()) {
			return;
		}

		Event eventTest = new Event();
		eventTest.setName("Villaparty am Wasserfall neben den Satanisten");
		eventTest.setDescription("Einfach teuflisches plätschern.");
		eventTest.setPlace(new Place("An der Goldgrube", "1", "01234", "Dresden", 1, 1));
		eventTest.setCapacity(123456);
		eventTest.setEventCompanyUuid("eventCompanyUuid");
		eventTest.setEventStatus(true);
		eventTest.setEventType(EventType.SMALL);
		eventTest.setImage("house2.png");
		eventCatalog.save(eventTest);
	}
}
