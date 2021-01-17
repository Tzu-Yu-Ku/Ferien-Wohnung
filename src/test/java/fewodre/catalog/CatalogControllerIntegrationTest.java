package fewodre.catalog;

import fewodre.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.*;

class CatalogControllerIntegrationTest extends AbstractIntegrationTests {

	@Autowired
	CatalogController catalogController;


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
}