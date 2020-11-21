package fewodre.bookings;

import fewodre.useraccounts.AccountEntity;
import org.springframework.data.repository.CrudRepository;

public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

	Iterable<BookingEntity> findBookingsByHost(String host){};

	Iterable<BookingEntity> findBookingsByTenant(String tenant){};

	Iterable<BookingEntity> findBookingsByHolidayHome(String holidayHome){};

	Iterable<BookingEntity> findBookingsByEvent(String event){};





}
