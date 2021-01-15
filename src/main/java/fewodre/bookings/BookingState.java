package fewodre.bookings;

import fewodre.utils.ProxyBusinessTime;
import org.aspectj.weaver.ast.Or;
import org.salespointframework.order.Order;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public class BookingState {
	private State state;
	private Ordered ordered = new Ordered();
	private Paid paid = new Paid();
	private Confirmed confirmed = new Confirmed();
	private Acquired acquired = new Acquired();
	private Completed completed = new Completed();
	private Canceled canceled = new Canceled();

	private LocalDate payDeadline;
	private LocalDate freeCancelDeadline;
	private LocalDate arrivalDate;

	public BookingState() {
		this.state = new Ordered();
		this.payDeadline = LocalDate.now();
		this.arrivalDate = LocalDate.now();
		this.freeCancelDeadline = this.arrivalDate.minus(7, ChronoUnit.DAYS);
	}

	public BookingState(LocalDate bookingDate, LocalDate arrivalDate) {
		this.state = new Ordered();
		this.payDeadline = bookingDate.plus(14, ChronoUnit.DAYS).isBefore(arrivalDate) ?
				bookingDate.plus(14, ChronoUnit.DAYS) : arrivalDate;
		this.freeCancelDeadline = arrivalDate.minus(7, ChronoUnit.DAYS);
		this.arrivalDate = arrivalDate;
	}

	public BookingState(BookingStateEnum startState) {
		this.payDeadline = LocalDate.now();
		this.freeCancelDeadline = LocalDate.now();
		this.arrivalDate = LocalDate.now();
		deciceState(startState);
	}

	public BookingState(BookingStateEnum startState, LocalDate bookingDate,
			/*LocalDate freeCancelDeadline,*/ LocalDate arrivalDate) {
		this.payDeadline = bookingDate.plus(14, ChronoUnit.DAYS).isBefore(arrivalDate) ?
				bookingDate.plus(14, ChronoUnit.DAYS) : arrivalDate;
		this.arrivalDate = arrivalDate;
		this.freeCancelDeadline = arrivalDate.minus(7, ChronoUnit.DAYS);
		deciceState(startState);
	}

	public boolean cancel(Order order) {
		if (!state.cancel(order)) {
			throw new IllegalStateException();
		} else {
			return true;
		}
	}

	public boolean confirm() {
		if (!state.confirm()) {
			throw new IllegalStateException();
		} else {
			return true;
		}
	}

	public boolean checkTime() {
		if (!state.checkTime()) {
			//nothing changed
			return false;
		} else {
			// Zeit ist abgelaufen -> etwas hat sich verändert
			return true;
		}
	}

	public boolean pay() {
		if (!state.pay()) {
			throw new IllegalStateException();
		} else {
			return true;
		}
	}

	private void deciceState(BookingStateEnum startState) {
		switch (startState) {
			case PAID:
				this.state = this.paid;

			case CONFIRMED:
				this.state = confirmed;

			case ACQUIRED:
				this.state = acquired;

			case COMPLETED:
				this.state =completed;

			case CANCELED:
				this.state = canceled;

			default:
				this.state = this.ordered;
		}
	}

	public BookingStateEnum toEnum() {
		return state.toEnum();
	}

	/*
	Inner Classes
	 */
	private abstract class State {
		public boolean cancel(Order order) {
			System.out.println("Old Price: " + order.getTotal());
			order.addChargeLine(order.getTotal().multiply(-1), "Storniert");
			System.out.println("New Price: " + order.getTotal());
			state = canceled;
			return true;
		}

		public boolean confirm() {
			return false;
		}

		public abstract boolean checkTime();

		public boolean pay() {
			return false;
		}

		public abstract BookingStateEnum toEnum();

		@Override
		public String toString() {
			return toEnum().toString();
		}
	}

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
