package fewodre.catalog;

import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Streamable;

import java.util.HashSet;


public interface HolidayHomeCatalog extends Catalog<HolidayHome> {

	static final Sort DEFAULT_SORT = Sort.by("productIdentifier").descending();

	@Override
	Streamable<HolidayHome> findAll();
}
