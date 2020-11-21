package fewodre.bookings;

import fewodre.useraccounts.AccountEntity;
import org.springframework.data.repository.CrudRepository;

public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

	Iterable<BookingEntity> findBookingsByUuidHostEquals(String host);

	Iterable<BookingEntity> findBookingsByUuidTenant(String tenant);

	Iterable<BookingEntity> findBookingsByUuidHome(String holidayHome);

	Iterable<BookingEntity> findBookingsByUuidEvents(String event);





}
