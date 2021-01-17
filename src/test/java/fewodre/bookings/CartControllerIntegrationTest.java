package fewodre.bookings;

import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import org.junit.jupiter.api.Test;
import org.salespointframework.order.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ModelAttribute;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerIntegrationTest {

	@Autowired
	private AccountManagement accountManagement;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BookingManagement bookingManagement;

	@Autowired
	private EventCatalog eventcatalog;

	@Autowired
	private HolidayHomeCatalog holidayHomeCatalog;

	@Autowired
	MockMvc mvc;

	@Test
	void basket() {

	}

	@Test
	@WithMockUser(username = "tenant@tenant", roles = "TENANT")
	void addHolidayHome() throws Exception {
		HolidayHome testHome = holidayHomeCatalog.findAll().toList().get(0);

		mvc.perform(
				post("/cart")
						.param("hid", testHome.getId().toString())
						.param("arrivaldate", "2021-02-10")
						.param("departuredate", "2021-02-15")
		).andExpect(status().isFound());

		mvc.perform(get("/cart"))
				.andExpect(model().attributeExists("holidayHome", "arrivalDate", "departureDate"))
				.andExpect(status().isOk());
	}

	@Test
	void updateDatum() {
	}

	@Test
	void updateTicketCount() {
	}

	@Test
	void testAddHolidayHome() {
	}

	@Test
	void addEvent() {
	}

	@Test
	void removeItem() {
	}

	@Test
	void clear() {
	}

	@Test
	void buy() {
	}

	@Test
	void checkIfBooked() {
	}

	@Test
	void details() {
	}

	@Test
	void cancel() {
	}

	@Test
	void pay() {
	}

	@Test
	void payRest() {
	}

	@Test
	void confirm() {
	}
}