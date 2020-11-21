package fewodre.catalog;

import fewodre.catalog.holidayhomes.HolidayHome;
import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;


public interface HolidayHomeEventCatalog extends Catalog<HolidayHome> {

	static final Sort DEFAULT_SORT = Sort.by("productIdentifier").descending();

	@Override
	Streamable<HolidayHome> findAll();
}
