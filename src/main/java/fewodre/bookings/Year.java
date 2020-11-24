package fewodre.bookings;

import java.time.LocalDate;
import java.util.ArrayList;

public class Year {
	private int yearNumber;
	private int daysOfYear;
	private ArrayList<Month> months;

	public Year(int yearNumber){
		this.yearNumber = yearNumber;
		this.daysOfYear = LocalDate.ofYearDay(yearNumber, 1).lengthOfYear();
		months = new ArrayList<Month>();
		for(int i = 1; i <= 12; i++){
			months.add(new Month(yearNumber, java.time.Month.of(i), java.time.Month.of(i).length(java.time.Year.isLeap(yearNumber))));
		}
	}

	public int getYearNumber() {
		return yearNumber;
	}

	public int getDaysOfYear() {
		return daysOfYear;
	}

	public ArrayList<Month> getMonths() {
		return months;
	}

	@Override
	public String toString() {
		String result = "Year "+yearNumber+": \n";
		for(int i = 0; i < 12; i++){
			result += months.get(i).toString();
		}
		return result;
	}
}
