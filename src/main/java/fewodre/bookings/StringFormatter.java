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

public class StringFormatter {
	private final Map<String, String> monthTransDict = new HashMap<>();

	public StringFormatter() {
		monthTransDict.put("JANUARY", "Januar");
		monthTransDict.put("FEBRUARY", "Februar");
		monthTransDict.put("MARCH", "März");
		monthTransDict.put("APRIL", "April");
		monthTransDict.put("May", "Mai");
		monthTransDict.put("JUNE", "Juni");
		monthTransDict.put("JULY", "Juli");
		monthTransDict.put("AUGUST", "August");
		monthTransDict.put("SEPTEMBER", "September");
		monthTransDict.put("NOVEMBER", "November");
		monthTransDict.put("DECEMBER", "Dezember");
	}

	public String formatDate(LocalDate date) {
		return date.getDayOfMonth() + ". " + monthTransDict.get(date.getMonth().toString()) + " " + date.getYear();
	}

	public String parsePrice(MonetaryAmount Price) {
		return Price.getNumber().doubleValue()
				+ (Price.getCurrency().toString().equals("EUR") ? " €" : Price.getCurrency().toString());
	}

	public String parsePrice(double Price) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		if (Price < 0) {
			return "- " + df.format((-1) * Price) + " €";
		}
		return df.format(Price) + " €";
	}

	public boolean DateIsBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
		return (date.isBefore(endDate) && date.isAfter(startDate)) || date.isEqual(startDate) || date.isEqual(endDate);
	}

	public boolean cartContainsProduct(Cart cart, Product product) {
		Iterator<CartItem> iter = cart.stream().iterator();
		while (iter.hasNext()) {
			System.out.println("1");
			CartItem item = iter.next();
			if (item.getProduct().getId().equals(product.getId())) {
				return true;
			}
		}
		return false;
	}

}
