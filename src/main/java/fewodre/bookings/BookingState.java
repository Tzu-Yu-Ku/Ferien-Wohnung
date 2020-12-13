package fewodre.bookings;

import fewodre.utils.ProxyBusinessTime;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

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

	public BookingState(){
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

	public BookingState(LocalDate bookingDate, LocalDate arrivalDate){
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

	public BookingState(BookingStateEnum startState){
		this.ordered = new Ordered();
		this.paid = new Paid();
		this.confirmed = new Confirmed();
		this.acquired = new Acquired();
		this.completed = new Completed();
		this.canceled = new Canceled();
		this.payDeadline = LocalDate.now();
		this.freeCancelDeadline = LocalDate.now();
		this.arrivalDate = LocalDate.now();
		switch (startState){
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

	public BookingState(BookingStateEnum startState, LocalDate bookingDate,
						/*LocalDate freeCancelDeadline,*/ LocalDate arrivalDate){
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
		switch (startState){
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

	public boolean cancel(){
		if(!state.cancel()){
			throw new IllegalStateException();
		}
		else {return true;}
	}

	public boolean confirm(){
		if(!state.confirm()){
			throw new IllegalStateException();
		}
		else {return true;}
	}

	public boolean checkTime(){
		if(!state.checkTime()){
			//nothing changed
			return false;
		}
		else {
			// Zeit ist abgelaufen -> etwas hat sich verändert
			return true;
		}
	}

	public boolean pay(){
		if(!state.pay()){
			throw new IllegalStateException();
		}
		else {return true;}
	}

	public BookingStateEnum toEnum(){
		return state.toEnum();
	}

	/*
	Inner Classes
	 */
	private abstract class State{
		public boolean cancel(){
			state = canceled;
			return true;
		}

		public boolean confirm(){return false;}

		public abstract boolean checkTime();

		public boolean pay(){return false;}

		public abstract BookingStateEnum toEnum();

		@Override
		public String toString() {
			return toEnum().toString();
		}
	}

	public class Ordered extends State{
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
		public boolean checkTime() {
			if(payDeadline.isBefore(ProxyBusinessTime.getBusinessTime().getTime().toLocalDate())){
				state = canceled;
				return true;
			}else {return false;}
		}

		@Override
		public BookingStateEnum toEnum() {
			checkTime();
			return BookingStateEnum.ORDERED;
		}
	}

	public class Paid extends State{
		@Override
		public boolean confirm() {
			state = confirmed;
			return true;
		}

		@Override
		public boolean checkTime() {
			if(payDeadline.isBefore(ProxyBusinessTime.getBusinessTime().getTime().toLocalDate())){
				state = canceled;
				return true;
			}else {return false;}
		}

		@Override
		public BookingStateEnum toEnum() {
			checkTime();
			return BookingStateEnum.PAID;
		}
	}

	public class Confirmed extends State{
		@Override
		public boolean checkTime() {
			if(freeCancelDeadline.isBefore(ProxyBusinessTime.getBusinessTime().getTime().toLocalDate())){
				state = acquired;
				return true;
			}else {return false;}
		}

		@Override
		public BookingStateEnum toEnum() {
			checkTime();
			return BookingStateEnum.CONFIRMED;
		}
	}

	public class Acquired extends State{
		@Override
		public boolean checkTime() {
			if(arrivalDate.isBefore(ProxyBusinessTime.getBusinessTime().getTime().toLocalDate())){
				state = canceled;
				return true;
			}else {return false;}
		}

		@Override
		public BookingStateEnum toEnum() {
			checkTime();
			return BookingStateEnum.ACQUIRED;
		}
	}

	public class Completed extends State{
		@Override
		public boolean cancel() {
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

	public class Canceled extends State{
		@Override
		public boolean cancel() {
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
