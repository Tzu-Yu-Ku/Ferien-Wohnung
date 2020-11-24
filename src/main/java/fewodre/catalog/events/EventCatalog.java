package fewodre.catalog.events;

import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Streamable;

import java.util.HashSet;


public interface EventCatalog extends Catalog<Event> {

	static final Sort DEFAULT_SORT = Sort.by("productIdentifier").descending();

	@Override
<<<<<<< HEAD:src/main/java/fewodre/catalog/HolidayHomeEventCatalog.java
	Streamable<HolidayHome> findAll();

	HolidayHome findFirstByProductIdentifier(ProductIdentifier productIdentifier);
}
=======
	Streamable<Event> findAll();
}
>>>>>>> origin/prototype-ood:src/main/java/fewodre/catalog/events/EventCatalog.java
