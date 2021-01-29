package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountController;
import fewodre.useraccounts.AccountEntity;
import fewodre.useraccounts.AccountManagement;

import fewodre.useraccounts.AccountRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.OrderLine;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class CartController {

	private final AccountManagement accountManagement;
	private final AccountRepository accountRepository;
	private final BookingManagement bookingManagement;
	private final EventCatalog eventcatalog;
	private final HolidayHomeCatalog holidayHomeCatalog;
	private final static Quantity one = Quantity.of(1);
	private Authentication authentication;

	private HolidayHome holidayHome;
	private UserAccount userAccount;

	private StringFormatter formatter;

	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

	@DateTimeFormat(pattern = "dd.mm.yyyy")
	private LocalDate arrivalDate, departureDate;

	private Cart cart;

	/**
	 * Creates a new {@link CartController} with the given Parameters.
	 *
	 * @param accountManagement  muss not be {@literal null}
	 * @param accountRepository  muss not be {@literal null}
	 * @param bookingManagement  muss not be {@literal null}
	 * @param eventCatalog       muss not be {@literal null}
	 * @param holidayHomeCatalog muss not be {@literal null}
	 */
	CartController(AccountManagement accountManagement, AccountRepository accountRepository,
	               BookingManagement bookingManagement, EventCatalog eventCatalog,
	               HolidayHomeCatalog holidayHomeCatalog) {

		Assert.notNull(accountManagement, "AccountManagement must not be null!");
		Assert.notNull(bookingManagement, "BookingManagement must not be null!");
		Assert.notNull(eventCatalog, "EventCatalog must not be null!");
		Assert.notNull(holidayHomeCatalog, "HolidayHomeCatalog must not be null!");

		this.accountManagement = accountManagement;
		this.bookingManagement = bookingManagement;
		this.eventcatalog = eventCatalog;
		this.holidayHomeCatalog = holidayHomeCatalog;
		this.accountRepository = accountRepository;
		this.cart = new Cart();
	}


	/**
	 * Shows firstname of the current{@link AccountEntity}
	 *
	 * @param model must not be {@literal null}
	 */
	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!authentication.getPrincipal().equals("anonymousUser") && !authentication.getName().equals("admin")) {
			System.out.println("authentication: ");
			System.out.println(authentication.getPrincipal());
			model.addAttribute("firstname",
					accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		}
	}

	/**
	 * Creates a new {@link Cart}.
	 *
	 * @return new created Cart
	 */
	@ModelAttribute("cart")
	public Cart initializeCart() {
		return cart;
	}

	/**
	 * Shows all the information of the chosen {@link org.salespointframework.catalog.Product},
	 * which is planed to be ordered. And shows all the {@link Event}s,
	 * which takes place near to the chosen {@link HolidayHome}.
	 *
	 * @param model       muss not be {@literal null}
	 * @param cart        muss not be {@literal null}
	 * @param userAccount muss not be {@literal null}
	 * @return template: cart
	 */
	@GetMapping("/cart")
	@PreAuthorize("hasRole('TENANT')")
	public String basket(Model model, @ModelAttribute Cart cart, @LoggedIn UserAccount userAccount) {
		firstname(model);
		System.out.println("AcceptedEvent are :" + holidayHomeCatalog
				.findById(holidayHome.getId()).get().acceptedEvents);
		for (Event event : holidayHomeCatalog.findById(holidayHome.getId()).get().acceptedEvents) {
			LOG.info(event.getName());
			event.setCapacity(bookingManagement.getStockCountOf(event));
		}
		List<Event> bookable;

		System.out.println(cart);

		//eventCatalog.findByHolidayHome()
		bookable = holidayHomeCatalog
				.findById(holidayHome.getId()).get().acceptedEvents.stream()
				.filter(Event::isEventStatus)
				.filter(event -> event.getDate().isAfter(arrivalDate) && event.getDate().isBefore(departureDate)
						|| event.getDate().isEqual(arrivalDate) || event.getDate().isEqual(departureDate))
				.collect(Collectors.toList());

		boolean eventsInCart = false;
		for (CartItem item : cart) {
			if (item.getProduct().getCategories().iterator().next().equals("Event")) {
				eventsInCart = true;
				break;
			}
		}

		model.addAttribute("eventsInCart", eventsInCart);
		model.addAttribute("eventCatalog", bookable);
		model.addAttribute("holidayHome", holidayHome);
		model.addAttribute("arrivalDate", arrivalDate);
		model.addAttribute("departureDate", departureDate);
		this.userAccount = userAccount;
		model.addAttribute("formatter", formatter);
		model.addAttribute("account", this.userAccount);
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("check", checkIfBooked());
		return "cart";
	}

	/**
	 * Adds a {@link HolidayHome} into the cart,which is planed to be ordered.
	 * If there is already a  {@link HolidayHome} in cart, it will be replaced.
	 *
	 * @param holidayHome template: bookinghist
	 * @param startDate
	 * @param endDate
	 * @param cart
	 * @return redirect to cart
	 */
	@PostMapping("/cart")
	public String addHolidayHome(@RequestParam("hid") HolidayHome holidayHome,
	                             @RequestParam("arrivaldate") String startDate,
	                             @RequestParam("departuredate") String endDate,
	                             @ModelAttribute Cart cart) {
		this.holidayHome = holidayHome;
		this.formatter = new StringFormatter();

		if (!cart.isEmpty()) { //checkt ob schon ein HolidayHome im WarenKorb liegt
			for (CartItem cartItem : cart) {
				if (cartItem.getProduct().getClass() == HolidayHome.class) {
					cart.removeItem(cartItem.getId());
				}
			}
		}

		this.arrivalDate = LocalDate.parse(startDate);
		this.departureDate = LocalDate.parse(endDate);
		Quantity interval = Quantity.of(ChronoUnit.DAYS.between(this.arrivalDate, this.departureDate));
		cart.addOrUpdateItem(holidayHome, interval);
		return "redirect:/cart";
	}

	/**
	 * Updates the modified arrival date and departure date information of the {@link HolidayHome}
	 * The arrival date and departure date will be checked, if it's overlapped between the booked dates.
	 *
	 * @param cart        muss not be {@literal null}
	 * @param newSDate    muss not be {@literal null}
	 * @param newEDate    muss not be {@literal null}
	 * @param holidayHome muss not be {@literal null}
	 * @return redirect to cart
	 */
	@PostMapping("/updateDatum/{id}")
	public String updateDatum(@ModelAttribute Cart cart, @RequestParam("newSDate") String newSDate,
	                          @RequestParam("newEDate") String newEDate,
	                          @PathVariable("id") HolidayHome holidayHome) {

		this.arrivalDate = LocalDate.parse(newSDate);
		this.departureDate = LocalDate.parse(newEDate);
		if (checkIfBooked()) {
			return "redirect:/cart";
		}
		Quantity newInterval = Quantity.of(ChronoUnit.DAYS.between(this.arrivalDate, this.departureDate));
		Quantity oldInterval = Quantity.of(0);
		Iterator<CartItem> it = cart.iterator();
		LinkedList<CartItem> itemsToRemove = new LinkedList<CartItem>();
		while (it.hasNext()) {
			CartItem cartItem = it.next();
			if (cartItem.getProduct().equals(holidayHome)) {
				oldInterval = cartItem.getQuantity();
			} else if (cartItem.getProduct().getClass() == Event.class) {
				Event event = (Event) cartItem.getProduct();
				LocalDate eventDate = event.getDate();
				if (eventDate.isBefore(arrivalDate) || eventDate.isAfter(departureDate)) {
					//fügt es Liste hinzu damit cartitems nicht gelöscht werden solange der Iterator sie noch braucht
					itemsToRemove.add(cartItem);
				}
			}
		}
		for (CartItem cartItem : itemsToRemove) {
			cart.removeItem(cartItem.getId());
		}
		Quantity differenz = newInterval.subtract(oldInterval);
		cart.addOrUpdateItem(holidayHome, differenz);
		return "redirect:/cart";
	}

	/**
	 * Updates the number of the given {@link Event}.
	 *
	 * @param model muss not be {@literal null}
	 * @param cart  muss not be {@literal null}
	 * @param count muss not be {@literal null}
	 * @param event muss not be {@literal null}
	 * @return redirect to cart
	 */
	@PostMapping("/updateTicketCount/{id}")
	public String updateTicketCount(Model model, @ModelAttribute Cart cart, @RequestParam("count") int count,
	                                @PathVariable("id") Event event) {

		System.out.println("Let's update the Ticket Amount");
		System.out.println("Event: " + event.getName());
		String id = "";
		Iterator<CartItem> iter = cart.iterator();
		while (iter.hasNext()) {
			CartItem item = iter.next();
			if (item.getProduct().getId().equals(event.getId())) {
				id = item.getId();
				break;
			}
		}
		CartItem item = cart.getItem(id).get();

		if (count <= 0) {
			cart.removeItem(id);
		} else {
			cart.addOrUpdateItem(item.getProduct(), Quantity.of(count).subtract(item.getQuantity()));
		}
		return "redirect:/cart";
	}

	/**
	 * If the date is not given, sets today as the arrival date and 2 days after as the departure day.
	 *
	 * @param holidayHome muss not be {@literal null}
	 * @param cart        muss not be {@literal null}
	 * @return addHolidayHome function
	 */
	@PostMapping("/defaultcart")
	public String addHolidayHome(@RequestParam("hid") HolidayHome holidayHome, @ModelAttribute Cart cart) {
		LocalDate arrivalDate = LocalDate.now();
		LocalDate departureDate = arrivalDate.plusDays(2);
		return addHolidayHome(holidayHome, arrivalDate.toString(), departureDate.toString(), cart);
	}

	/**
	 * Checks if the date of chosen {@link Event} is between the arrival date and the departure date of the
	 * {@link HolidayHome}.
	 * If not,adds the given {@link Event} in the cart
	 * If there is no {@link HolidayHome} in the cart, the {@link Event} won't be added.
	 *
	 * @param event  muss not be {@literal null}
	 * @param anzahl muss not be {@literal null}
	 * @param cart   muss not be {@literal null}
	 * @return redirect to cart
	 */

	@PostMapping("/eventcart")
	public String addEvent(@RequestParam("eid") Event event,
	                       @RequestParam("number") Quantity anzahl, @ModelAttribute Cart cart) {
		LocalDate bookedDate = event.getDate();
		if (cart.isEmpty()) {
			return "redirect:/holdayhomes";
		}
		HolidayHome home;
		for (CartItem cartItem : cart) {
			if (cartItem.getProduct().getClass() == HolidayHome.class) {
				home = (HolidayHome) cartItem.getProduct();
			}
		}

		if (bookedDate.isBefore(LocalDate.now())
				|| bookedDate.isBefore(arrivalDate)
				|| bookedDate.isAfter(departureDate)) {
			//send to customer "Please choose the right day"
			return "error";
		}
		System.out.println("anzahl0: " + anzahl.getAmount());
		// check if still available
		Quantity completeRequirements = Quantity.of(0);
		Iterator<CartItem> iter = cart.stream().iterator();
		while (iter.hasNext()) {
			System.out.println("1");
			CartItem item = iter.next();
			if (item.getProduct().getId().equals(event.getId())) {
				completeRequirements = anzahl.add(item.getQuantity());
				break;
			}
		}
		System.out.println("anzahl: " + anzahl.getAmount());
		System.out.println("complete Requirements:" + completeRequirements.getAmount());
		if (anzahl.isGreaterThan(Quantity.of(event.getCapacity())) || anzahl.isLessThan(Quantity.of(0))
				|| completeRequirements.isGreaterThan(Quantity.of(event.getCapacity()))) {
			System.out.println("please dont ask for that amount this is f*cking impossible");
			//"Please give in a correct number"
			return "error";
		} else {
			// check if it's already full or anzahl > updated capacity
			cart.addOrUpdateItem(event, anzahl);
			return "redirect:/cart";
		}
	}

	/**
	 * Removes item in the cart,if it's {@link HolidayHome}, clear up all items in cart.
	 * If it is {@link Event},removes the given item.
	 *
	 * @param model muss not be {@literal null}
	 * @param id    muss not be {@literal null}
	 * @param cart  muss not be {@literal null}
	 * @return redirect to holidayhome if {@link HolidayHome} removed; redirect to event,if {@link Event} removed
	 */
	@GetMapping("/removeProduct/{id}")
	public String removeItem(Model model, @PathVariable("id") String id, @ModelAttribute Cart cart) {
		firstname(model);
		if (cart.getItem(id).isPresent()) {
			if (cart.getItem(id).get().getProduct().getCategories().iterator().next().equals("HolidayHome")) {
				cart.clear();
				return "redirect:/holidayhomes";
			}
			cart.removeItem(id);
			return "redirect:/cart";
		}
		return "redirect:/cart";
	}

	/**
	 * Removes all of the items in cart
	 *
	 * @param model muss not be {@literal null}
	 * @param cart  muss not be {@literal null}
	 * @return redirect to cart
	 */
	@GetMapping("/clear")
	public String clear(Model model, @ModelAttribute Cart cart) {
		firstname(model);
		cart.clear();
		return "redirect:/cart";
	}

	/**
	 * Checkouts the cart and creates a {@link BookingEntity} with all the needed parameters from the cart
	 *
	 * @param model       muss not be {@literal null}
	 * @param cart        muss not be {@literal null}
	 * @param holidayHome muss not be {@literal null}
	 * @param events      muss not be {@literal null}
	 * @param userAccount muss not be {@literal null}
	 * @param paymethod   muss not be {@literal null}
	 * @return template: bookingdetails
	 */
	@PostMapping("/purchase")
	public String buy(Model model, @ModelAttribute Cart cart, @RequestParam("hid") HolidayHome holidayHome,
	                  @ModelAttribute HashMap<Event, Integer> events, @LoggedIn UserAccount userAccount,
	                  @RequestParam("paymethod") String paymethod) {
		if (arrivalDate.isBefore(LocalDate.now())
				|| departureDate.isBefore(LocalDate.now())
				|| departureDate.isBefore(arrivalDate)) {
			//send message "Please choose the correct day"
			return "redirect:/cart";
		}

		if (checkIfBooked()) {
			return "redirect:/cart";
		}
		BookingEntity bookingEntity = bookingManagement.createBookingEntity(userAccount, holidayHome,
				cart, arrivalDate, departureDate, events, paymethod);
		if (bookingEntity == null) {
			return "redirect:/cart"; //es gab Probleme
		}
		// if the Tenant wants to pay independent from us
		if (paymethod.equalsIgnoreCase("cash")) {
			bookingManagement.payDeposit(bookingEntity);
		} // !! other option:  bookingManagement.pay(bookingManagement.findFirstByOrderIdentifier(booking.getId()))

		details(model, bookingEntity);
		return "bookingdetails"; //!!
	}

	/**
	 * Checks if the given arrival date and departure date are booked
	 *
	 * @return if booked return true, otherwise false
	 */
	public boolean checkIfBooked() {
		List<BookingEntity> bookedList = new ArrayList<>(bookingManagement
				.findBookingsByUuidHome(holidayHome.getId()).toList());
		for (BookingEntity b : bookedList) {
			if (!b.getState().equals(BookingStateEnum.CANCELED)
					&& arrivalDate.isBefore(b.getDepartureDate())
					&& departureDate.isAfter(b.getArrivalDate())) {
				//send message "the chosed duration is not avalible"
				System.out.println("redirect to Cart because its already booked");
				//!! Message to customer is missing
				return true;
			}
		}
		return false;
	}

	/**
	 * Shows all the information of the {@link BookingEntity}
	 *
	 * @param model   muss not be {@literal null}
	 * @param booking muss not be {@literal null}
	 * @return template: bookingdetails
	 */
	@GetMapping("/bookingdetails/{booking}")
	public String details(Model model, @PathVariable BookingEntity booking) {
		firstname(model);
		model.addAttribute("booking", bookingManagement.findFirstByOrderIdentifier(booking.getId()));
		model.addAttribute("formatter", new StringFormatter());
		model.addAttribute("accountManager", accountManagement);
		model.addAttribute("one", one);
		model.addAttribute("customer", booking.getUserAccount());
		for (OrderLine line : booking.getOrderLines()) {
			if (holidayHomeCatalog.findFirstByProductIdentifier(line.getProductIdentifier()) != null) {
				HolidayHome home = holidayHomeCatalog.findFirstByProductIdentifier(line.getProductIdentifier());
				model.addAttribute("holidayHome", home);
			}
		}
		OrderLine home = booking
				.getOrderLines()
				.filter(orderLine -> holidayHomeCatalog
						.findFirstByProductIdentifier(orderLine.getProductIdentifier()) != null)
				.toList()
				.listIterator()
				.next();
		model.addAttribute("holidayHomeOrderLine", home);
		model.addAttribute("eventCatalog", eventcatalog);
		List<OrderLine> events = booking
				.getOrderLines()
				.filter(orderLine -> eventcatalog
						.findFirstByProductIdentifier(orderLine.getProductIdentifier()) != null)
				.toList();
		//MonetaryAmount deposit=booking.getTotal().add(Money.of(0,"EUR").add(home.getPrice().multiply(0.9)).negate());
		model.addAttribute("orderlines", events);
		MonetaryAmount deposit = Money.of(booking.getDepositInCent() * 0.01f, "EUR");
		model.addAttribute("deposit", deposit);
		MonetaryAmount rest = Money.of(booking.getRestInCent() * 0.01f, "EUR");
		model.addAttribute("rest", rest);
		return "bookingdetails";
	}

	/**
	 * Cancels the given {@link BookingEntity}
	 *
	 * @param model   muss not be {@literal null}
	 * @param booking muss not be {@literal null}
	 * @return redirect to holidayahomes
	 */
	@GetMapping("/cancel/{id}")
	public String cancel(Model model, @PathVariable("id") BookingEntity booking) {
		firstname(model);
		System.out.println(booking.getId());
		if (bookingManagement.findFirstByOrderIdentifier(booking.getId()).cancel()) {
			//Work with Copy???
			System.out.println(bookingManagement.findFirstByOrderIdentifier(booking.getId()).getState());
			String user = authentication.getAuthorities().iterator().next().getAuthority();
			if(user.equals("ROLE_HOST")){
				return "redirect:/bookinghistory";
			}
			return "redirect:/holidayhomes";
		} else {
			return "";
		}
	}

	/**
	 * Pays the deposit of the {@link BookingEntity}.
	 *
	 * @param model   muss not be {@literal null}
	 * @param booking muss not be {@literal null}
	 * @return if it can be paid redirect to bookings, else redirect to bookingdetails
	 */
	@GetMapping("/pay/{id}")
	public String pay(Model model, @PathVariable("id") BookingEntity booking) {
		firstname(model);
		if (bookingManagement.payDeposit(bookingManagement.findFirstByOrderIdentifier(booking.getId()))) {
			//it's paid
			return "redirect:/bookings";
		}
		//it couldn't be paid maybe it already was or something like that
		return "redirect:/bookingdetails/" + booking.getId();
	}

	/**
	 * Pays the rest of the{@link BookingEntity}.
	 *
	 * @param model   muss not be {@literal null}
	 * @param booking muss not be {@literal null}
	 * @return if it can be paid redirect to bookings, else redirect to bookingdetails
	 */
	@GetMapping("/payRest/{id}")
	public String payRest(Model model, @PathVariable("id") BookingEntity booking) {
		firstname(model);
		if (bookingManagement.payRest(bookingManagement.findFirstByOrderIdentifier(booking.getId()))) {
			//it's paid
			return "redirect:/bookings";
		}
		//it couldn't be paid maybe it already was or something like that
		return "redirect:/bookingdetails/" + booking.getId();
	}

	/**
	 * Host confirms the{@link BookingEntity}, which is already paid by tenant.ü
	 *
	 * @param model
	 * @param booking
	 * @return redirect to holidayhomes
	 */
	@GetMapping("/confirm/{id}")
	public String confirm(Model model, @PathVariable("id") BookingEntity booking) {
		firstname(model);
		if (bookingManagement.findFirstByOrderIdentifier(booking.getId()).confirm()) {
			//it's now confirmed
			System.out.println(bookingManagement.findFirstByOrderIdentifier(booking.getId()).getState());
			return "redirect:/bookinghistory";
		}
		//it couldn't be confirmed maybe it already was or something like that
		return "redirect:/bookinghistory";
	}

}

