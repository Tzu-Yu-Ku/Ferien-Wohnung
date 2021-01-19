package fewodre.catalog;

import fewodre.catalog.events.*;
import fewodre.catalog.holidayhomes.*;
import fewodre.catalog.events.Event.EventType;
import fewodre.utils.Place;

import java.time.LocalDate;
import java.time.LocalTime;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final EventCatalog eventCatalog;
	private final HolidayHomeCatalog holidayHomeCatalog;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	CatalogDataInitializer(EventCatalog eventCatalog, HolidayHomeCatalog holidayHomeCatalog,
			UniqueInventory<UniqueInventoryItem> inventory) {
		this.eventCatalog = eventCatalog;
		this.holidayHomeCatalog = holidayHomeCatalog;
		this.inventory = inventory;
	}

	@Override
	public void initialize() {

		if (holidayHomeCatalog.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");

		HolidayHome dummyHome1 = new HolidayHome();
		dummyHome1.setName("Nette Wohnung in der Dresdner Innenstadt");
		dummyHome1.setDescription("Dicht an der bekannten Barszene in Neustadt bietet diese Wohnung einen tollen Ort" +
				"für Ihren nächsten Aufenthalt in Dresden.");
		dummyHome1.setPlace(new Place("An der Goldgrube",
				"1", "01099", "Dresden", 5000, 5000));
		dummyHome1.setIsBookable(true);
		dummyHome1.setHostMail("host@host");
		dummyHome1.setImage("house1.png");
		dummyHome1.setCapacity(4);
		dummyHome1.setPrice(Money.of(149.49f, "EUR"));
		holidayHomeCatalog.save(dummyHome1);

		HolidayHome dummyHome2 = new HolidayHome();
		dummyHome2.setName("Gemütliches Haus an der Elbe");
		dummyHome2.setDescription("Für einen entspannten Urlaub in Dresden mit tollem Blick auf die einzigartige" +
				" Elbe bietet sich diese Wohnung an.");
		dummyHome2.setPlace(new Place("An der Elbe",
				"1", "01099", "Dresden", 8000, 8000));
		dummyHome2.setIsBookable(true);
		dummyHome2.setHostMail("host@host");
		dummyHome2.setImage("house2.png");
		dummyHome2.setCapacity(6);
		dummyHome2.setPrice(Money.of(74.99f, "EUR"));
		holidayHomeCatalog.save(dummyHome2);

		HolidayHome dummyHome3 = new HolidayHome();
		dummyHome3.setName("*günstig* Ferienappartment in Blasewitz");
		dummyHome3.setDescription("Für die Sparfüchse unter Ihnen bietet sich dieses äußerst günstig gelegene" +
				" Appartment besonders gut an.");
		dummyHome3.setPlace(new Place("Am Schillerplatz",
				"1", "01099", "Dresden", 8000, 8000));
		dummyHome3.setIsBookable(true);
		dummyHome3.setHostMail("host@host");
		dummyHome3.setImage("house3.png");
		dummyHome3.setCapacity(6);
		dummyHome3.setPrice(Money.of(49.99f, "EUR"));
		holidayHomeCatalog.save(dummyHome3);

		if (eventCatalog.findAll().iterator().hasNext()) {
			return;
		}

		Event eventTest = new Event();
		eventTest.setName("Stadtführung im abendlichen Dresden");
		eventTest.setDescription("Nehmen Sie an der Stadtführung teil und lernen Sie Dresden und dessen einzigartige" +
				" Geschichte kennen.");
		eventTest.setPlace(new Place("An der Frauenkirche",
				"1", "01234", "Dresden", 2000, 2000));
		eventTest.setCapacity(10);
		eventTest.setEventCompanyUuid(("event"));
		eventTest.setPrice(Money.of(49.49f, "EUR"));
		eventTest.setEventStatus(true);
		eventTest.setEventType(EventType.SMALL);
		eventTest.setImage("event1.png");
		eventTest.setDate(LocalDate.now().plusDays(10));
		eventTest.setTime(LocalTime.now());
		eventCatalog.save(eventTest);
		inventory.save(new UniqueInventoryItem(eventTest, Quantity.of(eventTest.getCapacity())));

		Event eventTest2 = new Event();
		eventTest2.setName("Gemeinsamer Spaziergang an der Elbe");
		eventTest2.setDescription("Sind Sie gerne unterwegs und möchten eine einmalige Erfahrung hier in Dresden" +
				" machen? Dann nehmen an unserem berühmten Spaziergang an und um die Elbe herum teil.");
		eventTest2.setPlace(new Place("An der Elbe",
				"1", "01234", "Dresden", 5900, 6100));
		eventTest2.setCapacity(25);
		eventTest2.setEventCompanyUuid(("event"));
		eventTest2.setPrice(Money.of(34.99f, "EUR"));
		eventTest2.setEventStatus(true);
		eventTest2.setEventType(EventType.LARGE);
		eventTest2.setImage("event2.png");
		eventTest2.setDate(LocalDate.now().plusDays(4));
		eventTest2.setTime(LocalTime.now());
		eventCatalog.save(eventTest2);
		inventory.save(new UniqueInventoryItem(eventTest2, Quantity.of(eventTest.getCapacity())));

	}
}
