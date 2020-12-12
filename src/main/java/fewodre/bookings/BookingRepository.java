package fewodre.bookings;

import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

	Iterable<BookingEntity> findBookingsByUuidHostEquals(String host);

	Iterable<BookingEntity> findBookingsByUuidTenant(String tenant);

	Iterable<BookingEntity> findBookingEntityByUserAccount(UserAccount userAccount);

	Streamable<BookingEntity> findBookingsByUuidHome(String holidayHome);

	BookingEntity findFirstByOrderIdentifier(OrderIdentifier orderIdentifier);

	@Override
	Streamable<BookingEntity> findAll();

	//Iterable<BookingEntity> findBookingsByUuidEvents(String event);

	//Iterable<BookingEntity> findById(OrderIdentifier id);

}
