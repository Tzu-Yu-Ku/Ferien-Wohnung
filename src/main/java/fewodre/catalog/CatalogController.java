package fewodre.catalog;

import fewodre.bookings.BookingManagement;
import fewodre.bookings.StringFormatter;
import fewodre.catalog.events.*;
import fewodre.catalog.events.Event.EventType;
import fewodre.catalog.holidayhomes.*;
import fewodre.catalog.storage.StorageService;
import fewodre.useraccounts.*;

import fewodre.useraccounts.AccountManagement;
import fewodre.useraccounts.AccountRepository;
import fewodre.utils.Place;

import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class CatalogController {

	private static final Quantity NONE = Quantity.of(0);

	private final HolidayHomeCatalog Hcatalog;
	private final EventCatalog Ecatalog;
	private final BusinessTime businessTime;
	private UniqueInventory<UniqueInventoryItem> holidayHomeStorage;
	private final AccountManagement accountManagement;
	private final AccountRepository accountRepository;
	private Authentication authentication;

	@Autowired
	private final StorageService storageService;

	ArrayList<ProductIdentifier> holidayHomeIdList = new ArrayList<ProductIdentifier>();
	ArrayList<HolidayHome> sortCapacityList = new ArrayList<HolidayHome>();
	ArrayList<HolidayHome> sortPriceList = new ArrayList<HolidayHome>();
	ArrayList<HolidayHome> sortDistrictList = new ArrayList<HolidayHome>();
	ArrayList<Event> sortEventTypeList = new ArrayList<Event>();
	ArrayList<Event> sortEventCapacityList = new ArrayList<Event>();
	ArrayList<Event> sortEventDistrictList = new ArrayList<Event>();
	ArrayList<Event> recentEvent = new ArrayList<Event>();

	public static String uploadDirectory = Paths.get("").toAbsolutePath().toString() + "/src/main/resources/static/resources/img/";

	private BookingManagement bookingManagement;

	/**
	 * Creates a new {@link CatalogController} with the given Parameters.
	 *
	 * @param Hcatalog           muss not be {@literal null}
	 * @param Ecatalog           muss not be {@literal null}
	 * @param businessTime       muss not be {@literal null}
	 * @param holidayHomeStorage muss not be {@literal null}
	 * @param accountManagement  muss not be {@literal null}
	 * @param accountRepository  muss not be {@literal null}
	 * @param bookingManagement  muss not be {@literal null}
	 * @param storageService     muss not be {@literal null}
	 */
	CatalogController(HolidayHomeCatalog Hcatalog, EventCatalog Ecatalog, BusinessTime businessTime,
	                  UniqueInventory<UniqueInventoryItem> holidayHomeStorage, AccountManagement accountManagement,
	                  AccountRepository accountRepository, BookingManagement bookingManagement,
	                  StorageService storageService) {
		this.Hcatalog = Hcatalog;
		this.Ecatalog = Ecatalog;
		this.businessTime = businessTime;
		this.holidayHomeStorage = holidayHomeStorage;
		this.accountManagement = accountManagement;
		this.accountRepository = accountRepository;
		this.bookingManagement = bookingManagement;
		this.storageService = storageService;
	}

	/**
	 * Shows firstname of the current{@link AccountEntity}
	 *
	 * @param model must not be {@literal null}
	 */
	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")
				&& !authentication.getName().equals("admin")) {
			System.out.println("authentication: ");
			System.out.println(authentication.getPrincipal());
			model.addAttribute("firstname",
					accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		}
	}

	/**
	 * Method will show all searched {@link HolidayHome}s. Host will only see his {@link HolidayHome}s.
	 *
	 * @param model muss not be {@literal null}
	 * @return template: holidayhomes
	 */
	@GetMapping("/holidayhomes")
	String holidayHomeCatalog(Model model) {
		firstname(model);

		Hcatalog.findAll().forEach(item -> sortCapacityList.add(item));
		Hcatalog.findAll().forEach(item -> sortPriceList.add(item));
		Hcatalog.findAll().forEach(item -> sortDistrictList.add(item));

		if (authentication.getPrincipal().toString().contains("HOST")) {
			model.addAttribute("holidayhomeCatalog", Hcatalog.findAll()
					.filter(holidayHome -> holidayHome.getHostMail().equals(
							accountRepository.findByAccount_Email(authentication.getName()).getAccount().getEmail()))
					.filter(holidayHome -> holidayHome.getIsBookable()));
		} else {
			model.addAttribute("holidayhomeCatalog",
					Hcatalog.findAll().filter(holidayHome -> holidayHome.getIsBookable()));
		}
		return "holidayhomes";
	}

	/**
	 * Method enable the sort of the {@link HolidayHome}s.
	 *
	 * @param model muss not be {@literal null}
	 * @return template : holidayhomes
	 */
	@PreAuthorize("!hasRole('HOST')")
	@PostMapping("/holidayhomes")
	String sortHolidayHome(Model model, String searchedCapacity, String searchedPrice1, String searchedPrice2,
	                       String searchedDistrict) {
		firstname(model);

		if (searchedCapacity == null && searchedPrice1 == null && searchedPrice2 == null && searchedDistrict == null) {
			/*Hcatalog.findAll().forEach(item -> sortCapacityList.add(item));
			Hcatalog.findAll().forEach(item -> sortPriceList.add(item));
			Hcatalog.findAll().forEach(item -> sortDistrictList.add(item));
			*/
			holidayHomeCatalog(model);
		}

		if (searchedCapacity != null) {
			sortCapacityList.clear();

			if (searchedCapacity.equals("Alle")) {
				Hcatalog.findAll().forEach(item -> sortCapacityList.add(item));
			} else if (!searchedCapacity.equals("Alle")) {
				int searchedCapacityInt = Integer.parseInt(searchedCapacity);
				Hcatalog.findAll().filter(holidayHome -> holidayHome.getCapacity() >= searchedCapacityInt)
						.forEach(item -> sortCapacityList.add(item));
			}
		}

		if (searchedPrice1 != null && searchedPrice2 != null) {
			sortPriceList.clear();

			if (searchedPrice1.equals("Alle") && searchedPrice2.equals("Alle")) {
				Hcatalog.findAll().forEach(item -> sortPriceList.add(item));
			} else if (searchedPrice1.equals("0")) {
				Hcatalog.findAll().filter(
						holidayHome -> holidayHome.getPrice().isLessThanOrEqualTo(Money.parse("EUR " + searchedPrice2)))
						.forEach(item -> sortPriceList.add(item));
			} else if (searchedPrice2.equals("0")) {
				Hcatalog.findAll().filter(
						holidayHome -> holidayHome.getPrice().isGreaterThan(Money.parse("EUR " + searchedPrice1)))
						.forEach(item -> sortPriceList.add(item));
			} else {
				Hcatalog.findAll()
						.filter(holidayHome -> holidayHome.getPrice()
								.isGreaterThanOrEqualTo(Money.parse("EUR " + searchedPrice1)))
						.filter(holidayHome -> holidayHome.getPrice()
								.isLessThanOrEqualTo(Money.parse("EUR " + searchedPrice2)))
						.forEach(item -> sortPriceList.add(item));
			}
		}

		if (searchedDistrict != null) {
			sortDistrictList.clear();

			if (searchedDistrict.equals("Alle")) {
				Hcatalog.findAll().forEach(item -> sortDistrictList.add(item));
			} else if (!searchedDistrict.equals("Alle")) {
				Hcatalog.findAll().filter(holidayHome -> holidayHome.getPlace().getDistrict().equals(searchedDistrict))
						.forEach(item -> sortDistrictList.add(item));
			}
		}

		model.addAttribute("holidayhomeCatalog",
				Hcatalog.findAll().filter(holidayHome -> holidayHome.getIsBookable())
						.filter(holidayHome -> holidayHome.findInList(holidayHome, sortCapacityList))
						.filter(holidayHome -> holidayHome.findInList(holidayHome, sortPriceList))
						.filter(holidayHome -> holidayHome.findInList(holidayHome, sortDistrictList)));

		return "holidayhomes";
	}

	/**
	 * Method will pass to the form, where we can create a new HolidayHome
	 *
	 * @param model muss not be {@literal null}
	 * @return template : addholidayhome
	 */
	@GetMapping("/addholidayhome")
	@PreAuthorize("hasRole('HOST')")
	String addHolidayhomePage(Model model, HolidayHomeForm form) {
		firstname(model);
		model.addAttribute("form", form);
		return "addholidayhome";
	}

	/**
	 * Method will create and add a HolidayHome to the Hcatalog
	 *
	 * @param model muss not be {@literal null}
	 * @param form  muss not be {@literal null}
	 * @return template : editHolidayHomeLocation and a String that represent the Productidentifier
	 */
	@PreAuthorize("hasRole('HOST')")
	@PostMapping(path = "/addHolidayHome")
	String addHolidayHomes(@Valid @ModelAttribute("form") HolidayHomeForm form,
	                       BindingResult result,
	                       @RequestParam("imageupload") MultipartFile image,
	                       @LoggedIn UserAccount userAccount,
	                       Model model) {

		firstname(model);
		String[] strings = image.getContentType().split("/");
		String contentType = "." + strings[strings.length - 1];
		if (!(contentType.equals(".jpeg") || contentType.equals(".png") || contentType.equals(".jpg"))) {
			result.addError(new FieldError("form",
					"imageupload",
					"Es werden nur JPG oder PNG Bilder unterstützt."));
		}

		if (result.hasErrors()) {
			return "addholidayhome";
		}

		HolidayHome myHolidayHome = form.toNewHolidayHome(userAccount.getEmail());

		ProductIdentifier productIdentifier = myHolidayHome.getId();
		String fileName = UUID.randomUUID().toString() + contentType;
		storageService.store(image, fileName);

		myHolidayHome.setImage(fileName);

		Hcatalog.save(myHolidayHome);
		for (int i = 0; i < Ecatalog.findAll().toList().size(); i++) {
			System.out.println(
					Ecatalog.findAll().toList().get(i).getPlace().distanceToOtherPlaces(myHolidayHome.getPlace()));
		}

		return "redirect:/editHolidayHomeLocation?holidayhome=" + productIdentifier.toString();
	}

	/**
	 * Method will pass to the form, where we can edit a {@link HolidayHome}
	 *
	 * @param model       muss not be {@literal null}
	 * @param holidayHome muss not be {@literal null}
	 * @return template : editholidayhome
	 */
	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/editholidayhome")
	String editHolidayhomePage(@RequestParam("holidayHome") HolidayHome holidayHome, Model model) {
		firstname(model);
		model.addAttribute("holidayHome", holidayHome);
		model.addAttribute("errors", new HashMap<String, String>());
		return "editholidayhome";
	}

	/**
	 * Method will edit a {@link HolidayHome}
	 *
	 * @param model         muss not be {@literal null}
	 * @param holidayHomeId muss not be {@literal null}
	 * @param name          muss not be {@literal null}
	 * @param description   muss not be {@literal null}
	 * @param price         muss not be {@literal null}
	 * @param capacity      muss not be {@literal null}
	 * @param street        muss not be {@literal null}
	 * @param houseNumber   muss not be {@literal null}
	 * @param city          muss not be {@literal null}
	 * @param postalCode    muss not be {@literal null}
	 *                      //	 * @param coordinates_x muss not be {@literal null}
	 *                      //	 * @param coordinates_y muss not be {@literal null}
	 * @return template : holidayhome
	 */
	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/editHolidayHome")
	String editHolidayHomes(Model model, @RequestParam("holidayHomeId") ProductIdentifier holidayHomeId,
	                        @RequestParam("name") String name, @RequestParam("description") String description,
	                        @RequestParam("price") String price, @RequestParam("capacity") String capacity,
	                        @RequestParam("street") String street, @RequestParam("houseNumber") String houseNumber,
	                        @RequestParam("city") String city, @RequestParam("postalCode") String postalCode,
                            @RequestParam("imageupload") MultipartFile image) {

		firstname(model);
		HashMap<String, String> errorMap = new HashMap<>();
		String fileName = "unchanged";
		if (!image.isEmpty()) {
			System.out.println(image.toString());
			String[] strings = image.getContentType().split("/");
			String contentType = "." + strings[strings.length - 1];
			if (!(contentType.equals(".jpeg") || contentType.equals(".png") || contentType.equals(".jpg"))) {
				errorMap.put("imageupload", "Es werden nur JPG oder PNG Bilder unterstützt.");
//				model.addAttribute("errors", errorMap);
//				Optional<HolidayHome> holidayHome = Hcatalog.findById(holidayHomeId);
//				model.addAttribute("holidayHome", holidayHome.get());
//				return "editholidayhome";
			}
			else {
				fileName = UUID.randomUUID().toString() + contentType;
				storageService.store(image, fileName);
			}
		}

		boolean holidayHomePlaceUpdated = false;
		if (Hcatalog.findById(holidayHomeId).isPresent()) {
			HolidayHome holidayHomeToChange = Hcatalog.findById(holidayHomeId).get();
			if(!fileName.equals("unchanged")) {
				holidayHomeToChange.setImage(fileName);
			}
			if (!name.isBlank()) {
				holidayHomeToChange.setName(name);
			}

			if (!description.isBlank()) {
				holidayHomeToChange.setDescription(description);
			}
			if (!price.isBlank()) {
				float parsedPrice = Float.parseFloat(price);
				if(parsedPrice < 1.0f) {
					errorMap.put("price", "Bitte geben Sie einen Preis von mind. 1.00 EUR ein.");
				} else {
					holidayHomeToChange.setPrice(Money.of(parsedPrice, "EUR"));
				}
			}
			if (!capacity.isBlank()) {
				int capacityHH = Integer.parseInt(capacity);
				if(capacityHH < 1) {
					errorMap.put("capacity", "Bitte geben Sie eine max. Personenzahl von mind. 1 an.");
				} else {
					holidayHomeToChange.setCapacity(capacityHH);
				}
			}
			Place changedPlace = holidayHomeToChange.getPlace();
			if (!street.isBlank()) {
				changedPlace.setStreet(street);
				holidayHomePlaceUpdated = true;
			}
			if (!houseNumber.isBlank()) {
				if(houseNumber.matches("\\d+[a-zA-Z]*")) {
					changedPlace.setHouseNumber(houseNumber);
					holidayHomePlaceUpdated = true;
				} else {
					errorMap.put("houseNumber", "Bitte geben Sie eine gültige Hausnummer an.");
				}
			}
			if (!postalCode.isBlank()) {
				if(postalCode.matches("[0-9]{5}")) {
					changedPlace.setPostalCode(postalCode);
					holidayHomePlaceUpdated = true;
				} else {
					errorMap.put("postalCode", "Bitte geben sie eine gültige PLZ an.");
				}
			}
			if (!city.isBlank()) {
				changedPlace.setCity(city);
				holidayHomePlaceUpdated = true;
			}

			if(!errorMap.isEmpty()) {
				model.addAttribute("errors", errorMap);
				Optional<HolidayHome> holidayHome = Hcatalog.findById(holidayHomeId);
				model.addAttribute("holidayHome", holidayHome.get());
				return "editholidayhome";
			}

			holidayHomeToChange.setPlace(changedPlace);
			System.out.println(holidayHomeToChange);
			Hcatalog.save(holidayHomeToChange);

		}

		if (holidayHomePlaceUpdated) {
			return "redirect:/editHolidayHomeLocation?holidayhome=" + holidayHomeId.toString();
		}

		errorMap.put("success", "Ihre Änderungen wurden erfolgreich gespeichert.");
		model.addAttribute("errors", errorMap);
		Optional<HolidayHome> holidayHome = Hcatalog.findById(holidayHomeId);
		model.addAttribute("holidayHome", holidayHome.get());
		return "editholidayhome";
	}

	/**
	 * Method set the value isBookable of a {@link HolidayHome} to false.
	 *
	 * @param model       muss not be {@literal null}
	 * @param holidayHome muss not be {@literal null}
	 * @return template : holidayhome
	 */
	@PreAuthorize("hasRole('HOST')")
	@PostMapping("/deleteholidayhome")
	String deleteHolidayHome(@RequestParam("holidayHome") HolidayHome holidayHome, Model model) {

		ProductIdentifier holidayHomeId = holidayHome.getId();
		System.out.println(holidayHomeId);
		Hcatalog.findById(holidayHomeId);

		if (Hcatalog.findById(holidayHomeId).isPresent()) {
			HolidayHome holidayHomeToChange = Hcatalog.findById(holidayHomeId).get();
			holidayHomeToChange.setIsBookable(false);
			Hcatalog.save(holidayHomeToChange);
		}

		return "redirect:/holidayhomes";
	}

	/**
	 * Method give all informations of a {@link HolidayHome}.
	 *
	 * @param model       muss not be {@literal null}
	 * @param holidayHome muss not be {@literal null}
	 * @return template : housedetails
	 */
	@PostMapping("/housedetails")
	String detail(@RequestParam("holidayHome") HolidayHome holidayHome, Model model) {
		model.addAttribute("holidayHome", holidayHome);
		model.addAttribute("currentDay", LocalDate.now());
		model.addAttribute("endDay", LocalDate.now().plusDays(2));
		model.addAttribute("userAccount",
				accountRepository.findByAccount_Email(holidayHome.getHostMail()).getAccount());
		return "housedetails";
	}

	@PreAuthorize("hasRole('HOST')")
	@GetMapping(path = "/activateEventPage")
	String activateEventPage(Model model, @RequestParam("holidayHome") ProductIdentifier holidayHomeId) {
		System.out.println("BEREITS AKTIVE EVENTS: " + Hcatalog.findById(holidayHomeId).get().acceptedEvents);
		List<Event> nonActivtedEvents = new LinkedList<>();
		List<Event> allEvents = Ecatalog.findAll().toList();
		for (int i = 0; i < allEvents.size(); i++) {
			if (!Hcatalog.findById(holidayHomeId).get().getAcctivatEvents().contains(allEvents.get(i))) {
				if (allEvents.get(i).isEventStatus()) {
					nonActivtedEvents.add(allEvents.get(i));
				}
			}
		}
		model.addAttribute("nonActivatedEventCatalog", nonActivtedEvents);
		model.addAttribute("holidayHome", Hcatalog.findById(holidayHomeId).get());
		return "houseEventaccepts";
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping(path = "/activateEventForHouse")
	String activateEventForHolidayHome(Model model, @RequestParam("holidayHome") ProductIdentifier holidayHomeId,
	                                   @RequestParam("event") ProductIdentifier eventId) {
		HolidayHome holidayHome = Hcatalog.findById(holidayHomeId).get();
		holidayHome.acceptEvent(Ecatalog.findById(eventId).get());
		Hcatalog.save(Hcatalog.findById(holidayHomeId).get());
		return "redirect:/activateEventPage?holidayHome=" + holidayHomeId.toString();
	}

	@PreAuthorize("hasRole('HOST')")
	@PostMapping(path = "/activateAllEventsForHouse")
	String activateAllEventsForHolidayHome(Model model, @RequestParam("holidayHome") ProductIdentifier holidayHomeId) {
		List<Event> nonActivtedEvents = new LinkedList<Event>();
		for (int i = 0; i < Ecatalog.findAll().toList().size(); i++) {
			if (!Hcatalog.findById(holidayHomeId).get().getAcctivatEvents()
					.contains(Ecatalog.findAll().toList().get(i))) {
				nonActivtedEvents.add(Ecatalog.findAll().toList().get(i));
			}
		}
		Hcatalog.findById(holidayHomeId).get().acceptedEvents.addAll(nonActivtedEvents);
		Hcatalog.save(Hcatalog.findById(holidayHomeId).get());
		return "redirect:/holidayhomes";
	}

	// Events------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/events")
	String eventCatalog(Model model) {
		recentEvent.clear();

		updateSmallEvents();
		firstname(model);

		ArrayList<ProductIdentifier> recent = new ArrayList();
		Ecatalog.findAll()
				.filter(event -> event.getDate().isAfter(LocalDate.now())
						|| event.getDate().isEqual(LocalDate.now()))
				.forEach(item -> recent.add(item.getId()));

		LocalDate latestdate = Ecatalog.findById(recent.get(0)).get().getDate();
		LocalTime latesttime = Ecatalog.findById(recent.get(0)).get().getTime();

		ProductIdentifier latestID = recent.get(0);
		int count = 0;
		for (int i = 0; i < recent.size(); i++) {
			LocalDate nextdate = Ecatalog.findById(recent.get(i)).get().getDate();
			LocalTime nexttime = Ecatalog.findById(recent.get(i)).get().getTime();
			if (nextdate.equals(latestdate)) {
				if (nexttime.isBefore(latesttime)) {
					latestdate = nextdate;
					latesttime = nexttime;
					latestID = recent.get(count);
				}
			}
			if (nextdate.isBefore(latestdate)) {
				latestdate = nextdate;
				latesttime = nexttime;
				latestID = recent.get(count);
			}
			count++;
		}
		//System.out.println(latest);
		//System.out.println(latestID);
		final ProductIdentifier finallatestID = latestID;
		Ecatalog.findAll().filter(event -> event.getId().equals(finallatestID)).forEach(item -> recentEvent.add(item));

		Ecatalog.findAll().forEach(item -> sortEventTypeList.add(item));
		Ecatalog.findAll().forEach(item -> sortEventCapacityList.add(item));
		Ecatalog.findAll().forEach(item -> sortEventDistrictList.add(item));

		model.addAttribute("recentEvent", recentEvent);
		model.addAttribute("eventCatalog", Ecatalog.findAll()
				.filter(event -> event.getDate().isAfter(LocalDate.now())
						|| event.getDate().isEqual(LocalDate.now())));

		Object formatter = new StringFormatter();
		model.addAttribute("formatter", formatter);

		return "events";
	}

	private void updateSmallEvents() {
		LocalDate todayDate = LocalDate.now();
		LocalTime todayTime = LocalTime.now(ZoneId.systemDefault());

		Streamable<Event> allSmallEvents = Ecatalog.findAllByEventType(EventType.SMALL);
		for (Event e : allSmallEvents) {
			boolean todayDateBefore = todayDate.isBefore(e.getDate());
			boolean todayTimeBefore = todayTime.isBefore(e.getTime());

			if (todayDate.isEqual(e.getDate())) {
				if (!todayTimeBefore) {
					e.setDate(e.getDate().plusWeeks(1));
				}
			} else if (!todayDateBefore) {
				e.setDate(e.getDate().plusWeeks(1));

			}
			Ecatalog.save(e);
		}
	}

	@PreAuthorize("!hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/events")
	String sortEvent(Model model, String searchedEventType, String searchedEventCapacity,
	                 String searchedEventDistrict) {
		firstname(model);

		if (searchedEventType == null && searchedEventCapacity == null && searchedEventDistrict == null) {
			/*Ecatalog.findAll().forEach(item -> sortEventTypeList.add(item));
			Ecatalog.findAll().forEach(item -> sortEventCapacityList.add(item));
			Ecatalog.findAll().forEach(item -> sortEventDistrictList.add(item));
			*/
			eventCatalog(model);
		}

		if (searchedEventType != null) {
			sortEventTypeList.clear();

			if (searchedEventType.equals("Alle")) {
				Ecatalog.findAll().forEach(item -> sortEventTypeList.add(item));
			} else if (searchedEventType.equals("SMALL")) {
				Ecatalog.findAll().filter(events -> events.getEventType() == EventType.SMALL)
						.forEach(item -> sortEventTypeList.add(item));
			} else if (searchedEventType.equals("LARGE")) {
				Ecatalog.findAll().filter(events -> events.getEventType() == EventType.LARGE)
						.forEach(item -> sortEventTypeList.add(item));
			}
		}

		if (searchedEventCapacity != null) {
			sortEventCapacityList.clear();

			if (searchedEventCapacity.equals("Alle")) {
				Ecatalog.findAll().forEach(item -> sortEventCapacityList.add(item));
			} else {
				int searchedCapacityInt = Integer.parseInt(searchedEventCapacity);
				Ecatalog.findAll().filter(holidayHome -> holidayHome.getCapacity() >= searchedCapacityInt)
						.forEach(item -> sortEventCapacityList.add(item));
			}
		}

		if (searchedEventDistrict != null) {
			sortEventDistrictList.clear();

			if (searchedEventDistrict.equals("Alle")) {
				Ecatalog.findAll().forEach(item -> sortEventDistrictList.add(item));
			} else if (!searchedEventDistrict.equals("Alle")) {
				Ecatalog.findAll().filter(event -> event.getPlace().getDistrict().equals(searchedEventDistrict))
						.forEach(item -> sortEventDistrictList.add(item));
			}
		}
		System.out.println(sortEventTypeList);
		System.out.println(sortEventCapacityList);
		System.out.println(sortEventDistrictList);

		model.addAttribute("eventCatalog",
				Ecatalog.findAll().filter(Event::isEventStatus)
						.filter(event -> event.getDate().isAfter(LocalDate.now())
								|| event.getDate().isEqual(LocalDate.now()))
						.filter(event -> event.findInList(event, sortEventCapacityList))
						.filter(event -> event.findInList(event, sortEventTypeList))
						.filter(event -> event.findInList(event, sortEventDistrictList)));

		Object formatter = new StringFormatter();
		model.addAttribute("formatter", formatter);

		return "events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/cancelevent")
	String cancelEvent(@RequestParam("event") Event event) {
		bookingManagement.cancelEvent(event);
		event.setEventStatus(false);
		Ecatalog.save(event);
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/activateevent")
	String activateEvent(@RequestParam("event") Event event) {
		event.setEventStatus(true);
		Ecatalog.save(event);
		return "redirect:/events";
	}

	// es muss noch kontrolliert werden ob das Event in einer Buchung ist
	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/deleteevent")
	String deleteEvent(@RequestParam("event") Event event) {
		System.out.println("zu löschendes Event " + event);
		if (holidayHomeStorage.findByProduct(event).isPresent()) {
			if (!event.isEventStatus()) {
				// holidayHomeStorage.delete(holidayHomeStorage.findByProduct(event).get());
				// Ecatalog.delete(event);
				Ecatalog.delete(event);
			}
		}
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/editeventpage")
	String editEventPage(@RequestParam("event") Event event, Model model) {
		firstname(model);
		model.addAttribute("event", event);
		model.addAttribute("today", LocalDate.now());
		return "editevent";
	}

	// überarbeiten...
	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@PostMapping("/editEvent")
	String editEvent(Model model, @RequestParam("eventId") ProductIdentifier eventId, @RequestParam("name") String name,
	                 @RequestParam("description") String description, @RequestParam("price") String price,
	                 @RequestParam("date") String date, @RequestParam("time") String time,
	                 @RequestParam("capacity") String capacity, @RequestParam("street") String street,
	                 @RequestParam("houseNumber") String houseNumber, @RequestParam("postalCode") String postalCode,
	                 @RequestParam("city") String city, @RequestParam("coordinates_x") String coordinates_x,
	                 @RequestParam("coordinates_y") String coordinates_y) {
		firstname(model);
		System.out.println(eventId);
		Ecatalog.findById(eventId);
		boolean eventPlaceUpdated = false;
		if (Ecatalog.findById(eventId).isPresent()) {
			Event EventToChange = Ecatalog.findById(eventId).get();
			if (!name.isBlank()) {
				EventToChange.setName(name);
			}
			if (!description.isBlank()) {
				EventToChange.setDescription(description);
			}
			if (!price.isBlank()) {
				int price2 = Integer.parseInt(price);
				EventToChange.setPrice(Money.of(price2, "EUR"));
			}
			if (!date.isBlank()) {
				EventToChange.setDate(LocalDate.parse(date));
			}
			if (!time.isBlank()) {
				EventToChange.setTime(LocalTime.parse(time));
			}
			if (!capacity.isBlank()) {
				int capacity2 = Integer.parseInt(capacity);
				EventToChange.setCapacity(capacity2);
			}
			Place changedPlace = EventToChange.getPlace();
			if (!street.isBlank()) {
				changedPlace.setStreet(street);
				eventPlaceUpdated = true;
			}
			if (!houseNumber.isBlank()) {
				changedPlace.setHouseNumber(houseNumber);
				eventPlaceUpdated = true;
			}
			if (!postalCode.isBlank()) {
				changedPlace.setPostalCode(postalCode);
				eventPlaceUpdated = true;
			}
			if (!city.isBlank()) {
				changedPlace.setCity(city);
				eventPlaceUpdated = true;
			}
			if (!coordinates_x.isBlank()) {
				int coordinates_x2 = Integer.parseInt(coordinates_x);
				changedPlace.setCoordX(coordinates_x2);
			}
			if (!coordinates_y.isBlank()) {
				int coordinates_y2 = Integer.parseInt(coordinates_y);
				changedPlace.setCoordY(coordinates_y2);
			}
			EventToChange.setPlace(changedPlace);
			System.out.println(EventToChange);
			Ecatalog.save(EventToChange);
			// holidayHomeStorage.save(UniqueInventoryItem(EventToChange,
			// EventToChange.getCapacity()));
		}

		if (eventPlaceUpdated) {
			return "redirect:/editEventLocation?event=" + eventId.toString();
		}
		return "redirect:/events";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@GetMapping("/addevents")
	String addEventPage(Model model, EventForm form) {
		firstname(model);
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("form", form);
		return "addevent";
	}

	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	@GetMapping("/addsmallevents")
	String addSmallEventPage(Model model, EventForm form) {
		firstname(model);
		model.addAttribute("today", LocalDate.now());
		model.addAttribute("form", form);
		return "addsmallevent";
	}


	@PostMapping(path = "/addEvent")
	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	String addEvent(@LoggedIn UserAccount userAccount,
	                @RequestParam("imageupload") MultipartFile image,
	                @Valid @ModelAttribute("form") EventForm form,
	                BindingResult result,
	                Model model) {

		firstname(model);
		String errorReturn;
		errorReturn = form.getEventType().equals(EventType.SMALL) ? "addsmallevent" : "addevent";

		if (result.hasErrors()) {
			System.out.println(errorReturn);
			return errorReturn;
		}

		System.out.println(userAccount.getId().getIdentifier());
		Event event = form.toNewEvent(userAccount.getId().getIdentifier());

		Ecatalog.save(event);
		holidayHomeStorage.save(new UniqueInventoryItem(event, Quantity.of(event.getCapacity())));

		ProductIdentifier productIdentifier = event.getId();

		String[] strings = image.getContentType().split("/");
		String contentType = "." + strings[strings.length - 1];
		String fileName = productIdentifier.getIdentifier() + contentType;
		Path fileNameAndPath = Paths.get(uploadDirectory, fileName);

		storageService.store(image, fileName);
		Event newEvent = Ecatalog.findFirstByProductIdentifier(productIdentifier);
		newEvent.setImage(fileName);
		Ecatalog.save(newEvent);

		return "redirect:/editEventLocation?event=" + productIdentifier.toString();
	}

	@GetMapping("/map")
	public String map(@Valid @ModelAttribute("coordinates") Coordinates coordinates, Model model) {
		firstname(model);
		return "map";
	}

	@PostMapping("/map")
	public String postmap(@RequestParam(value = "size") String size,
	                      @RequestParam("productIdentifier") ProductIdentifier productIdentifier,
	                      @Valid @ModelAttribute("coordinates") Coordinates coordinates, HttpServletRequest httpServletRequest) {

		Coordinates productCoordinates = new Coordinates(size);
		String returnPage = "/";

		if (httpServletRequest.getHeader("referer").contains("editEventLocation")) {
			Event event = Ecatalog.findById(productIdentifier).get();
			Place eventPlace = event.getPlace();
			eventPlace.setCoordX(productCoordinates.xRatio);
			eventPlace.setCoordY(productCoordinates.yRatio);
			eventPlace.setDistrict(productCoordinates.getDistrict());
			event.setPlace(eventPlace);
			Ecatalog.save(event);
			Event eventtest = Ecatalog.findById(productIdentifier).get();

			returnPage = "/events";
		}
		if (httpServletRequest.getHeader("referer").contains("editHolidayHomeLocation")) {
			HolidayHome holidayHome = Hcatalog.findById(productIdentifier).get();
			System.out.println("PLACE VORHER:" + holidayHome.getPlace().toString());
			Place holidayHomePlace = holidayHome.getPlace();
			holidayHomePlace.setCoordX(productCoordinates.xRatio);
			holidayHomePlace.setCoordY(productCoordinates.yRatio);
			holidayHomePlace.setDistrict(productCoordinates.getDistrict());
			holidayHome.setPlace(holidayHomePlace);
			Hcatalog.save(holidayHome);
			HolidayHome holidayHometest = Hcatalog.findById(productIdentifier).get();
			System.out.println("PLACE NACHER:" + holidayHometest.getPlace().toString());
			returnPage = "/holidayhomes";
		}
		return "redirect:" + returnPage;
	}

	@GetMapping("/editEventLocation")
	@PreAuthorize("hasRole('EVENT_EMPLOYEE')")
	public String editEventLocation(@RequestParam("event") ProductIdentifier productIdentifier,
	                                @ModelAttribute("coordinates") Coordinates coordinates, Model model) {
		model.addAttribute("productIdentifier", productIdentifier);
		return "map";
	}

	@GetMapping("/editHolidayHomeLocation")
	@PreAuthorize("hasRole('HOST')")
	public String editHolidayHomeLocation(@RequestParam("holidayhome") ProductIdentifier productIdentifier,
	                                      @ModelAttribute("coordinates") Coordinates coordinates, Model model) {
		model.addAttribute("productIdentifier", productIdentifier);
		return "map";
	}

}
