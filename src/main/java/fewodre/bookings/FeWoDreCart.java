package fewodre.bookings;

import fewodre.catalog.holidayhomes.HolidayHome;
import org.salespointframework.order.Cart;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class FeWoDreCart extends Cart {
	private HolidayHome holidayHome;
	private LinkedHashMap<Integer, Year> calender;

	public FeWoDreCart(HolidayHome holidayHome){
		this.holidayHome = holidayHome;
		calender = new LinkedHashMap<Integer, Year>();
		for(int i = 0; i <= 3; i++){
			int yearNumber = java.time.Year.now().getValue()+i;
			calender.put(yearNumber, new Year(yearNumber));
		}
	}

	public void SetUpCalender(LinkedHashMap<Integer, LinkedList<Integer>> bookedDates){// Map<YearNumber, bookedDates>
		for(Map.Entry<Integer, LinkedList<Integer>> entry : bookedDates.entrySet()){
			LinkedList<Integer> bookedDatesOfTheYear = entry.getValue();
			calender.put(entry.getKey(), calender.get(entry.getKey()).SetUpBookedDates(bookedDatesOfTheYear));
		}
	}
}
