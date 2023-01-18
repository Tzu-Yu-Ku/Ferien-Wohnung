package fewodre.bookings;

import java.time.LocalDate;
import java.util.ArrayList;

@Deprecated
public class Month {
	public java.time.Month jMonth;
	public ArrayList<Date> dates;

	public Month(int year, java.time.Month month, int dateCount){
		this.jMonth = month;
		this.dates = new ArrayList<Date>();
		for(int i = 1; i <= dateCount; i++){
			dates.add(new Date(LocalDate.of(year, month.getValue(), i)));
		}
	}

	public ArrayList<Date> getDates() {
		return dates;
	}

	public java.time.Month getMonth() {
		return jMonth;
	}

	@Override
	public String toString() {
		String result = jMonth.name();
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
