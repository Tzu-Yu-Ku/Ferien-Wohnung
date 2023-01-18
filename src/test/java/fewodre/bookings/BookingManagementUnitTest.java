package fewodre.bookings;

import fewodre.catalog.CatalogController;
import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountRepository;
import org.junit.jupiter.api.Test;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.config.http.MatcherType.mvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class BookingManagementUnitTest {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private BookingManagement bookingManagement;

	@Autowired
	private HolidayHomeCatalog holidayHomeCatalog;

	@Autowired
	private EventCatalog eventCatalog;

	@Autowired
	private CatalogController catalogController;

	@Autowired
	private UniqueInventory<UniqueInventoryItem> holidayHomeStorage;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	MockMvc mvc;

	@Test
	void createBookingEntity() {
		HolidayHome testHome = holidayHomeCatalog.findAll().toList().get(0);
		UserAccount testAccount = accountRepository.findAll().iterator().next().getAccount();

	}

	@Test
	void payDeposit() {
		BookingEntity entity =
				bookingManagement.createBookingEntity(accountRepository.findByAccount_Email("test@test").getAccount(),
				holidayHomeCatalog.findAll().iterator().next(), new Cart(), LocalDate.now(), LocalDate.now().plusDays(5),
				new HashMap<Event, Integer>(), "CHEQUE");
		Assert.isTrue(entity.getState().equals(BookingStateEnum.ORDERED));
		bookingManagement.payDeposit(entity);
		Assert.isTrue(entity.getState().equals(BookingStateEnum.PAID));
		System.out.println("Test Passed?!");
	}

	@Test
	void cancelEvent() throws Exception {
	}

	@Test
	void payRest() {
	}

	@Test
	void getStockCountOf() {
	}

	@Test
	void getMoney() {
	}

	@Test
	void giveMoney() {
	}

	@Test
	void findAll() {
	}

	@Test
	void findBookingsByUuidHome() {
	}

	@Test
	void findBookingEntityByUserAccount() {
	}

	@Test
	void findFirstByOrderIdentifier() {
		BookingEntity entity =
				bookingManagement.createBookingEntity(accountRepository.findByAccount_Email("test@test").getAccount(),
						holidayHomeCatalog.findAll().iterator().next(), new Cart(), LocalDate.now(), LocalDate.now().plusDays(5),
						new HashMap<Event, Integer>(), "CHEQUE");
		Assert.isTrue(bookingRepository.findFirstByOrderIdentifier(entity.getId()).equals(entity));
	}

	@Test
	void findByState() {
	}

	@Test
	void findByTenantName() {
	}

	@Test
	void findByHomeName() {
	}
}