package fewodre.catalog;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.catalog.holidayhomes.HolidayHomeForm;
import fewodre.useraccounts.Coordinates;
import fewodre.utils.Place;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	public void addHolidayhomePage() throws Exception {
		Model model = new ExtendedModelMap();
		String returnedView = catalogController.addHolidayhomePage(model);

		mvc.perform(get("/addholidayhome"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "host@host", roles = "HOST")
	public void editHolidayHomeLocation() throws Exception {
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