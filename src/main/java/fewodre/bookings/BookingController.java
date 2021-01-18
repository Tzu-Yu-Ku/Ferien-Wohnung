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

	/**
	 * View lastname from the current{@link AccountEntity}
	 *
	 * @param model must not be {@literal null}
	 */
	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (! authentication.getPrincipal().equals("anonymousUser") &&  ! authentication.getName().equals("admin") ) {
			System.out.println("authentication: ");
			System.out.println(authentication.getPrincipal());
			model.addAttribute("firstname", accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		}
	}


	/**
	 * Creates a new {@link BookingController} with the given Parameters.
	 *
	 * @param accountManagement must not be {@literal null}
	 * @param accountRepository must not be {@literal null}
	 * @param bookingRepository must not be {@literal null}
	 * @param holidayHomeCatalog must not be {@literal null}
	 * @param bookingManagement must not be {@literal null}
	 */
	public BookingController(AccountManagement accountManagement,AccountRepository accountRepository,
	                         BookingRepository bookingRepository, HolidayHomeCatalog holidayHomeCatalog,
	                         BookingManagement bookingManagement){
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

	/**
	 * Show a list of all {@link BookingEntity}s which is booked from the current {@link AccountEntity}(who hat the role of TENANT.)
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount the current {@link AccountEntity}, must not be {@literal null}
	 * @return template: bookings
	 */
	@GetMapping("/bookings")
	@PreAuthorize("hasRole('TENANT')")
	public String bookings(Model model, @LoggedIn UserAccount userAccount){
		firstname(model);
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

	/**
	 * Show a list of all {@link BookingEntity}s which the booked house belongs to the current {@link AccountEntity}(which hat the role of HOST.)
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount the current {@link AccountEntity}, must not be {@literal null}
	 * @return template: bookinghistory
	 */
	@GetMapping("/bookinghistory")
	@PreAuthorize("hasRole('HOST')")
	public String bookingsByHost(Model model, @LoggedIn UserAccount userAccount){
		firstname(model);
		model.addAttribute("bookings", bookingRepository.findAllByUuidHost(userAccount.getEmail()));
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	/**
	 * Show a list of all {@link BookingEntity}s with the given state,
	 * which the booked house belongs to the current {@link AccountEntity}(which hat the role of HOST.)
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount the current {@link AccountEntity}, must not be {@literal null}
	 * @param state the given state to query,must not be {@literal null}
	 * @return template: bookinghistory
	 */
	@PostMapping("/bookingsFiltered")
	@PreAuthorize("hasRole('HOST')")
	public String sortByState(Model model,@LoggedIn UserAccount userAccount, @RequestParam("state") String state){
		firstname(model);
		model.addAttribute("bookings", bookingManagement.findByState(state,userAccount.getEmail()));
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	/**
	 * Show a list of all {@link BookingEntity}s with the given lastname from the TENANT,
	 * which the booked house belongs to the current {@link AccountEntity}(which hat the role of HOST.)
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount the current {@link AccountEntity}, must not be {@literal null}
	 * @param tenantName the given tenant's lastname to query,must not be {@literal null}
	 * @return template: bookinghistory
	 */
	@PostMapping("/searchByName")
	@PreAuthorize("hasRole('HOST')")
	public String searchByLastname(Model model,@LoggedIn UserAccount userAccount, @RequestParam("lastname")String tenantName){
		firstname(model);
		model.addAttribute("bookings", bookingManagement.findByTenantName(tenantName,userAccount.getEmail()));
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	/**
	 * Show a list of all {@link BookingEntity}s with the given house's name,
	 * which the booked house belongs to the current {@link AccountEntity}(which hat the role of HOST.)
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount must not be {@literal null}
	 * @param homeName the given house's name to query,must not be {@literal null}
	 * @return template: bookinghistory
	 */
	@PostMapping("/searchByHomeName")
	@PreAuthorize("hasRole('HOST')")
	public String searchByHomeName(Model model,@LoggedIn UserAccount userAccount, @RequestParam("homename")String homeName){
		firstname(model);
		model.addAttribute("bookings", bookingManagement.findByHomeName(homeName,userAccount.getEmail()));
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

}
