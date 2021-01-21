package fewodre.catalog;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.events.EventForm;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.catalog.holidayhomes.HolidayHomeForm;
import fewodre.useraccounts.Coordinates;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
	@WithMockUser(username = "test@test", roles = "TENANT")
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

		Streamable<Event> events = (Streamable<Event>) model.asMap().get("eventCatalog");
		assertThat(events.isEmpty()).isFalse();
	}

	@Test
	@WithMockUser(username = "event@employee", roles = "EVENT_EMPLOYEE")
	public void addEventPageGet() throws Exception {
		Model model = new ExtendedModelMap();
		EventForm eventForm = new EventForm();
		String returnedView = catalogController.addEventPage(model, eventForm);

		mvc.perform(get("/addevents"))
				.andExpect(status().isOk())
				.andExpect(view().name("addevent"));
	}

	@Test
	@WithMockUser(username = "event@employee", roles = "EVENT_EMPLOYEE")
	public void addEventPagePost() throws Exception {
		Model model = new ExtendedModelMap();
		EventForm eventForm = new EventForm();
		String returnedView = catalogController.addEventPage(model, eventForm);

		EventForm form = new EventForm();
		form.setEventCompanyUuid("event@employee");
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
		form.setDate("2021-11-11");
//		form.setRepeateRate(1);
//		form.setRepeats(1);

		byte[] imageBytes = new byte[]{1};
		MockMultipartFile image = new MockMultipartFile("imageupload","test.png",
				MediaType.IMAGE_PNG.toString(), imageBytes);

		MvcResult result = mvc.perform(multipart("/addEvent")
				.file(image)
				.flashAttr("form", form))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/editEventLocation?event=**"))
				.andReturn();

		String testEventId = result.getModelAndView().getViewName().split("=")[1];

		result = mvc.perform(get("/events"))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(result);
		Streamable<Event> events = (Streamable<Event>) result.getModelAndView().getModel().get("eventCatalog");
		Set<Event> eventList = events.toSet();

		boolean eventWasCreated = false;
		for (Event e : events) {
			String string = e.getId().toString();
			if (string.equals(testEventId)) {
				eventWasCreated = true;
			}
		}
		assertThat(eventWasCreated).isTrue();
	}

	@Test
	@WithMockUser(username = "event@employee", roles = "EVENT_EMPLOYEE")
	public void editEventPage() throws Exception {
		Event testEvent = eventCatalog.findAll().toList().get(0);
		mvc.perform(post("/editeventpage")
				.param("event", testEvent.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("editevent"));
	}

	@Test
	@WithMockUser(username = "event@employee", roles = "EVENT_EMPLOYEE")
	public void editEvent() throws Exception {
		Event testEvent = eventCatalog.findAll().toList().get(0);
		mvc.perform(post("/editEvent")
				.param("eventId", testEvent.getId().toString())
				.param("name", "test_name")
				.param("description", "test_desc")
				.param("price", "1234")
				.param("date", "2021-12-12")
				.param("time", "")
				.param("repeats", "20")
				.param("repeateRate", "30")
				.param("capacity", "99")
				.param("street", "test_street")
				.param("houseNumber", "1234")
				.param("postalCode", "55555")
				.param("city", "test_city")
				.param("coordinates_x", "999")
				.param("coordinates_y", "888"))
				.andExpect(status().isFound());

		MvcResult result = mvc.perform(get("/events"))
				.andExpect(status().isOk())
				.andReturn();

		Streamable<Event> events = (Streamable<Event>) result.getModelAndView().getModel().get("eventCatalog");

		Event editedEvent = null;
		for (Event e : events) {
			if (e.getId().toString().equals(testEvent.getId().toString())) {
				editedEvent = e;
			}
		}

		assertThat(editedEvent).isNotNull();
		assertThat(editedEvent.getId()).isEqualTo(testEvent.getId());
		assertThat(editedEvent.getName()).isEqualTo("test_name");
		assertThat(editedEvent.getDescription()).isEqualTo("test_desc");
		assertThat(editedEvent.getPrice().toString()).isEqualTo("EUR 1234");
		assertThat(editedEvent.getDate().toString()).isEqualTo("2021-12-12");
//		assertThat(editedEvent.getRepeats()).isEqualTo(20);
//		assertThat(editedEvent.getRepeateRate()).isEqualTo(30);
		assertThat(editedEvent.getCapacity()).isEqualTo(99);
		assertThat(editedEvent.getPlace().getStreet()).isEqualTo("test_street");
		assertThat(editedEvent.getPlace().getHouseNumber()).isEqualTo("1234");
		assertThat(editedEvent.getPlace().getPostalCode()).isEqualTo("55555");
		assertThat(editedEvent.getPlace().getCity()).isEqualTo("test_city");
		assertThat(editedEvent.getPlace().getCoordX()).isEqualTo(999);
		assertThat(editedEvent.getPlace().getCoordY()).isEqualTo(888);
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
	public void editHolidayHomePost() throws Exception {
		HolidayHome testHome = holidayHomeCatalog.findAll().toList().get(0);
		mvc.perform(post("/editholidayhome")
				.param("holidayHome", testHome.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("editholidayhome"));
	}

	@Test
	@WithMockUser(username = "host@host", roles = "HOST")
	public void editHolidayHome() throws Exception {
		HolidayHome testHome = holidayHomeCatalog.findAll().toList().get(0);
		MvcResult result = mvc.perform(post("/editHolidayHome")
				.param("holidayHomeId", testHome.getId().toString())
				.param("name", "name_edit")
				.param("description", "desc_edit")
				.param("price", "111")
				.param("capacity", "7")
				.param("street", "street_test")
				.param("houseNumber", "77")
				.param("city", "city_test")
				.param("postalCode", "99999")
				.param("coordinates_x", "999")
				.param("coordinates_y", "888"))
				.andExpect(status().isFound())
				.andReturn();

		result = mvc.perform(post("/housedetails")
				.param("holidayHome", testHome.getId().toString()))
				.andExpect(status().isOk())
				.andReturn();

		HolidayHome savedHome = (HolidayHome) result.getModelAndView().getModel().get("holidayHome");
		assertThat(savedHome.getHostMail()).isEqualTo("host@host");
		assertThat(savedHome.getName()).isEqualTo("name_edit");
		assertThat(savedHome.getDescription()).isEqualTo("desc_edit");
		assertThat(savedHome.getCapacity()).isEqualTo(7);
		assertThat(savedHome.getPlace().getCity()).isEqualTo("city_test");
		assertThat(savedHome.getPlace().getStreet()).isEqualTo("street_test");
		assertThat(savedHome.getPlace().getPostalCode()).isEqualTo("99999");
		assertThat(savedHome.getPlace().getCoordX()).isEqualTo(999);
		assertThat(savedHome.getPlace().getCoordY()).isEqualTo(888);
		assertThat(savedHome.getPrice().toString()).isEqualTo("EUR 111");
	}

	@Test
	@WithMockUser(username = "host@host", roles = "HOST")
	public void addHolidayhomePageGet() throws Exception {
		Model model = new ExtendedModelMap();
		HolidayHomeForm form = new HolidayHomeForm();
		String returnedView = catalogController.addHolidayhomePage(model, form);

		mvc.perform(get("/addholidayhome"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "host@host", roles = "HOST")
	public void addHolidayhomePagePost() throws Exception {
		Model model = new ExtendedModelMap();
		HolidayHomeForm form = new HolidayHomeForm();
		String returnedView = catalogController.addHolidayhomePage(model, form);

		form = new HolidayHomeForm();
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

		byte[] imageBytes = new byte[]{1};
		MockMultipartFile image = new MockMultipartFile("imageupload","test.png",
				MediaType.IMAGE_PNG.toString(), imageBytes);

		MvcResult result = mvc.perform(multipart("/addHolidayHome")
				.file(image)
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