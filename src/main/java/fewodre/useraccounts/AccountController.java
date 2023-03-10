package fewodre.useraccounts;

import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.catalog.holidayhomes.HolidayHomeCatalog;
import fewodre.useraccounts.forms.EventEmployeeForm;
import fewodre.useraccounts.forms.HostForm;
import fewodre.useraccounts.forms.TenantForm;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Set;

@Controller
public class AccountController {

	private final AccountManagement accountManagement;
	private final AccountRepository accountRepository;
	private final UserAccountManagement userAccounts;
	private Authentication authentication;
	private final HolidayHomeCatalog hCatalog;

	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

	AccountController(AccountManagement accountManagement, AccountRepository accountRepository,
	                  HolidayHomeCatalog hCatalog, UserAccountManagement userAccounts) {
		Assert.notNull(accountManagement, "CustomerManagement must not be null!");
		this.accountManagement = accountManagement;
		this.accountRepository = accountRepository;
		this.userAccounts = userAccounts;
		this.hCatalog = hCatalog;
	}

	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null
				&& !authentication.getPrincipal().equals("anonymousUser")
				&& !authentication.getName().equals("admin")) {
			model.addAttribute("firstname_user",
					accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		}
	}

	@GetMapping("/register")
	public String registerAdmin(Model model, TenantForm tenantForm) {
		model.addAttribute("registrationForm", tenantForm);
		model.addAttribute("minAgeDate", LocalDate.now().minusDays(5840));
		firstname(model);
		return "register";
	}

	@PostMapping("/register")
	public String registerNewAccount(
			@Valid @ModelAttribute("tenantForm") TenantForm tenantForm,
			BindingResult result) {

		LocalDate minAgeDate = LocalDate.now().minusDays(5840);
		String birthDate = tenantForm.getBirthDate();
		LocalDate localDateBirthDate;

		if(!birthDate.isEmpty()) {
			localDateBirthDate = LocalDate.parse(birthDate);
			if (!localDateBirthDate.isBefore(minAgeDate)) {
				result.addError(new FieldError("tenantForm", "birthDate",
						"Sie m??ssen mindestens 18 Jahre alt sein."));
			}
		}
		else {
			result.addError(new FieldError("tenantForm", "birthDate",
					"Bitte geben Sie ein Geburtsdatum an."));
		}

		if(!result.hasErrors()) {
			AccountEntity accountEntity = accountManagement.createTenantAccount(tenantForm);
			if (accountEntity == null) {
				result.addError(new FieldError("tenantForm", "email",
						"RegistrationForm.username.Taken"));
				LOG.info(result.getAllErrors().toString());
			}
		}

		if (result.hasErrors()) {
			LOG.info(result.getAllErrors().toString());
			return "register";
		}

		return "redirect:/login";
	}

	@GetMapping("/resetpassword")
	public String resetPassword(Model model) {
		firstname(model);
		return "accounts/forgotpassword";
	}

	@GetMapping("/activatetenants")
	public String activateTenants(Model model) {
		model.addAttribute("unactivatedtenants", accountManagement.findAllDisabled());
		firstname(model);
		return "accounts/activatetenants";
	}

	@PostMapping("/activatetenants")
	public String postActivateTenants(Model model, String tenant_username) {
		accountManagement.enableTenant(tenant_username);
		model.addAttribute("unactivatedtenants", accountManagement.findAllDisabled());
		return "accounts/activatetenants";
	}

	@GetMapping("/manageaccount")
	@PreAuthorize("hasAnyRole('TENANT', 'ADMIN', 'HOST', 'EVENT_EMPLOYEE')")
	public String editUser(Model model, String tenant_username) {
		firstname(model);
		String principal = authentication.getPrincipal().toString();
		model.addAttribute("savedchanges", false);

		if (principal.contains("ADMIN")) {
			return getString(model, tenant_username);
		} else {
			return getString(model, authentication.getName());

		}
	}

	@PostMapping("/manageaccount")
	public String postEditUser(Model model, String firstname, String lastname, String password, String birthdate,
	                           String street, String housenumber, String postcode, String city, String iban, String bic,
	                           String eventcompany, String tenant_username) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String editingUserRole = "NONE";

		if (authentication.getPrincipal().toString().contains("ADMIN")) {
			editingUserRole = accountRepository.findByAccount_Email(tenant_username)
					.getAccount().getRoles().stream().findAny().get().toString();
		} else {
			tenant_username = authentication.getName();
			if (authentication.getPrincipal().toString().contains("TENANT")) {
				editingUserRole = "TENANT";
			} else if (authentication.getPrincipal().toString().contains("HOST")) {
				editingUserRole = "HOST";
			} else if (authentication.getPrincipal().toString().contains("EVENT_EMPLOYEE")) {
				editingUserRole = "EVENT_EMPLOYEE";
			}
		}

		if (editingUserRole.equals("NONE")) {
			return "/login";
		}

		if (!firstname.isEmpty()) {
			accountRepository.findByAccount_Email(tenant_username).getAccount().setFirstname(firstname);
		}
		if (!lastname.isEmpty()) {
			accountRepository.findByAccount_Email(tenant_username).getAccount().setLastname(lastname);
		}
		if (editingUserRole.equals("HOST") || editingUserRole.equals("TENANT")) {
			if (!birthdate.isEmpty()) {
				accountRepository.findByAccount_Email(tenant_username).setBirthDate(birthdate);
			}
			if (!street.isEmpty()) {
				accountRepository.findByAccount_Email(tenant_username).setStreet(street);
			}
			if (!housenumber.isEmpty()) {
				accountRepository.findByAccount_Email(tenant_username).setHouseNumber(housenumber);
			}
			if (!street.isEmpty()) {
				accountRepository.findByAccount_Email(tenant_username).setStreet(street);
			}
			if (!postcode.isEmpty()) {
				accountRepository.findByAccount_Email(tenant_username).setPostCode(postcode);
			}
			if (!city.isEmpty()) {
				accountRepository.findByAccount_Email(tenant_username).setCity(city);
			}
		}
		if (editingUserRole.equals("HOST")) {
			if (!iban.isEmpty()) {
				accountRepository.findByAccount_Email(tenant_username).setIban(iban);
			}
			if (!bic.isEmpty()) {
				accountRepository.findByAccount_Email(tenant_username).setBic(bic);
			}
		}
		if (editingUserRole.equals("EVENT_EMPLOYEE") && !eventcompany.isEmpty()) {
			accountRepository.findByAccount_Email(tenant_username).setEventCompany(eventcompany);
		}
		if (!password.isEmpty()) {
			UserAccount account = userAccounts.findByUsername(
					accountRepository.findByAccount_Email(tenant_username)
							.getAccount().getUsername()).get();
			userAccounts.changePassword(account, Password.UnencryptedPassword.of(password));
		}
		model.addAttribute("tenant", accountManagement.findAllDisabled());
		model.addAttribute("savedchanges", true);
		return getString(model, tenant_username);

	}

	private String getString(Model model, String tenant_username) {
		model.addAttribute("firstname",
				accountRepository.findByAccount_Email(tenant_username).getAccount().getFirstname());
		model.addAttribute("lastname",
				accountRepository.findByAccount_Email(tenant_username).getAccount().getLastname());
		model.addAttribute("email",
				accountRepository.findByAccount_Email(tenant_username).getAccount().getEmail());
		model.addAttribute("birthdate",
				accountRepository.findByAccount_Email(tenant_username).getBirthDate());
		model.addAttribute("street",
				accountRepository.findByAccount_Email(tenant_username).getStreet());
		model.addAttribute("housenumber",
				accountRepository.findByAccount_Email(tenant_username).getHouseNumber());
		model.addAttribute("postcode",
				accountRepository.findByAccount_Email(tenant_username).getPostCode());
		model.addAttribute("city",
				accountRepository.findByAccount_Email(tenant_username).getCity());
		model.addAttribute("iban",
				accountRepository.findByAccount_Email(tenant_username).getIban());
		model.addAttribute("bic",
				accountRepository.findByAccount_Email(tenant_username).getBic());
		model.addAttribute("eventcompany",
				accountRepository.findByAccount_Email(tenant_username).getEventCompany());
		model.addAttribute("role",
				accountRepository.findByAccount_Email(tenant_username).getAccount().getRoles()
						.stream().findAny().get());
		return "accounts/manageaccount";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/newhost")
	public String registerHost(Model model, HostForm hostForm) {
		firstname(model);
		model.addAttribute("hostForm", hostForm);
		model.addAttribute("minAgeDate", LocalDate.now().minusDays(5840));
		return "accounts/newhost";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/newhost")
	public String registerNewHost(
			@Valid @ModelAttribute("hostForm") HostForm hostForm,
			BindingResult result) {

		if (result.hasErrors()) {
			return "accounts/newhost";
		}

		LocalDate minAgeDate = LocalDate.now().minusDays(5840);
		String birthDate = hostForm.getBirthDate();
		LocalDate localDateBirthDate = LocalDate.parse(birthDate);

		if (!localDateBirthDate.isBefore(minAgeDate)) {
			result.addError(new FieldError("hostForm", "birthDate",
					"Sie m??ssen mindestens 18 Jahre alt sein!"));
			return "register";
		}

		AccountEntity accountEntity = accountManagement.createHostAccount(hostForm);
		if (accountEntity == null) {
			result.addError(new FieldError("hostForm", "email",
					"RegistrationForm.username.Taken"));
			LOG.info(result.getAllErrors().toString());
			return "accounts/newhost";
		}

		AccountEntity test = accountRepository.findByAccount_Email(hostForm.getEmail());
		LOG.info(test.toString());

		return "redirect:/manageaccounts";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/neweventemployee")
	public String registerEventEmployee(Model model, EventEmployeeForm eventEmployeeForm) {
		firstname(model);
		model.addAttribute("eventEmployeeForm", eventEmployeeForm);
		return "accounts/neweventemployee";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/neweventemployee")
	public String registerNewEventEmployee(
			@Valid @ModelAttribute("eventEmployeeForm")
					EventEmployeeForm eventEmployeeForm,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			LOG.info(result.getAllErrors().toString());
			return "accounts/neweventemployee";
		}

		AccountEntity accountEntity = accountManagement.createEventEmployeeAccount(eventEmployeeForm);
		if (accountEntity == null) {
			result.addError(new FieldError("eventEmployeeForm", "email",
					"RegistrationForm.username.Taken"));
			LOG.info(result.getAllErrors().toString());
			return "accounts/neweventemployee";
		}

		AccountEntity test = accountRepository.findByAccount_Email(eventEmployeeForm.getEmail());
		LOG.info(test.toString());

		return "redirect:/manageaccounts";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/manageaccounts")
	public String manageAccounts(Model model) {
		firstname(model);
		model.addAttribute("unactivatedTenants", accountManagement.findAllDisabled());
		model.addAttribute("activatedTenants", accountManagement.findByRole(AccountManagement.TENANT_ROLE));
		model.addAttribute("hostAccounts", accountManagement.findByRole(AccountManagement.HOST_ROLE));
		model.addAttribute("eventAccounts", accountManagement.findByRole(AccountManagement.EVENTEMPLOYEE_ROLE));
		return "accounts/manageaccounts";
	}

	/**
	 * POST-request-mapping to enable a tenant account. This is usually done after a
	 * new tenant has successfully created a new account.
	 *
	 * @param tenant_username specifies the account that will be enabled (username
	 *                        == e-mail adress in our case).
	 * @return redirects the admin to the account management page.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/activatetenant")
	public String activateTenant(String tenant_username) {
		accountManagement.enableTenant(tenant_username);
		return "redirect:/manageaccounts";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/deleteaccount")
	public String deleteAccount(String account_username) {
		Set<Role> accountRoles = accountRepository.findByAccount_Email(account_username).getAccount().getRoles().toSet();
		if (accountRoles.contains(AccountManagement.HOST_ROLE)) {
			Streamable<HolidayHome> hostHolidayHomes = hCatalog.findAll()
					.filter(holidayHome -> holidayHome.getHostMail().equals(account_username));
			for (HolidayHome h : hostHolidayHomes) {
				h.setIsBookable(false);
				hCatalog.save(h);
			}
		}
		accountManagement.deleteAccount(account_username);
		return "redirect:/manageaccounts";
	}

}