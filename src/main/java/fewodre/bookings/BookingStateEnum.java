package fewodre.bookings;

/**
 * An enum which serves to save the {@link BookingState} in an simple Format.
 * For that each entry represents a booking state.
 * The entries are: Ordered, Paid, Confimed, Acquired, Completed and Canceled.
 *
 * @author jimmy
 */
public enum BookingStateEnum {
	ORDERED,
	PAID,
	CONFIRMED,
	ACQUIRED,
	COMPLETED,
	CANCELED
}
