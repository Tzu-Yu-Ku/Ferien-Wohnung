package fewodre.catalog;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.catalog.holidayhomes.HolidayHomeForm;
import fewodre.useraccounts.Coordinates;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogControllerIntegrationTest {

	@Autowired
	CatalogController catalogController;

	@Autowired
	EventCatalog eventCatalog;

	@Autowired
	HolidayHomeCatalog holidayHomeCatalog;

	@Autowired
	MockMvc mvc;

	@Test
	@SuppressWarnings("unchecked")
	public void holidayHomeCatalog() {

		Model model = new ExtendedModelMap();
		String returnedView = catalogController.holidayHomeCatalog(model);
		assertThat(returnedView).isEqualTo("holidayhomes");

		Iterable<Object> objects = (Iterable<Object>) model.asMap().get("holidayhomeCatalog");
		assertThat(objects).hasSize(3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void eventCatalog() {
		Model model = new ExtendedModelMap();
		String returnedView = catalogController.eventCatalog(model);
		assertThat(returnedView).isEqualTo("events");

		Iterable<Object> objects = (Iterable<Object>) model.asMap().get("eventCatalog");
		assertThat(objects).hasSize(2);
	}

	@Test
	@WithMockUser(username = "event@employee", roles = "EVENT_EMPLOYEE")
	public void addEventPage() throws Exception {
		Model model = new ExtendedModelMap();
		String returnedView = catalogController.addEventPage(model);

		mvc.perform(get("/addevents"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "event@employee", roles = "EVENT_EMPLOYEE")
	public void editEventLocation() throws Exception {
		Model model = new ExtendedModelMap();

		Event event = eventCatalog.findAll().toList().get(0);

		mvc.perform(
				get("/editEventLocation")
						.param("event", event.getId().toString()))
				.andExpect(model().attributeExists("productIdentifier"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "host@host", roles = "HOST")
	public void addHolidayhomePageGet() throws Exception {
		Model model = new ExtendedModelMap();
		String returnedView = catalogController.addHolidayhomePage(model);

		mvc.perform(get("/addholidayhome"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "host@host", roles = "HOST")
	public void addHolidayhomePagePost() throws Exception {
		Model model = new ExtendedModelMap();
		String returnedView = catalogController.addHolidayhomePage(model);

		HolidayHomeForm form = new HolidayHomeForm();
		form.setHostMail("host@host");
		form.setName("Test Wohnung");
		form.setDescription("Test Beschreibung");
		form.setCapacity(4);
		form.setCity("Dresden");
		form.setStreet("Straße");
		form.setNumber("1");
		form.setPostalnumber("12345");
		form.setPrice(10);
		form.setCoordinateX(100);
		form.setCoordinateY(100);

		MvcResult result = mvc.perform(post("/addHolidayHome")
				.flashAttr("form", form))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/editHolidayHomeLocation?holidayhome=**"))
				.andReturn();

		String testHomeId = result.getModelAndView().getViewName().split("=")[1];

		result = mvc.perform(post("/housedetails")
				.param("holidayHome", testHomeId))
				.andExpect(status().isOk())
				.andReturn();

		HolidayHome savedHome = (HolidayHome) result.getModelAndView().getModel().get("holidayHome");
		assertThat(savedHome.getHostMail()).isEqualTo("host@host");
		assertThat(savedHome.getName()).isEqualTo("Test Wohnung");
		assertThat(savedHome.getDescription()).isEqualTo("Test Beschreibung");
		assertThat(savedHome.getCapacity()).isEqualTo(4);
		assertThat(savedHome.getPlace().getCity()).isEqualTo("Dresden");
		assertThat(savedHome.getPlace().getStreet()).isEqualTo("Straße");
		assertThat(savedHome.getPlace().getPostalCode()).isEqualTo("12345");
		assertThat(savedHome.getPrice().toString()).isEqualTo("EUR 10");
	}

	@Test
	@WithMockUser(username = "host@host", roles = "HOST")
	public void editHolidayHomeLocationGet() throws Exception {
		Model model = new ExtendedModelMap();

		HolidayHome holidayHome = holidayHomeCatalog.findAll().toList().get(0);

		mvc.perform(get("/editHolidayHomeLocation")
				.param("holidayhome", holidayHome.getId().toString()))
				.andExpect(model().attributeExists("productIdentifier"))
				.andExpect(status().isOk());
	}

	@Test
	public void map() {
		Model model = new ExtendedModelMap();
		Coordinates coordinates = new Coordinates("1-1-1-1");
		String returnedView = catalogController.map(coordinates, model);
		assertThat(returnedView).isEqualTo("map");
	}

}