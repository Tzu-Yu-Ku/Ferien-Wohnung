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
		HolidayHome dummyHome1 = new HolidayHome();
		HolidayHome dummyHome2 = new HolidayHome();
		dummyHome1.setName("Nette Wohnung in der Innenstadt");
		dummyHome1.setDescription("Dicht an der bekannten Barszene in Neustadt.");
		dummyHome1.setPlace(new Place("An der Goldgrube", "1", "01099", "Dresden", 1, 1));
		dummyHome1.setIsBookable(true);
		dummyHome1.setHostUuid("abcd-efgh-jkli");
		dummyHome1.setImage("house4.png");
		dummyHome1.setCapacity(4);
		dummyHome1.setPrice(Money.of(250, "EUR"));

		dummyHome2.setName("Gemühtliches Haus an der Elbe");
		dummyHome2.setDescription("Für einen entspannten Urlaub in Dresden");
		dummyHome2.setPlace(new Place("An der Elbe", "1", "01099", "Dresden", 1, 1));
		dummyHome2.setIsBookable(true);
		dummyHome2.setHostUuid("abcd-efgh-jkli");
		dummyHome2.setImage("house3.png");
		dummyHome2.setCapacity(6);
		dummyHome2.setPrice(Money.of(75, "EUR"));
		holidayHomeCatalog.save(dummyHome1);
		holidayHomeCatalog.save(dummyHome2);

		if (eventCatalog.findAll().iterator().hasNext()) {
			return;
		}

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
	}
}
