package fewodre.catalog;

import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.HashSet;


public interface HolidayHomeEventCatalog extends Catalog<HolidayHome> {

	static final Sort DEFAULT_SORT = Sort.by("productIdentifier").descending();


}
