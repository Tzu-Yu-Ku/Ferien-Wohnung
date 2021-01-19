package fewodre.bookings;

import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
* A repository interface to manage {@link BookingEntity} instances.
*/
public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

	static final Sort DEFAULT_SORT = Sort.by("arrivalDate").ascending();

	/**
	 * Returns all {@link BookingEntity}s with the given host.
	 *
	 * @param host must not be {@literal null}.
	 * @return the bookingEntities of the given host
	 */
	Iterable<BookingEntity> findBookingsByUuidHostEquals(String host);

	/**
	 * Returns all {@link BookingEntity}s with the given host's ID.
	 *
	 * @param host must not be {@literal null}.
	 * @return the bookingEntities of the given host's ID
	 */
	Streamable<BookingEntity> findAllByUuidHost(String host, Sort sort);

	/**
	 * Returns all {@link BookingEntity}s with the given host's ID, ordered by the arrival's date.
	 *
	 * @param host must not be {@literal null}.
	 * @return the bookingEntities of the given host's ID
	 */
	default Streamable<BookingEntity> findAllByUuidHost(String host){
		return findAllByUuidHost(host,DEFAULT_SORT);
	}

	/**
	 * Returns all {@link BookingEntity}s with the given tenant's ID.
	 *
	 * @param tenant must not be {@literal null}.
	 * @return the bookingEntities of the given tenant's ID
	 */
	Iterable<BookingEntity> findBookingsByUuidTenant(String tenant);

	/**
	 * Returns all {@link BookingEntity}s with the given userAccount.
	 *
	 * @param userAccount must not be {@literal null}.
	 * @return the bookingEntities of the given userAccount
	 */
	Iterable<BookingEntity> findBookingEntityByUserAccount(UserAccount userAccount);

	/**
	 * Returns all {@link BookingEntity}s with the given holidayHome's name.
	 *
	 * @param holidayHome must not be {@literal null}.
	 * @return the bookingEntities of the given holidayHome's name
	 */
	Streamable<BookingEntity> findBookingsByUuidHome(String holidayHome);

	/**
	 * Returns all {@link BookingEntity}s with the given orderIdentifier(in type of OrderIdentifier).
	 *
	 * @param orderIdentifier must not be {@literal null}.
	 * @return the bookingEntities of the given orderIdentifier
	 */
	BookingEntity findFirstByOrderIdentifier(OrderIdentifier orderIdentifier);

	/**
	 *  Returns all {@link BookingEntity}s with the given orderIdentifier(in type of String).
	 *
	 * @param orderIdentifier must not be {@literal null}.
	 * @return the bookingEntities from the given host
	 */
	BookingEntity findFirstByOrderIdentifier(String orderIdentifier);

	/**
	 * Re-declared {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	Streamable<BookingEntity> findAll();


}
