package fewodre.bookings;

import java.time.LocalDate;
import java.util.ArrayList;

public class Month {
	public java.time.Month month;
	public ArrayList<Date> dates;

	public Month(int year, java.time.Month month, int dateCount){
		this.month = month;
		this.dates = new ArrayList<Date>();
		for(int i = 1; i <= dateCount; i++){
			dates.add(new Date(LocalDate.of(year, month.getValue(), i)));
		}
	}

	public ArrayList<Date> getDates() {
		return dates;
	}

	public java.time.Month getMonth() {
		return month;
	}

	@Override
	public String toString() {
		String result = month.name();
		result += ": ";
		for(int i = 0; i < dates.size(); i++){
			result += dates.get(i).toString();
			result += ", ";
		}
		result += "\n";
		return result;
	}

	public void BookDate(int day){
		dates.get(day).setBooked();
	}
}
