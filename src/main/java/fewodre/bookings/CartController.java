package fewodre.bookings;

import fewodre.catalog.events.Event;
import fewodre.catalog.events.EventCatalog;
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.AccountController;
import fewodre.useraccounts.AccountManagement;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class CartController {

	private final AccountManagement accountManagement;
	private final BookingManagement bookingManagement;
	private final EventCatalog eventcatalog;
	private final HolidayHomeCatalog holidayHomeCatalog;

	private HolidayHome holidayHome;
	private UserAccount userAccount;

	private StringFormatter formatter;

	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

	@DateTimeFormat(pattern = "dd.mm.yyyy")
	private LocalDate arrivalDate, departureDate;


	CartController(AccountManagement accountManagement, BookingManagement bookingManagement,
				   EventCatalog eventCatalog, HolidayHomeCatalog holidayHomeCatalog) {

		Assert.notNull(accountManagement, "AccountManagement must not be null!");
		Assert.notNull(bookingManagement, "BookingManagement must not be null!");
		Assert.notNull(eventCatalog, "EventCatalog must not be null!");
		Assert.notNull(holidayHomeCatalog, "HolidayHomeCatalog must not be null!");

		this.accountManagement = accountManagement;
		this.bookingManagement = bookingManagement;
		this.eventcatalog = eventCatalog;
		this.holidayHomeCatalog = holidayHomeCatalog;
	}

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@GetMapping("/cart")
	@PreAuthorize("hasRole('TENANT')")
	public String basket(Model model, @ModelAttribute Cart cart, @LoggedIn UserAccount userAccount){
		Iterator<Event> iter = eventcatalog.findAll().iterator();
		while (iter.hasNext()){
			Event event = iter.next();
			LOG.info(event.getName());
			event.setCapacity(bookingManagement.getStockCountOf(event));
		}
		List<Event> bookable = new ArrayList<Event>();

		//eventCatalog.findByHolidayHome()
		bookable = eventcatalog.findAll().
				filter(event -> event.getDate().isAfter(arrivalDate) && event.getDate().isBefore(departureDate)
						||event.getDate().isEqual(arrivalDate)||event.getDate().isEqual(departureDate)).toList();
		model.addAttribute("eventCatalog", bookable);
		model.addAttribute("holidayHome", holidayHome);
		model.addAttribute("arrivalDate", arrivalDate);
		model.addAttribute("departureDate", departureDate);
		this.userAccount = userAccount;
		model.addAttribute("formatter", formatter);
		model.addAttribute("account", this.userAccount);
		model.addAttribute("today", LocalDate.now());
		return "cart"; }

	@PostMapping("/cart")
	public String addHolidayHome(@RequestParam("hid") HolidayHome holidayHome, @RequestParam("arrivaldate")LocalDate startDate,
								 @RequestParam("departuredate")LocalDate endDate, @ModelAttribute Cart cart){
		this.holidayHome = holidayHome;
		this.formatter = new StringFormatter();

		if(!cart.isEmpty()){ //checkt ob schon ein HolidayHome im WarenKorb liegt
			Iterator<CartItem> iter = cart.iterator();
			while(iter.hasNext()) {
				CartItem cartItem = iter.next();
				if (cartItem.getProduct().getClass() == HolidayHome.class) {
					cart.removeItem(cartItem.getId());
				}
			}
		}

			this.arrivalDate = startDate;
			this.departureDate = endDate;
			Quantity interval = Quantity.of(ChronoUnit.DAYS.between(this.arrivalDate, this.departureDate));
			cart.addOrUpdateItem(holidayHome, interval);

			return "redirect:/cart";
	}


	@PostMapping("/updateDatum/{id}")
	public String updateDatum(@ModelAttribute Cart cart,@RequestParam("newSDate")String newSDate,
								@RequestParam("newEDate")String newEDate, @PathVariable("id") HolidayHome holidayHome){

		this.arrivalDate = LocalDate.parse(newSDate);;
		this.departureDate = LocalDate.parse(newEDate);;
		Quantity newInterval = Quantity.of(ChronoUnit.DAYS.between(this.arrivalDate, this.departureDate));
		Quantity oldInterval = Quantity.of(0);
		Iterator<CartItem> it = cart.iterator();
		LinkedList<CartItem> itemsToRemove = new LinkedList<CartItem>();
		while(it.hasNext()){
			CartItem cartItem = it.next();
			if(cartItem.getProduct().equals(holidayHome)){
				oldInterval =cartItem.getQuantity();
			}else if(cartItem.getProduct().getClass() == Event.class){
				Event event = (Event)cartItem.getProduct();
				LocalDate eventDate = event.getDate();
				if(eventDate.isBefore(arrivalDate)||eventDate.isAfter(departureDate)){
					//fügt es Liste hinzu damit cartitems nicht gelöscht werden solange der Iterator sie noch braucht
					itemsToRemove.add(cartItem);
				}
			}
		}
		for (int i = 0; i < itemsToRemove.size(); i++){
			cart.removeItem(itemsToRemove.get(i).getId());
		}
		Quantity differenz = newInterval.subtract(oldInterval);
		cart.addOrUpdateItem(holidayHome, differenz);
		return "redirect:/cart";
	}

	@PostMapping("/updateTicketCount/{id}")
	public String updateTicketCount(Model model, @ModelAttribute Cart cart, @RequestParam("count") int count,
									@PathVariable("id") Event event){
		System.out.println("Let's update the Ticket Amount");
		System.out.println("Event: "+event.getName());
		String id = "";
		Iterator<CartItem> iter = cart.iterator();
		while (iter.hasNext()){
			CartItem item = iter.next();
			if(item.getProduct().getId().equals(event.getId())){
				id = item.getId();
				break;
			}
		}
		CartItem item = cart.getItem(id).get();

		if(count <= 0){
			cart.removeItem(id);
		}
		else{
			cart.addOrUpdateItem(item.getProduct(), Quantity.of(count).subtract(item.getQuantity()));
		}
		return "redirect:/cart";
	}


	@PostMapping("/defaultcart")
	public String addHolidayHome(@RequestParam("hid") HolidayHome holidayHome,@ModelAttribute Cart cart){
		LocalDate arrivalDate = LocalDate.now();
		LocalDate departureDate = arrivalDate.plusDays(2);
		return addHolidayHome(holidayHome, arrivalDate, departureDate,cart);
	}



	@PostMapping("/eventcart")
	public String addEvent(@RequestParam("eid") Event event,
						   @RequestParam("number") Quantity anzahl, @ModelAttribute Cart cart){
		LocalDate bookedDate = event.getDate();
		if(cart.isEmpty()){
			return"redirect:/holdayhomes";
		}
		HolidayHome home;
		Iterator it = cart.iterator();
		while(it.hasNext()) {
			CartItem cartItem = (CartItem) it.next();
			if (cartItem.getProduct().getClass() == HolidayHome.class) {
				home = (HolidayHome)cartItem.getProduct();
			}
		}

		if(bookedDate.isBefore(LocalDate.now())|| bookedDate.isBefore(arrivalDate)|| bookedDate.isAfter(departureDate)){
			//send to customer "Please choose the right day"
			return "error";
		}
		System.out.println("anzahl0: " + anzahl.getAmount());
		// check if still available
		Quantity completeRequirements = Quantity.of(0);
		Iterator<CartItem> iter = cart.stream().iterator();
		while(iter.hasNext()){
			System.out.println("1");
			CartItem item = iter.next();
			if(item.getProduct().getId().equals(event.getId())){
				 completeRequirements = anzahl.add(item.getQuantity());
				break;
			}
		}
		System.out.println("anzahl: " + anzahl.getAmount());
		System.out.println("complete Requirements:" + completeRequirements.getAmount());
		if(anzahl.isGreaterThan(Quantity.of(event.getCapacity())) || anzahl.isLessThan(Quantity.of(0))
				|| completeRequirements.isGreaterThan(Quantity.of(event.getCapacity()))){
			System.out.println("please dont ask for that amount this is f*cking impossible");
			//"Please give in a correct number"
			return"error";
		}
		// check if it's already full or anzahl > updated capacity
		else{
			cart.addOrUpdateItem(event, anzahl);
			return "redirect:/cart";
		}
	}


	@GetMapping("/removeProduct/{id}")
	public String removeItem(Model model, @PathVariable("id") String id, @ModelAttribute Cart cart) {
		if(cart.getItem(id).get().getProduct().getCategories().iterator().next().equals("HolidayHome")){
			cart.clear();
			return "redirect:/holidayhomes";
		}
		cart.removeItem(id);
		return "redirect:/cart";
	}

	@GetMapping("/clear")
	public String clear(Model model, @ModelAttribute Cart cart) {
		cart.clear();
		return "redirect:/cart";
	}


	@PostMapping("/purchase")
	public String buy(Model model, @ModelAttribute Cart cart, @RequestParam("hid") HolidayHome holidayHome,
					  @ModelAttribute HashMap<Event, Integer> events, @LoggedIn UserAccount userAccount){
		System.out.println(cart.getPrice());
		System.out.println("Buchungszeitraum0: ");
		System.out.println(arrivalDate.toString() + " - " +departureDate.toString());
		if(arrivalDate.isBefore(LocalDate.now()) || departureDate.isBefore(LocalDate.now()) || departureDate.isBefore(arrivalDate)) {
			//send message "Please choose the correct day"
			return "redirect:/cart";
		}

		//check if it's available
		ArrayList<BookingEntity> bookedList = new ArrayList<BookingEntity>(bookingManagement.findBookingsByUuidHome(holidayHome.getId()).toList());
		for (BookingEntity b : bookedList) {
			if(arrivalDate.isBefore(b.getDepartureDate()) && departureDate.isAfter(b.getArrivalDate())){
				//send message "the chosed duration is not avalible"
				System.out.println("redirect to Cart because its already booked");
				//!! Message to customer is missing
				return "redirect:/cart";
			}
		}
		BookingEntity bookingEntity = bookingManagement.createBookingEntity(userAccount, holidayHome, cart, arrivalDate, departureDate, events);
		if ( bookingEntity == null){
			return "redirect:/cart"; //es gab Probleme
		}
		details(model ,bookingEntity);
		return "/bookingdetails"; //!!
	}

	@GetMapping("/bookingdetails")
	public String details(Model model, BookingEntity bookingEntity){
		model.addAttribute("booking", bookingEntity);
		model.addAttribute("formatter", new StringFormatter());
		model.addAttribute("accountManager", accountManagement);
		Iterator<OrderLine> iter = bookingEntity.getOrderLines().iterator();
		while(iter.hasNext()){
			OrderLine line = iter.next();
			if(holidayHomeCatalog.findFirstByProductIdentifier(line.getProductIdentifier()) != null){
				HolidayHome home = holidayHomeCatalog.findFirstByProductIdentifier(line.getProductIdentifier());
				model.addAttribute("holidayHome", home);
			}
		}
		return "bookingdetails";
	}

	@GetMapping("/cancel/{id}")
	public String cancel(Model model, @PathVariable("id") BookingEntity booking){
		System.out.println(booking.getId());
		if(bookingManagement.findFirstByOrderIdentifier(booking.getId()).cancel()){
			//Work with Copy???
			System.out.println(bookingManagement.findFirstByOrderIdentifier(booking.getId()).getState());
			return "redirect:/holidayhomes";
		}
		else {return "";}
	}

	@GetMapping("/pay/{id}")
	public String pay(Model model, @PathVariable("id") BookingEntity booking){
		if(bookingManagement.pay(bookingManagement.findFirstByOrderIdentifier(booking.getId()))){
			//it's paid
			return "redirect:/holidayhomes";
		}
		//it could't be paid maybe it already was or something like that
		return "redirect:/holidayhomes";
	}

}

