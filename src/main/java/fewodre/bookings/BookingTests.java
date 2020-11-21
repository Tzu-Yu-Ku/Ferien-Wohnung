package fewodre.bookings;


import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.useraccounts.AccountEntity;
import fewodre.utils.Place;
import org.junit.Test;

public class BookingTests {
	@Test
	public void TestRepository() {
		// Create User!?
		AccountEntity user = null;
		HolidayHome testHome = new HolidayHome();
		testHome.setName("Anus");
		testHome.setDescription("Haha");
		testHome.setPlace(new Place("straße", "nr", "plz", "dresden", 1, 1));
		testHome.setBookable(true);
		testHome.setHostUuid("abcd-efgh-jkli");
		testHome.setImage("house2.png");
		BookingEntity entity = new BookingEntity(user.getAccount(), testHome.getId().toString());
	}
}
