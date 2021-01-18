package fewodre.bookings;

import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountRepository;
import org.junit.jupiter.api.Test;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingManagementUnitTest {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private OrderManagement orderManagement;

	@Autowired
	private HolidayHomeCatalog holidayHomeCatalog;

	@Autowired
	private EventCatalog eventCatalog;

	@Autowired
	private UniqueInventory<UniqueInventoryItem> holidayHomeStorage;

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void createBookingEntity() {
		HolidayHome testHome = holidayHomeCatalog.findAll().toList().get(0);
		UserAccount testAccount = accountRepository.findAll().iterator().next().getAccount();

	}

	@Test
	void payDeposit() {
	}

	@Test
	void cancelEvent() {
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