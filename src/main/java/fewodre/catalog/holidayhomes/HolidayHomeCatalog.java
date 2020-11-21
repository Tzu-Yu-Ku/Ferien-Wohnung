package fewodre.catalog.holidayhomes;

import fewodre.catalog.holidayhomes.HolidayHome;
import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;


public interface HolidayHomeCatalog extends Catalog<HolidayHome> {

	static final Sort DEFAULT_SORT = Sort.by("productIdentifier").descending();

	@Override
	Streamable<HolidayHome> findAll();
}
