package fewodre.bookings;

import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.salespointframework.order.OrderStatus;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatesUnitTest {

	@Test
	void testYear() {
		Year testYear = new Year(2021);
		assertThat(testYear.getYearNumber()).isEqualTo(2021);
		assertThat(testYear.getDaysOfYear()).isEqualTo(365);
		assertThat(testYear.getMonths().size()).isEqualTo(12);
		assertThat(testYear.toString()).isEqualTo(testYear.toString());
	}

	@Test
	void testMonth() {
		java.time.Month month = java.time.Month.of(1);
		Month testMonth = new Month(2021, month, 31);
		assertThat(testMonth.getMonth()).isEqualTo(month);
		assertThat(testMonth.getDates().size()).isEqualTo(31);
	}

	@Test
	void testDate() {
		LocalDate dateToday = LocalDate.now();
		Date testDate = new Date(dateToday);
		assertThat(testDate.getLocalDate()).isEqualTo(dateToday);
		assertThat(testDate.getDayOfMonth()).isEqualTo(dateToday.getDayOfMonth());
		assertThat(testDate.getMonth()).isEqualTo(dateToday.getMonth());
		assertThat(testDate.getDayOfWeek()).isEqualTo(dateToday.getDayOfWeek());
		assertThat(testDate.getState()).isEqualTo(OrderStatus.CANCELLED);
		assertThat(testDate.isBooked()).isFalse();

		testDate.setState(OrderStatus.PAID);
		assertThat(testDate.getState()).isEqualTo(OrderStatus.PAID);

		testDate.setBooked();
		assertThat(testDate.getState()).isEqualTo(OrderStatus.PAID);
	}

}