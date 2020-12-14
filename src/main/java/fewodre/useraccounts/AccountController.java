package fewodre.useraccounts;

import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
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
	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

	AccountController(AccountManagement accountManagement, AccountRepository accountRepository) {
		Assert.notNull(accountManagement, "CustomerManagement must not be null!");
		this.accountManagement = accountManagement;
		this.accountRepository = accountRepository;
	}

	@GetMapping("/register")
	public String registerAdmin(Model model, TenantRegistrationForm tenantRegistrationForm) {
		model.addAttribute("registrationForm", tenantRegistrationForm);
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
	public String map(@Valid @ModelAttribute("coordinates") Coordinates coordinates) {
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
		return "accounts/activatetenants";
	}

	@PostMapping("/activatetenants")
	public String postActivateTenants(Model model, String tenant_username) {
		accountManagement.enable_tenant(tenant_username);
		model.addAttribute("unactivatedtenants", accountManagement.findAllDisabled());
		return "accounts/activatetenants";
	}

	@GetMapping("/newhost")
	public String registerHost(Model model, HostRegistrationForm hostRegistrationForm) {
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

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/manageaccounts")
	public String manageAccounts(Model model) {
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
		accountManagement.enable_tenant(tenant_username);
		return "redirect:/manageaccounts";
	}
}