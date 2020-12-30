package fewodre.useraccounts;

import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class AccountController {

	private final AccountManagement accountManagement;
	private final AccountRepository accountRepository;
	private final UserAccountManagement userAccounts;
	private Authentication authentication;

	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

	AccountController(AccountManagement accountManagement, AccountRepository accountRepository, UserAccountManagement userAccounts) {
		Assert.notNull(accountManagement, "CustomerManagement must not be null!");
		this.accountManagement = accountManagement;
		this.accountRepository = accountRepository;
		this.userAccounts = userAccounts;
	}

	private void firstname(Model model) {
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (! authentication.getPrincipal().equals("anonymousUser") &&  ! authentication.getName().equals("admin") ) {
			model.addAttribute("firstname", accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		}
	}

	@GetMapping("/register")
	public String registerAdmin(Model model, TenantRegistrationForm tenantRegistrationForm) {
		model.addAttribute("registrationForm", tenantRegistrationForm);
		firstname(model);
		return "register";
	}

	@PostMapping("/register")
	public String registerNewAccount(@Valid @ModelAttribute("tenantRegistrationForm") TenantRegistrationForm tenantRegistrationForm,
	                                 BindingResult result, Model model) {
		LOG.info(tenantRegistrationForm.getBirthDate());
		if (result.hasErrors()) {
			LOG.info(result.getAllErrors().toString());
			return "register";
		}

		AccountEntity accountEntity = accountManagement.createTenantAccount(tenantRegistrationForm);
		if (accountEntity == null) {
			result.reject("RegistrationForm.username.Taken");
			LOG.info(result.getAllErrors().toString());
			return "register";
		}

		AccountEntity test = accountRepository.findByAccount_Email(tenantRegistrationForm.getEmail());
		LOG.info(test.toString());

		return "redirect:/login";
	}

	@GetMapping("/map")
	public String map(@Valid @ModelAttribute("coordinates") Coordinates coordinates, Model model) {
		firstname(model);
		return "map";
	}

	@PostMapping("/map")
	public String postmap(@RequestParam(value = "size") String size, @Valid @ModelAttribute("coordinates") Coordinates coordinates) {
		System.out.println(size);
		Coordinates test = new Coordinates(size);
		return "map";
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

	@GetMapping("/managetenantaccount")
	public String editUser(Model model, String firstname){
		firstname(model);
		System.out.println(authentication.getPrincipal());
		if (! authentication.getPrincipal().toString().contains("TENANT")){
			return "/login";
		}
		model.addAttribute("firstname", accountRepository.findByAccount_Email(authentication.getName()).getAccount().getFirstname());
		model.addAttribute("lastname", accountRepository.findByAccount_Email(authentication.getName()).getAccount().getLastname());
		model.addAttribute("email", accountRepository.findByAccount_Email(authentication.getName()).getAccount().getEmail());
		model.addAttribute("birthdate", accountRepository.findByAccount_Email(authentication.getName()).getBirthDate());
		model.addAttribute("street", accountRepository.findByAccount_Email(authentication.getName()).getStreet());
		model.addAttribute("housenumber", accountRepository.findByAccount_Email(authentication.getName()).getHouseNumber());
		model.addAttribute("postcode", accountRepository.findByAccount_Email(authentication.getName()).getPostCode());
		model.addAttribute("city", accountRepository.findByAccount_Email(authentication.getName()).getCity());

		return "accounts/managetenantaccount";
	}

	@PostMapping("/managetenantaccount")
	public String postEditUser(Model model, String firstname, String lastname, String password, String birthdate, String street, String housenumber, String postcode, String city) {
		Authentication authentication;
		authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!firstname.isEmpty()){accountRepository.findByAccount_Email(authentication.getName()).getAccount().setFirstname(firstname);}
		if(!lastname.isEmpty()){accountRepository.findByAccount_Email(authentication.getName()).getAccount().setLastname(lastname);}
		if(!birthdate.isEmpty()){accountRepository.findByAccount_Email(authentication.getName()).setBirthDate(birthdate);}
		if(!street.isEmpty()){accountRepository.findByAccount_Email(authentication.getName()).setStreet(street);}
		if(!housenumber.isEmpty()){accountRepository.findByAccount_Email(authentication.getName()).setHouseNumber(housenumber);}
		if(!street.isEmpty()){accountRepository.findByAccount_Email(authentication.getName()).setStreet(street);}
		if(!postcode.isEmpty()){accountRepository.findByAccount_Email(authentication.getName()).setPostCode(postcode);}
		if(!city.isEmpty()){accountRepository.findByAccount_Email(authentication.getName()).setCity(city);}
		if(!password.isEmpty()) {
			UserAccount baum = userAccounts.findByUsername(accountRepository.findByAccount_Email(authentication.getName()).getAccount().getUsername()).get();
			userAccounts.changePassword(baum, Password.UnencryptedPassword.of(password));
		}
		model.addAttribute("tenant", accountManagement.findAllDisabled());

		return "accounts/managetenantaccount";
	}

	@GetMapping("/newhost")
	public String registerHost(Model model, HostRegistrationForm hostRegistrationForm) {
		firstname(model);
		model.addAttribute("registrationForm", hostRegistrationForm);
		return "accounts/newhost";
	}

	@PostMapping("/newhost")
	public String registerNewHost(@Valid @ModelAttribute("hostRegistrationForm") HostRegistrationForm hostRegistrationForm,
	                              BindingResult result, Model model) {
		LOG.info(hostRegistrationForm.getBirthDate());
		if (result.hasErrors()) {
			LOG.info(result.getAllErrors().toString());
			return "accounts/newhost";
		}

		AccountEntity accountEntity = accountManagement.createHostAccount(hostRegistrationForm);
		if (accountEntity == null) {
			result.reject("RegistrationForm.username.Taken");
			LOG.info(result.getAllErrors().toString());
			return "accounts/newhost";
		}

		AccountEntity test = accountRepository.findByAccount_Email(hostRegistrationForm.getEmail());
		LOG.info(test.toString());

		return "redirect:accounts/newhost";
	}

	@GetMapping("/neweventemployee")
	public String registerEventEmployee(Model model, EventEmployeeRegistrationForm eventEmployeeRegistrationForm) {
		firstname(model);
		model.addAttribute("registrationForm", eventEmployeeRegistrationForm);
		return "accounts/neweventemployee";
	}

	@PostMapping("/neweventemployee")
	public String registerNewEventEmployee(@Valid @ModelAttribute("eventEmployeeRegistrationForm") EventEmployeeRegistrationForm eventEmployeeRegistrationForm,
	                                       BindingResult result, Model model) {

		AccountEntity accountEntity = accountManagement.createEventEmployeeAccount(eventEmployeeRegistrationForm);
		if (accountEntity == null) {
			result.reject("RegistrationForm.username.Taken");
			LOG.info(result.getAllErrors().toString());
			return "accounts/neweventemployee";
		}

		AccountEntity test = accountRepository.findByAccount_Email(eventEmployeeRegistrationForm.getEmail());
		LOG.info(test.toString());

		return "redirect:accounts/neweventemployee";
	}

//	@PreAuthorize("hasRole('ADMIN')")
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
	 * POST-request-mapping to enable a tenant account. This is usually done after a new tenant has successfully created
	 * a new account.
	 *
	 * @param tenant_username   specifies the account that will be enabled (username == e-mail adress in our case).
	 * @return                  redirects the admin to the account management page.
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
		accountManagement.deleteAccount(account_username);
		return "redirect:/manageaccounts";
	}

}