package fewodre.bookings;

import fewodre.useraccounts.AccountEntity;
import org.salespointframework.order.OrderIdentifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

	Iterable<BookingEntity> findBookingsByUuidHostEquals(String host);

	Iterable<BookingEntity> findBookingsByUuidTenant(String tenant);

	Iterable<BookingEntity> findBookingsByUuidHome(String holidayHome);

	@Override
	Streamable<BookingEntity> findAll();

	//Iterable<BookingEntity> findBookingsByUuidEvents(String event);

	//Iterable<BookingEntity> findById(OrderIdentifier id);

}
