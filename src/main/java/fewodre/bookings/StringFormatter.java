package fewodre.bookings;

import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;

import javax.money.MonetaryAmount;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StringFormatter{
	private Map<String, String> MonthTransDict = new HashMap<String, String>();

	/**
	 * Creates a new Custom Formatter for often used funtions in the frontend like parsing Dates and prices.
	 */
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

	/**
	 * Formats a {@link LocalDate} to a String in the Format DD.Monat.YYYY
	 * @param date
	 * @return
	 */
	public String formatDate(LocalDate date){
		return date.getDayOfMonth() + ". " + MonthTransDict.get(date.getMonth().toString()) + " " + date.getYear();
	}

	/**
	 * Parses a {@link MonetaryAmount} to the format P...P.PPPP... €
	 * @param Price
	 * @return
	 */
	public String parsePrice(MonetaryAmount Price){
		float PriceValue = ((int)(Price.getNumber().floatValue()*100))*0.01f;
		return PriceValue + (Price.getCurrency().toString().equals("EUR") ? " €" : Price.getCurrency().toString());
	}

	/**
	 * Parses a {@link Double} to the format P...P.PPPP... €
	 * @param Price
	 * @return
	 */
	public String parsePrice(double Price){
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		if(Price < 0){
			return "- " + df.format((-1)*Price) + " €";
		}
			return df.format(Price) + " €";
	}

	/**
	 * Parses a {@link Float} to the format P...P.PPPP... €
	 * @param Price
	 * @return
	 */
	public String parsePrice(float Price){
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		if(Price < 0){
			return "- " + df.format((-1)*Price) + " €";
		}
			return df.format(Price) + " €";
	}

	/**
	 * Returns True if the {@link LocalDate} date is between the startDate and the endDate.
	 * For easy checking in the Frontend.
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public boolean DateIsBetween(LocalDate date, LocalDate startDate, LocalDate endDate){
		return (date.isBefore(endDate) && date.isAfter(startDate)) || date.isEqual(startDate) || date.isEqual(endDate);
	}

	/**
	 * Returns True if the given Cart already contains the given Product
	 * 
	 * @param cart
	 * @param product
	 * @return
	 */
	public boolean cartContainsProduct(Cart cart, Product product){
		Iterator<CartItem> iter = cart.stream().iterator();
		while(iter.hasNext()){
			System.out.println("1");
			CartItem item = iter.next();
			if(item.getProduct().getId().equals(product.getId())){
				return true;
			}
		}
		return false;
	}

}
