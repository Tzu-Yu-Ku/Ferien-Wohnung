package fewodre.bookings;

import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;

@Controller
@PreAuthorize("isAuthenticated()")
public class BookingController {

	private final BookingRepository bookingRepository;
	private final AccountManagement accountManagement;
	private UserAccount userAccount;
	private StringFormatter formatter;
	private HolidayHomeCatalog holidayHomeCatalog;
	private final AccountRepository accountRepository;
	private Authentication authentication;
	private final BookingManagement bookingManagement;

	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (! authentication.getPrincipal().equals("anonymousUser") &&  ! authentication.getName().equals("admin") ) {
			System.out.println("authentication: ");
			System.out.println(authentication.getPrincipal());
			model.addAttribute("firstname", accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		}
	}

	public BookingController(AccountManagement accountManagement,AccountRepository accountRepository, BookingRepository bookingRepository, HolidayHomeCatalog holidayHomeCatalog, BookingManagement bookingManagement){
		Assert.notNull(accountManagement, "AccountManagement must not be null!");
		Assert.notNull(bookingRepository, "BookingRepository must not be null!");
		Assert.notNull(holidayHomeCatalog, "HomeCatalog must not be null!");
		Assert.notNull(bookingManagement, "BookingManagement must not be null!");
		this.accountManagement = accountManagement;
		this.bookingRepository = bookingRepository;
		this.formatter = new StringFormatter();
		this.holidayHomeCatalog = holidayHomeCatalog;
		this.accountRepository = accountRepository;
		this.bookingManagement = bookingManagement;
	}

	@GetMapping("/bookings")
	@PreAuthorize("hasRole('TENANT')")
	public String bookings(Model model, @LoggedIn UserAccount userAccount){
		firstname(model);
			//model.addAttribute("userAccount", accountManagement.getRepository().findByAccount_Email(userAccount.getEmail()));
		if(!bookingRepository.findBookingEntityByUserAccount(userAccount).iterator().hasNext()){
			return "redirect:/holidayhomes";
		}else {
			model.addAttribute("homeCatalog", this.holidayHomeCatalog);
			model.addAttribute("bookings", bookingRepository.findBookingEntityByUserAccount(userAccount));
			Iterator<BookingEntity> iter = bookingRepository.findBookingEntityByUserAccount(userAccount).iterator();
			while (iter.hasNext()){
				BookingEntity bookingEntity = iter.next();
				System.out.println(bookingEntity.getId() + bookingEntity.getState().toString());
				System.out.println(bookingEntity.getId() + bookingEntity.getState().toString());
				System.out.println(bookingEntity.getId() + bookingEntity.getState().toString());
				System.out.println(bookingEntity.getId() + bookingEntity.getState().toString());
			}
			model.addAttribute("formatter", this.formatter);
			return "bookings";
		}
	}

	@GetMapping("/bookinghistory")
	@PreAuthorize("hasRole('HOST')")
	public String bookingsByHost(Model model, @LoggedIn UserAccount userAccount){
		firstname(model);
		model.addAttribute("bookings", bookingRepository.findAllByUuidHost(userAccount.getEmail()));
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	@PostMapping("/bookingsFiltered")
	@PreAuthorize("hasRole('HOST')")
	public String sortByState(Model model,@LoggedIn UserAccount userAccount, @RequestParam("state") String state){
		firstname(model);
		model.addAttribute("bookings", bookingManagement.findByState(state));
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	@PostMapping("/searchByName")
	@PreAuthorize("hasRole('HOST')")
	public String searchByLastname(Model model,@LoggedIn UserAccount userAccount, @RequestParam("lastname")String tenantName){
		firstname(model);
		model.addAttribute("bookings", bookingManagement.findByTenantName(tenantName));
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	@PostMapping("/searchByHomeName")
	@PreAuthorize("hasRole('HOST')")
	public String searchByHomeName(Model model,@LoggedIn UserAccount userAccount, @RequestParam("homename")String homeName){
		firstname(model);
		model.addAttribute("bookings", bookingManagement.findByHomeName(homeName));
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

}
