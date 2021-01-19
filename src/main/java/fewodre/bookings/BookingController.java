package fewodre.bookings;

import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import org.salespointframework.useraccount.Role;
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
import java.util.List;

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
	 * Shows firstname of the current{@link AccountEntity}
	 *
	 * @param model must not be {@literal null}
	 */
	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (! authentication.getPrincipal().equals("anonymousUser") &&  ! authentication.getName().equals("admin") ) {
			System.out.println("authentication: ");
			System.out.println(authentication.getPrincipal());
			model.addAttribute("firstname",
					accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
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
	 * Shows all of {@link BookingEntity}s which are booked from the current {@link AccountEntity}(who hat the role of TENANT.)
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
			for (BookingEntity bookingEntity : bookingRepository.findBookingEntityByUserAccount(userAccount)) {
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
	 * If the current {@link AccountEntity} has role of HOST,shows all of {@link BookingEntity}s
	 * in which the booked house belongs to.Otherwise, shows all of {@link BookingEntity}s,
	 * if the current {@link AccountEntity} has the role of ADMIN.
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount the current {@link AccountEntity}, must not be {@literal null}
	 * @return template: bookinghistory
	 */
	@GetMapping("/bookinghistory")
	@PreAuthorize("hasAnyRole('HOST','ADMIN')")
	public String bookingsByHost(Model model, @LoggedIn UserAccount userAccount){
		firstname(model);
		if(userAccount.hasRole(Role.of("ADMIN"))) {
			model.addAttribute("bookings", bookingRepository.findAll());
		} else if (userAccount.hasRole(Role.of("HOST"))) {
			model.addAttribute("bookings", bookingRepository.findAllByUuidHost(userAccount.getEmail()));
		}
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	/**
	 * If the current {@link AccountEntity} has role of HOST,shows all of {@link BookingEntity}s
	 * with the given state. Otherwise, shows all of {@link BookingEntity}s with the given state,
	 * if the current {@link AccountEntity} has the role of ADMIN.
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount the current {@link AccountEntity}, must not be {@literal null}
	 * @param state the given state to query,must not be {@literal null}
	 * @return template: bookinghistory
	 */
	@PostMapping("/bookingsFiltered")
	@PreAuthorize("hasAnyRole('HOST','ADMIN')")
	public String sortByState(Model model,@LoggedIn UserAccount userAccount, @RequestParam("state") String state){
		firstname(model);
		if(userAccount.hasRole(Role.of("ADMIN"))) {
			List<BookingEntity> sortedByState = bookingRepository.findAll()
					.filter(bookingEntity -> bookingEntity.getState().toString().equals(state)).toList();
			model.addAttribute("bookings", sortedByState);
		} else if (userAccount.hasRole(Role.of("HOST"))) {
			model.addAttribute("bookings", bookingManagement.findByState(state, userAccount.getEmail()));
		}
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	/**
	 * If the current {@link AccountEntity} has role of HOST,shows all of {@link BookingEntity}s
	 * with the given lastname from the TENANT. Otherwise, shows all of {@link BookingEntity}s
	 * with the given state,if the current {@link AccountEntity} has the role of ADMIN.
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount the current {@link AccountEntity}, must not be {@literal null}
	 * @param tenantName the given tenant's lastname to query,must not be {@literal null}
	 * @return template: bookinghistory
	 */
	@PostMapping("/searchByName")
	@PreAuthorize("hasRole('HOST')")
	public String searchByLastname(Model model,@LoggedIn UserAccount userAccount,
	                               @RequestParam("lastname")String tenantName){
		firstname(model);
		if(userAccount.hasRole(Role.of("ADMIN"))) {
			List<BookingEntity> sortedByTenantName = bookingRepository.findAll().filter(bookingEntity ->
					bookingEntity.getUserAccount().getLastname().equals(tenantName)).toList();
			model.addAttribute("bookings", sortedByTenantName);
		} else if (userAccount.hasRole(Role.of("HOST"))) {
			model.addAttribute("bookings", bookingManagement.findByTenantName(tenantName, userAccount.getEmail()));
		}
		model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

	/**
	 * If the current {@link AccountEntity} has role of HOST,shows all of {@link BookingEntity}s
	 * with the given HolidayHome's name. Otherwise, shows all of {@link BookingEntity}s
	 * with the given HolidayHome's name.,if the current {@link AccountEntity} has the role of ADMIN.
	 *
	 * @param model must not be {@literal null}
	 * @param userAccount must not be {@literal null}
	 * @param homeName the given house's name to query,must not be {@literal null}
	 * @return template: bookinghistory
	 */
	@PostMapping("/searchByHomeName")
	@PreAuthorize("hasRole('HOST')")
	public String searchByHomeName(Model model,@LoggedIn UserAccount userAccount,
	                               @RequestParam("homename")String homeName){
		firstname(model);
		if(userAccount.hasRole(Role.of("ADMIN"))) {
			List<BookingEntity> sortedByHomeName = bookingRepository.findAll().filter(bookingEntity ->
					bookingEntity.getUserAccount().getLastname().equals(homeName)).toList();
			model.addAttribute("bookings", sortedByHomeName);
		} else if (userAccount.hasRole(Role.of("HOST"))) {
			model.addAttribute("bookings", bookingManagement.findByHomeName(homeName, userAccount.getEmail()));
		}model.addAttribute("formatter", this.formatter);
		return "bookinghistory";
	}

}
