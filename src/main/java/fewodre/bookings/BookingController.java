package fewodre.bookings;

import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Iterator;

@Controller
@PreAuthorize("isAuthenticated()")
public class BookingController {

	private final BookingRepository bookingRepository;
	private final AccountManagement accountManagement;
	private UserAccount userAccount;
	private StringFormatter formatter;
	private HolidayHomeCatalog holidayHomeCatalog;

	public BookingController(AccountManagement accountManagement,BookingRepository bookingRepository, HolidayHomeCatalog holidayHomeCatalog){
		Assert.notNull(accountManagement, "AccountManagement must not be null!");
		Assert.notNull(bookingRepository, "BookingRepository must not be null!");
		Assert.notNull(holidayHomeCatalog, "HomeCatalog must not be null!");
		this.accountManagement = accountManagement;
		this.bookingRepository = bookingRepository;
		this.formatter = new StringFormatter();
		this.holidayHomeCatalog = holidayHomeCatalog;
	}

	@GetMapping("/bookings")
	@PreAuthorize("hasRole('TENANT')")
	public String bookings(Model model, @LoggedIn UserAccount userAccount){
		if(!bookingRepository.findBookingEntityByUserAccount(userAccount).iterator().hasNext()){
			return "redirect:/holidayhomes";
		}else {
			model.addAttribute("homeCatalog", this.holidayHomeCatalog);
			model.addAttribute("bookings", bookingRepository.findBookingEntityByUserAccount(userAccount));
			Iterator<BookingEntity> iter = bookingRepository.findBookingEntityByUserAccount(userAccount).iterator();
			while (iter.hasNext()){
				BookingEntity bookingEntity = iter.next();
				System.out.println(bookingEntity.getId() + bookingEntity.getState().toString());
			}
			model.addAttribute("formatter", this.formatter);
			return "bookings";
		}
	}

	@GetMapping("/bookingsFromHost")
	@PreAuthorize("hasRole('HOST')")
	public String bookingsByHost(Model model, @LoggedIn AccountEntity accountEntity){
		//you have to give in the same HostUUID when you create the home, and they shouldn't let host give in HostUUID should be automatik filled in
		model.addAttribute("bookings", bookingRepository.findBookingsByUuidHostEquals("123123"));
		model.addAttribute("homeCatalog", this.holidayHomeCatalog);
		Iterator<BookingEntity> iter = bookingRepository.findBookingsByUuidHostEquals(accountEntity.getUuid()).iterator();
		while (iter.hasNext()){
			BookingEntity bookingEntity = iter.next();
			System.out.println(bookingEntity.getId() + bookingEntity.getState().toString());
		}
		model.addAttribute("formatter", this.formatter);
		return "bookings";
	}

}
