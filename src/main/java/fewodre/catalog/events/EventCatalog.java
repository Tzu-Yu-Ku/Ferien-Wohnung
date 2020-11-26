package fewodre.catalog.events;

import fewodre.catalog.holidayhomes.HolidayHome;
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
	Streamable<Event> findAll();

	Event findFirstByProductIdentifier(ProductIdentifier productIdentifier);
}
