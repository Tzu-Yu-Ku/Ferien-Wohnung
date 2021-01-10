package fewodre.bookings;

import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import javax.xml.bind.annotation.XmlType;

public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

	static final Sort DEFAULT_SORT = Sort.by("arrivalDate").ascending();

	Iterable<BookingEntity> findBookingsByUuidHostEquals(String host);

	Streamable<BookingEntity> findAllByUuidHost(String host, Sort sort);

	default Streamable<BookingEntity> findAllByUuidHost(String host){
		return findAllByUuidHost(host,DEFAULT_SORT);
	}

	Iterable<BookingEntity> findBookingsByUuidTenant(String tenant);

	Iterable<BookingEntity> findBookingEntityByUserAccount(UserAccount userAccount);

	Streamable<BookingEntity> findBookingsByUuidHome(String holidayHome);

	BookingEntity findFirstByOrderIdentifier(OrderIdentifier orderIdentifier);

	@Override
	Streamable<BookingEntity> findAll();

	//Iterable<BookingEntity> findBookingsByUuidEvents(String event);

	//Iterable<BookingEntity> findById(OrderIdentifier id);

}
