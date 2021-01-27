package fewodre.bookings;

import fewodre.utils.ProxyBusinessTime;
import org.aspectj.weaver.ast.Or;
import org.salespointframework.order.Order;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

/**
 * State of a {@link BookingEntity} by the State Pattern.
 * For managing the lifecycle of a booking more easily
 *
 * @author jimmy
 */
public class BookingState {
	private State state;
	private Ordered ordered;
	private Paid paid;
	private Confirmed confirmed;
	private Acquired acquired;
	private Completed completed;
	private Canceled canceled;

	private LocalDate payDeadline;
	private LocalDate freeCancelDeadline;
	private LocalDate arrivalDate;

	/**
	 * Creates a new {@link BookingState} with a default state of {@link Ordered}
	 */
	public BookingState() {
		this.state = new Ordered();
		this.ordered = new Ordered();
		this.paid = new Paid();
		this.confirmed = new Confirmed();
		this.acquired = new Acquired();
		this.completed = new Completed();
		this.canceled = new Canceled();
		this.payDeadline = LocalDate.now();
		this.arrivalDate = LocalDate.now();
		this.freeCancelDeadline = this.arrivalDate.minus(7, ChronoUnit.DAYS);
	}

	/**
	 * Creates a new {@link BookingState} with a default state of {@link Ordered}
	 * It takes two local dates to calculate when it shall switch to {@link Acquired} or {@link Canceled}
	 * when the host doesn't accept the payment in time.
	 *
	 * @param bookingDate
	 * @param arrivalDate
	 */
	public BookingState(LocalDate bookingDate, LocalDate arrivalDate) {
		this.state = new Ordered();
		this.ordered = new Ordered();
		this.paid = new Paid();
		this.confirmed = new Confirmed();
		this.acquired = new Acquired();
		this.completed = new Completed();
		this.canceled = new Canceled();
		this.payDeadline = bookingDate.plus(14, ChronoUnit.DAYS).isBefore(arrivalDate) ?
				bookingDate.plus(14, ChronoUnit.DAYS) : arrivalDate;
		this.freeCancelDeadline = arrivalDate.minus(7, ChronoUnit.DAYS);
		this.arrivalDate = arrivalDate;
	}

	/**
	 * Creates a new {@link BookingState} with a default state of {@link Ordered}
	 * It takes a {@link BookingStateEnum} to override the default state with the
	 * equivalent of the given value
	 *
	 * @param startState
	 */
	public BookingState(BookingStateEnum startState) {
		this.ordered = new Ordered();
		this.paid = new Paid();
		this.confirmed = new Confirmed();
		this.acquired = new Acquired();
		this.completed = new Completed();
		this.canceled = new Canceled();
		this.payDeadline = LocalDate.now();
		this.freeCancelDeadline = LocalDate.now();
		this.arrivalDate = LocalDate.now();
		switch (startState) {
			case ORDERED:
				this.state = this.ordered;
				break;

			case PAID:
				this.state = this.paid;
				break;

			case CONFIRMED:
				this.state = this.confirmed;
				break;

			case ACQUIRED:
				this.state = this.acquired;
				break;

			case COMPLETED:
				this.state = this.completed;
				break;

			case CANCELED:
				this.state = this.canceled;
				break;

			default:
				this.state = this.ordered;
				break;
		}
	}

	/**
	 * Creates a new {@link BookingState} with a default state of {@link Ordered}
	 * <p>
	 * It takes a {@link BookingStateEnum} to override the default state with the
	 * equivalent of the given value
	 * <p>
	 * It takes two local dates to calculate when it shall switch to {@link Acquired}
	 * or {@link Canceled} when the host doesn't accept the payment in time.
	 *
	 * @param startState
	 * @param bookingDate
	 * @param arrivalDate
	 */
	public BookingState(BookingStateEnum startState, LocalDate bookingDate,
			/*LocalDate freeCancelDeadline,*/ LocalDate arrivalDate) {
		this.ordered = new Ordered();
		this.paid = new Paid();
		this.confirmed = new Confirmed();
		this.acquired = new Acquired();
		this.completed = new Completed();
		this.canceled = new Canceled();
		this.payDeadline = bookingDate.plus(14, ChronoUnit.DAYS).isBefore(arrivalDate) ?
				bookingDate.plus(14, ChronoUnit.DAYS) : arrivalDate;
		this.arrivalDate = arrivalDate;
		this.freeCancelDeadline = arrivalDate.minus(7, ChronoUnit.DAYS);
		switch (startState) {
			case ORDERED:
				this.state = this.ordered;
				break;

			case PAID:
				this.state = this.paid;
				break;

			case CONFIRMED:
				this.state = this.confirmed;
				break;

			case ACQUIRED:
				this.state = this.acquired;
				break;

			case COMPLETED:
				this.state = this.completed;
				break;

			case CANCELED:
				this.state = this.canceled;
				break;

			default:
				this.state = this.ordered;
				break;
		}
	}

	/**
	 * Cancels the booking and adds at the {@link Order} a negative
	 * {@link org.salespointframework.order.ChargeLine} to show the
	 * refund and new Price.
	 * Returns true if no error occured.
	 *
	 * @param order
	 * @return
	 */
	public boolean cancel(Order order) {
		if (!state.cancel(order)) {
			throw new IllegalStateException();
		} else {
			return true;
		}
	}

	/**
	 * Confirms the receipt of payment by the host.
	 * Returns true if no error occured.
	 *
	 * @return
	 */
	public boolean confirm() {
		if (!state.confirm()) {
			throw new IllegalStateException();
		} else {
			return true;
		}
	}

	/**
	 * Checks current Time and returns true if it had to change
	 * it's state accordingly to the Time.
	 *
	 * @return
	 */
	public boolean checkTime() {
		if (!state.checkTime()) {
			//nothing changed
			return false;
		} else {
			// Zeit ist abgelaufen -> etwas hat sich verändert
			return true;
		}
	}

	/**
	 * Pays the booking.
	 * Returns true if no error occurred.
	 *
	 * @return
	 */
	public boolean pay() {
		if (!state.pay()) {
			throw new IllegalStateException();
		} else {
			return true;
		}
	}

	/**
	 * Returns the equivalent in {@link BookingStateEnum} to the current State.
	 *
	 * @return
	 */
	public BookingStateEnum toEnum() {
		return state.toEnum();
	}

	/*
	Inner Classes
	 */
	private abstract class State {
		/**
		 * Cancels the {@link BookingEntity} as long as it isn't completed, yet.
		 * If it's conform with the current State it will change the State of
		 * the {@link BookingState} to {@link Canceled}
		 *
		 * @return
		 */
		public boolean cancel(Order order) {
			System.out.println("Old Price: " + order.getTotal());
			order.addChargeLine(order.getTotal().multiply(-1), "Storniert");
			System.out.println("New Price: " + order.getTotal());
			state = canceled;
			return true;
		}

		/**
		 * Confirms the receipt of payment.
		 * If it's conform with the current State it will change the State of
		 * the {@link BookingState} to {@link Confirmed}
		 *
		 * @return
		 */
		public boolean confirm() {
			return false;
		}

		/**
		 * Checks current Time and returns true if it had to change
		 * it's state accordingly to the Time and the current State.
		 *
		 * @return
		 */
		public abstract boolean checkTime();

		/**
		 * Pays the {@link BookingEntity} as long as it is still {@link Ordered}, yet.
		 * If it's conform with the current State it will change the State of
		 * the {@link BookingState} to {@link Canceled}
		 *
		 * @return
		 */
		public boolean pay() {
			return false;
		}

		/**
		 * Returns the equivalent in {@link BookingStateEnum} to the current State.
		 *
		 * @return
		 */
		public abstract BookingStateEnum toEnum();

		/**
		 * Returns the equivalent in {@link BookingStateEnum} to the current State as a {@link String}.
		 *
		 * @return
		 */
		@Override
		public String toString() {
			return toEnum().toString();
		}
	}

	/**
	 * The class equivalent to the Ordered State by the state pattern.
	 */
	public class Ordered extends State {
		@Override
		public boolean confirm() {
			state = confirmed;
			return true;
		}

		@Override
		public boolean pay() {
			state = paid;
			return true;
		}

		@Override
		public boolean cancel(Order order) {
			state = canceled;
			return true;
		}

		@Override
		public boolean checkTime() {
			if (payDeadline.isBefore(ProxyBusinessTime.getBusinessTime().getTime().toLocalDate())) {
				state = canceled;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public BookingStateEnum toEnum() {
			if (checkTime()) {
				return BookingState.this.toEnum();
			}
			return BookingStateEnum.ORDERED;
		}
	}

	/**
	 * The class equivalent to the Paid State by the state pattern.
	 */
	public class Paid extends State {
		@Override
		public boolean confirm() {
			state = confirmed;
			return true;
		}

		@Override
		public boolean checkTime() {
			if (payDeadline.isBefore(ProxyBusinessTime.getBusinessTime().getTime().toLocalDate())) {
				state = canceled;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public BookingStateEnum toEnum() {
			if (checkTime()) {
				return BookingState.this.toEnum();
			}
			return BookingStateEnum.PAID;
		}
	}

	/**
	 * The class equivalent to the Confirmed State by the state pattern.
	 */
	public class Confirmed extends State {
		@Override
		public boolean checkTime() {
			if (freeCancelDeadline.isBefore(ProxyBusinessTime.getBusinessTime().getTime().toLocalDate())) {
				state = acquired;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public BookingStateEnum toEnum() {
			if (checkTime()) {
				return BookingState.this.toEnum();
			}
			return BookingStateEnum.CONFIRMED;
		}
	}

	/**
	 * The class equivalent to the Acquired State by the state pattern.
	 */
	public class Acquired extends State {

		public boolean cancel(Order order) {
			System.out.println("Old Price: " + order.getTotal());
			System.out.println("New Price: " + order.getTotal());
			state = canceled;
			return true;
		}

		@Override
		public boolean checkTime() {
			if (arrivalDate.isBefore(ProxyBusinessTime.getBusinessTime().getTime().toLocalDate())) {
				state = canceled;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public BookingStateEnum toEnum() {
			if (checkTime()) {
				return BookingState.this.toEnum();
			}
			return BookingStateEnum.ACQUIRED;
		}
	}

	/**
	 * The class equivalent to the Completed State by the state pattern.
	 */
	public class Completed extends State {
		@Override
		public boolean cancel(Order order) {
			return false;
		}

		@Override
		public boolean checkTime() {
			return false;
		}

		@Override
		public BookingStateEnum toEnum() {
			return BookingStateEnum.COMPLETED;
		}
	}

	/**
	 * The class equivalent to the Canceled State by the state pattern.
	 */
	public class Canceled extends State {
		@Override
		public boolean cancel(Order order) {
			return false;
		}

		@Override
		public boolean checkTime() {
			return false;
		}

		@Override
		public BookingStateEnum toEnum() {
			return BookingStateEnum.CANCELED;
		}
	}
}
