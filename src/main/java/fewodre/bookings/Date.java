package fewodre.bookings;

import org.salespointframework.order.OrderStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

@Deprecated
public class Date {
	private LocalDate date;
	private Month month;
	private int dayOfMonth;
	private DayOfWeek dayOfWeek;

	private OrderStatus state;

	public Date(LocalDate date){
		this.date = date;
		this.month = date.getMonth();
		this.dayOfMonth = date.getDayOfMonth();
		this.dayOfWeek = date.getDayOfWeek();
		this.state = OrderStatus.CANCELLED; // Cancelled isomorph zu frei
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public Month getMonth() {
		return month;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setState(OrderStatus state) {
		this.state = state;
	}

	public void setBooked(){
		this.state = OrderStatus.PAID;
	}

	public OrderStatus getState() {
		return state;
	}

	@Override
	public String toString(){
		return dayOfWeek.toString().substring(0,2) + dayOfMonth;
	}

	public  boolean isBooked(){
		return !(this.state == OrderStatus.CANCELLED);
	}
}
