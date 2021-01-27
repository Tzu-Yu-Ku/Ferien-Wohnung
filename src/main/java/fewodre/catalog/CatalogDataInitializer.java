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
		dummyHome1.setName("Nette Wohnung in der Dresdner Neustadt");
		dummyHome1.setDescription("Dicht an der bekannten Barszene in Neustadt bietet diese Wohnung einen tollen Ort"
				+ "für Ihren nächsten Aufenthalt in Dresden.");
		dummyHome1.setPlace(new Place("An der Goldgrube", "1", "01099", "Dresden", 500, 500));
		dummyHome1.setIsBookable(true);
		dummyHome1.setHostMail("host@host");
		dummyHome1.setImage("house1.png");
		dummyHome1.setCapacity(4);
		dummyHome1.setPrice(Money.of(149.49f, "EUR"));
		holidayHomeCatalog.save(dummyHome1);

		

		HolidayHome dummyHome2 = new HolidayHome();
		dummyHome2.setName("Gemütliches Haus an der Elbe");
		dummyHome2.setDescription("Für einen entspannten Urlaub in Dresden mit tollem Blick auf die einzigartige"
				+ " Elbe bietet sich diese Wohnung an.");
		dummyHome2.setPlace(new Place("An der Elbe", "1", "01099", "Dresden", 750, 750));
		dummyHome2.setIsBookable(true);
		dummyHome2.setHostMail("host@host");
		dummyHome2.setImage("house2.png");
		dummyHome2.setCapacity(6);
		dummyHome2.setPrice(Money.of(74.99f, "EUR"));
		holidayHomeCatalog.save(dummyHome2);

		HolidayHome dummyHome3 = new HolidayHome();
		dummyHome3.setName("*günstig* Ferienappartment in Blasewitz");
		dummyHome3.setDescription("Für die Sparfüchse unter Ihnen bietet sich dieses äußerst günstig gelegene"
				+ " Appartment besonders gut an.");
		dummyHome3.setPlace(new Place("Am Schillerplatz", "1", "01099", "Dresden", 250, 250));
		dummyHome3.setIsBookable(true);
		dummyHome3.setHostMail("host@host");
		dummyHome3.setImage("house3.png");
		dummyHome3.setCapacity(6);
		dummyHome3.setPrice(Money.of(49.99f, "EUR"));
		holidayHomeCatalog.save(dummyHome3);

		HolidayHome dummyHome4 = new HolidayHome();
		dummyHome4.setName("*günstig* Ferienappartment in der Neustadt");
		dummyHome4.setDescription("Für die Sparfüchse unter Ihnen bietet sich dieses äußerst günstig gelegene Wohnung in der Neustadt");
		dummyHome4.setPlace(new Place("Am Schützenplatz", "33", "01099", "Dresden", 800, 600));
		dummyHome4.setIsBookable(true);
		dummyHome4.setHostMail("neuer@host");
		dummyHome4.setImage("house4.png");
		dummyHome4.setCapacity(8);
		dummyHome4.setPrice(Money.of(49.99f, "EUR"));
		holidayHomeCatalog.save(dummyHome4);

		HolidayHome dummyHome5 = new HolidayHome();
		dummyHome5.setName("Luxusappartment in der Altstadt");
		dummyHome5.setDescription("Für den besonderen flair bieten wir ihnen die beste Wohnung von ganz Dresden.");
		dummyHome5.setPlace(new Place("Am Altmarkt", "1", "01099", "Dresden", 250, 100));
		dummyHome5.setIsBookable(true);
		dummyHome5.setHostMail("neuer@host");
		dummyHome5.setImage("house5.png");
		dummyHome5.setCapacity(2);
		dummyHome5.setPrice(Money.of(254.99f, "EUR"));
		holidayHomeCatalog.save(dummyHome5);

		if (eventCatalog.findAll().iterator().hasNext()) {
			return;
		}

		Event eventTest = new Event();
		eventTest.setName("Stadtführung im abendlichen Dresden");
		eventTest.setDescription("Nehmen Sie an der Stadtführung teil und lernen Sie Dresden und dessen einzigartige"
				+ " Geschichte kennen.");
		eventTest.setPlace(new Place("An der Frauenkirche", "1", "01234", "Dresden", 200, 200));
		eventTest.setCapacity(10);
		eventTest.setEventCompanyUuid(("event"));
		eventTest.setPrice(Money.of(0, "EUR"));
		eventTest.setEventStatus(true);
		eventTest.setEventType(EventType.SMALL);
		eventTest.setImage("event1.png");
		eventTest.setDate(LocalDate.now());
		eventTest.setTime(LocalTime.now().plusMinutes(1));
		eventCatalog.save(eventTest);
		inventory.save(new UniqueInventoryItem(eventTest, Quantity.of(eventTest.getCapacity())));

		Event eventTest2 = new Event();
		eventTest2.setName("Gemeinsamer Spaziergang an der Elbe");
		eventTest2.setDescription("Sind Sie gerne unterwegs und möchten eine einmalige Erfahrung hier in Dresden"
				+ " machen? Dann nehmen an unserem berühmten Spaziergang an und um die Elbe herum teil.");
		eventTest2.setPlace(new Place("An der Elbe", "1", "01234", "Dresden", 500, 500));
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

		Event eventTest3 = new Event();
		eventTest3.setName("Konzert am Elbufer");
		eventTest3.setDescription("Nehmen sie doch gerne am Konzert des weltberühmten unbekannten künstler teil.");
		eventTest3.setPlace(new Place("An der Elbe", "125a", "12345", "Dresden", 800, 800));
		eventTest3.setCapacity(250);
		eventTest3.setEventCompanyUuid(("event"));
		eventTest3.setPrice(Money.of(99.99f, "EUR"));
		eventTest3.setEventStatus(true);
		eventTest3.setEventType(EventType.LARGE);
		eventTest3.setImage("event3.jpg");
		eventTest3.setDate(LocalDate.now().plusDays(12));
		eventTest3.setTime(LocalTime.now().minusHours(6));
		eventCatalog.save(eventTest3);
		inventory.save(new UniqueInventoryItem(eventTest3, Quantity.of(eventTest.getCapacity())));

		Event eventTest4 = new Event();
		eventTest4.setName("Radtour durch die Stadt");
		eventTest4.setDescription("Nehmen sie doch gerne unserer Fahrradtour durch Dresden teil und erleben Sie wunderbaren Aussichten und die tollsten Stellen der Stadt");
		eventTest4.setPlace(new Place("Am Altmarkt", "1", "12345", "Dresden", 400, 700));
		eventTest4.setCapacity(20);
		eventTest4.setEventCompanyUuid(("event"));
		eventTest4.setPrice(Money.of(0, "EUR"));
		eventTest4.setEventStatus(true);
		eventTest4.setEventType(EventType.SMALL);
		eventTest4.setImage("event1.png");
		eventTest4.setDate(LocalDate.now().plusDays(1));
		eventTest4.setTime(LocalTime.now().minusHours(6));
		eventCatalog.save(eventTest4);
		inventory.save(new UniqueInventoryItem(eventTest4, Quantity.of(eventTest.getCapacity())));

		Event eventTest5 = new Event();
		eventTest5.setName("Besuch im alten Museumskeller");
		eventTest5.setDescription("Besuchen Sie exklusiv mit uns den alten Museumskeller und entdecken Sie die Geheimnisse Dresdens");
		eventTest5.setPlace(new Place("Schloßstraße", "13", "12345", "Dresden", 250, 650));
		eventTest5.setCapacity(25);
		eventTest5.setEventCompanyUuid(("event"));
		eventTest5.setPrice(Money.of(0, "EUR"));
		eventTest5.setEventStatus(true);
		eventTest5.setEventType(EventType.SMALL);
		eventTest5.setImage("event4.jpg");
		eventTest5.setDate(LocalDate.now().plusDays(3));
		eventTest5.setTime(LocalTime.now().minusHours(1));
		eventCatalog.save(eventTest5);
		inventory.save(new UniqueInventoryItem(eventTest5, Quantity.of(eventTest.getCapacity())));

		// Funktioniert nicht :/
//		 dummyHome1.acceptedEvents.add(eventCatalog.findFirstByProductIdentifier(eventTest4.getId()));
//		 dummyHome1.acceptEvent(eventTest4);
//		 holidayHomeCatalog.save(dummyHome1);
	}
}
