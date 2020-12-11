package fewodre.bookings;

public class BookingState {
	private State state;
	private Ordered ordered;
	private Paid paid;
	private Confirmed confirmed;
	private Acquired acquired;
	private Completed completed;
	private Canceled canceled;

	public BookingState(){
		this.state = new Ordered();
		this.ordered = new Ordered();
		this.paid = new Paid();
		this.confirmed = new Confirmed();
		this.acquired = new Acquired();
		this.completed = new Completed();
		this.canceled = new Canceled();
	}

	public BookingState(BookingStateEnum startState){
		this.ordered = new Ordered();
		this.paid = new Paid();
		this.confirmed = new Confirmed();
		this.acquired = new Acquired();
		this.completed = new Completed();
		this.canceled = new Canceled();
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

		public boolean checkTime(){throw new IllegalStateException();}

		public boolean pay(){return false;}

		public abstract BookingStateEnum toEnum();
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
		public BookingStateEnum toEnum() {
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
		public BookingStateEnum toEnum() {
			return BookingStateEnum.PAID;
		}
	}

	public class Confirmed extends State{
		@Override
		public BookingStateEnum toEnum() {
			return BookingStateEnum.CONFIRMED;
		}
	}

	public class Acquired extends State{
		@Override
		public BookingStateEnum toEnum() {
			return BookingStateEnum.ACQUIRED;
		}
	}

	public class Completed extends State{
		@Override
		public boolean cancel() {
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
		public BookingStateEnum toEnum() {
			return BookingStateEnum.CANCELED;
		}
	}
}
