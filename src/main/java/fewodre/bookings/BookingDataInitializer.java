package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.order.Cart;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@Component
public class BookingDataInitializer{

	private final Logger LOG= LoggerFactory.getLogger(BookingDataInitializer.class);

	private final BookingManagement bookingManagement;
	private final HolidayHomeCatalog holidayHomeCatalog;
	private final AccountRepository accountRepository;


	BookingDataInitializer(BookingManagement bookingManagement, HolidayHomeCatalog holidayHomeCatalog,
						   AccountRepository accountRepository) {
		Assert.notNull(bookingManagement, "BookingManagement must not be null!");
		Assert.notNull(holidayHomeCatalog, "HolidayHomeCatalog must not be null!");
		Assert.notNull(accountRepository, "AccountRepository must not be null!");

		this.bookingManagement = bookingManagement;
		this.holidayHomeCatalog = holidayHomeCatalog;
		this.accountRepository = accountRepository;
	}

	public void initialize() {

		UserAccount user = accountRepository.findByAccount_Email("test@test").getAccount();
		//canceled
		HolidayHome home1 = holidayHomeCatalog.findFirstByName("Nette Wohnung in der Dresdner Innenstadt");
		LocalDate arrivalDate1 = LocalDate.now().minusDays(7);
		LocalDate departureDate1 = LocalDate.now().minusDays(5);
		Cart cart1 = new Cart();
		cart1.addOrUpdateItem(home1, Quantity.of(ChronoUnit.DAYS.between(arrivalDate1, departureDate1)));
		HashMap<Event, Integer> events1 = new HashMap<Event, Integer>();
		BookingEntity bookingEntity1 = bookingManagement.createBookingEntity(user, home1, cart1, arrivalDate1,
				departureDate1, events1, "Cheque");
		//ordered
		HolidayHome home2 = holidayHomeCatalog.findFirstByName("Gemütliches Haus an der Elbe");
		LocalDate arrivalDate2 = LocalDate.now().plusDays(5);
		LocalDate departureDate2 = LocalDate.now().plusDays(7);
		Cart cart2 = new Cart();
		cart2.addOrUpdateItem(home2, Quantity.of(ChronoUnit.DAYS.between(arrivalDate2, departureDate2)));
		BookingEntity bookingEntity2 = bookingManagement.createBookingEntity(user, home2, cart2, arrivalDate2, departureDate2,
				events1, "Cheque");
		//paid
		LocalDate arrivalDate3 = LocalDate.now().plusDays(30);
		LocalDate departureDate3 = LocalDate.now().plusDays(38);
		Cart cart3 = new Cart();
		cart3.addOrUpdateItem(home1, Quantity.of(ChronoUnit.DAYS.between(arrivalDate3, departureDate3)));
		BookingEntity bookingEntity3 = bookingManagement.createBookingEntity(user, home1, cart3, arrivalDate3,
				departureDate3, events1, "Cheque");
		bookingEntity3.pay();
		//confirmed
		Cart cart4 = new Cart();
		cart3.addOrUpdateItem(home2, Quantity.of(ChronoUnit.DAYS.between(arrivalDate3, departureDate3)));
		BookingEntity bookingEntity4 = bookingManagement.createBookingEntity(user, home2, cart3, arrivalDate3,
				departureDate3, events1, "Cheque");
		bookingEntity4.pay();
		bookingEntity4.confirm();

		//Acquired
		Cart cart5 = new Cart();
		cart5.addOrUpdateItem(home2, Quantity.of(ChronoUnit.DAYS.between(LocalDate.now(), departureDate3)));
		BookingEntity bookingEntity5 = bookingManagement.createBookingEntity(user, home2, cart5, LocalDate.now(),
				departureDate3, events1, "Cheque");
		bookingEntity5.pay();
		bookingEntity5.confirm();


	}


}
