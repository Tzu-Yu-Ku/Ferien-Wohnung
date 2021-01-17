package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import org.apache.tomcat.jni.Local;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.salespointframework.order.Cart;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerIntegrationTest {

	@Autowired
	static private AccountManagement accountManagement;

	@Autowired
	static private AccountRepository accountRepository;

	@Autowired
	static private UserAccountManagement userAccountManagement;

	@Autowired
	private BookingManagement bookingManagement;

	@Autowired
	private EventCatalog eventcatalog;

	@Autowired
	private HolidayHomeCatalog holidayHomeCatalog;

	@Autowired
	private CartController cartController;

	@Autowired
	MockMvc mvc;

	HolidayHome testHome;
	Event testEvent;
	LocalDate arrivalDate;
	LocalDate departureDate;

	@Test
	void basket() {

	}

	@Test
	@WithMockUser(username = "test@test", roles = "TENANT")
	void addHolidayHome() throws Exception {
		prepareCart();

		MvcResult mvcResult = mvc.perform(get("/cart"))
				.andExpect(model().attributeExists("holidayHome", "arrivalDate", "departureDate"))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(mvcResult);
	}

	@Test
	@WithMockUser(username = "test@test", roles = "TENANT")
	void updateDatum() throws Exception {
		prepareCart();

		mvc.perform(post("/updateDatum/"+testHome.getId().toString())
				.param("newSDate", arrivalDate.toString())
				.param("newEDate", departureDate.plusDays(1).toString()))
				.andDo(new ResultHandler() {
					@Override
					public void handle(MvcResult mvcResult) throws Exception {
						ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
						System.out.println(modelMap);
					}
				})
				.andExpect(status().isFound());

		mvc.perform(get("/cart"))
				.andExpect(model().attributeExists("holidayHome", "arrivalDate", "departureDate"))
				.andExpect(model().attribute("departureDate", departureDate.plusDays(1)))
				.andExpect(status().isOk());
	}

	@Test
	void updateTicketCount() {
	}

	@Test
	void testAddHolidayHome() {
	}

	@Test
	@WithMockUser(username = "test@test", roles = "TENANT")
	void addEvent() throws Exception {
		prepareCart();

		testEvent = eventcatalog.findAll().toList().get(0);

		mvc.perform(post("/eventcart")
				.param("eid", testEvent.getId().toString())
				.param("number", "1"))
				.andExpect(status().isOk());

		mvc.perform(get("/cart"))
				.andExpect(model().attributeExists("eventsInCart", "eventCatalog"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test@test", roles = "TENANT")
	void removeEventItem() throws Exception {
		prepareCart();
		addEvent();
		mvc.perform(get("/cart"))
				.andExpect(model().attributeExists("holidayHome", "arrivalDate", "departureDate"))
				.andExpect(model().attribute("arrivalDate", arrivalDate))
				.andExpect(model().attribute("departureDate", departureDate))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test@test", roles = "TENANT")
	void removeHolidayHomeItem() throws Exception {
		prepareCart();

		mvc.perform(get("/cart"))
				.andDo(new ResultHandler() {
					@Override
					public void handle(MvcResult mvcResult) throws Exception {
						ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
						System.out.println(modelMap.toString());
					}
				})
				.andExpect(model().attributeExists("holidayHome", "arrivalDate", "departureDate"))
				.andExpect(model().attribute("arrivalDate", arrivalDate))
				.andExpect(model().attribute("departureDate", departureDate))
				.andExpect(status().isOk());

		mvc.perform(get("/removeProduct/" + testHome.getId().toString()))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/cart"));

	}

	@Test
	@WithMockUser(username = "test@test", roles = "TENANT")
	void removeItemThatDoesntExist() throws Exception {
		prepareCart();

		mvc.perform(get("/removeProduct/INVALID_ID"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/cart"));

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

	@WithMockUser(username = "test@test", roles = "TENANT")
	void prepareCart() throws Exception {
		testHome = holidayHomeCatalog.findAll().toList().get(0);
		arrivalDate = LocalDate.parse("2021-02-10");
		departureDate = LocalDate.parse("2021-02-15");

		MvcResult mvcResult = mvc.perform(post("/cart")
				.param("hid", testHome.getId().toString())
				.param("arrivaldate", arrivalDate.toString())
				.param("departuredate", departureDate.toString()))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/cart"))
				.andReturn();
		System.out.println(mvcResult);
	}
}