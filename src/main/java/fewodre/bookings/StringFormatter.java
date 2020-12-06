package fewodre.bookings;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class StringFormatter{
	private Map<String, String> MonthTransDict = new HashMap<String, String>();
	public StringFormatter(){
		MonthTransDict.put("JANUARY", "Januar");
		MonthTransDict.put("FEBRUARY", "Februar");
		MonthTransDict.put("MARCH", "März");
		MonthTransDict.put("APRIL", "April");
		MonthTransDict.put("May", "Mai");
		MonthTransDict.put("JUNE", "Juni");
		MonthTransDict.put("JULY", "Juli");
		MonthTransDict.put("AUGUST", "August");
		MonthTransDict.put("SEPTEMBER", "September");
		MonthTransDict.put("NOVEMBER", "November");
		MonthTransDict.put("DECEMBER", "Dezember");
	}
	public String formatDate(LocalDate date){
		return date.getDayOfMonth() + ". " + MonthTransDict.get(date.getMonth().toString()) + " " + date.getYear();
	}
	public String parsePrice(MonetaryAmount Price){
		return Price.getNumber() + (Price.getCurrency().toString().equals("EUR") ? " €" : Price.getCurrency().toString());
	}

	public boolean DateIsBetween(LocalDate date, LocalDate startDate, LocalDate endDate){
		return (date.isBefore(endDate) && date.isAfter(startDate)) || date.isEqual(startDate) || date.isEqual(endDate);
	}

}
