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
		HolidayHome dummyHome2 = new HolidayHome();
		dummyHome1.setName("Nette Wohnung in der Innenstadt");
		dummyHome1.setDescription("Dicht an der bekannten Barszene in Neustadt.");
		dummyHome1.setPlace(new Place("An der Goldgrube", "1", "01099", "Dresden", 1, 1));
		dummyHome1.setIsBookable(true);
		dummyHome1.setHostMail("host@host");
		dummyHome1.setImage("house4.png");
		dummyHome1.setCapacity(4);
		dummyHome1.setPrice(Money.of(250, "EUR"));

		dummyHome2.setName("Gemühtliches Haus an der Elbe");
		dummyHome2.setDescription("Für einen entspannten Urlaub in Dresden");
		dummyHome2.setPlace(new Place("An der Elbe", "1", "01099", "Dresden", 1, 1));
		dummyHome2.setIsBookable(true);
		dummyHome2.setHostMail("host@host");
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
		eventTest.setDescription(
				"Nehmen sie an der Stadtführung teil und lernen sie Dresden und dessen atemberaubende Geschichte kennen.");
		eventTest.setPlace(new Place("An der Frauenkirche", "1", "01234", "Dresden", 1, 1));
		eventTest.setCapacity(10);
		eventTest.setEventCompanyUuid(("event"));
		eventTest.setPrice(Money.of(123, "EUR"));
		eventTest.setEventStatus(true);
		eventTest.setEventType(EventType.SMALL);
		eventTest.setImage("event1.png");
		eventTest.setDate(LocalDate.now().plusDays(2));
		eventTest.setTime(LocalTime.now());
		eventCatalog.save(eventTest);
		inventory.save(new UniqueInventoryItem(eventTest, Quantity.of(eventTest.getCapacity())));

	}
}
