package fewodre.catalog;

import fewodre.catalog.events.*;
import fewodre.catalog.holidayhomes.*;
import fewodre.catalog.events.Event.EventType;
import fewodre.utils.Place;

import org.javamoney.moneta.Money;
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
		test.setName("Nette Wohnung in der Innenstadt");
		test.setDescription("Dicht an der bekannten Barszene in Neustadt.");
		test.setPlace(new Place("An der Goldgrube", "1", "01099", "Dresden", 1, 1));
		test.setBookable(true);
		test.setHostUuid("abcd-efgh-jkli");
		test.setImage("house4.png");

		test2.setName("Gemühtliches Haus an der Elbe");
		test2.setDescription("Für einen entspannten Urlaub in Dresden");
		test2.setPlace(new Place("An der Elbe", "1", "01099", "Dresden", 1, 1));
		test2.setBookable(true);
		test2.setHostUuid("abcd-efgh-jkli");
		test2.setImage("house3.png");
		holidayHomeCatalog.save(test);
		holidayHomeCatalog.save(test2);

		if (eventCatalog.findAll().iterator().hasNext()) {
			return;
		}
/*
		Event eventTest = new Event();
		eventTest.setName("Stadtführung im abendlichen Dresden");
		eventTest.setDescription("Nehmen sie an der Stadtführung teil und lernen sie Dresden und dessen atemberaubende Geschichte kennen.");
		eventTest.setPlace(new Place("An der Frauenkirche", "1", "01234", "Dresden", 1, 1));
		eventTest.setCapacity(10);
		eventTest.setEventCompanyUuid("eventCompanyUuid");
		eventTest.setPrice(Money.of(123, "EUR"));
		eventTest.setEventStatus(true);
		eventTest.setEventType(EventType.SMALL);
		eventTest.setImage("event1.png");
		eventCatalog.save(eventTest);
		*/
	}
}
