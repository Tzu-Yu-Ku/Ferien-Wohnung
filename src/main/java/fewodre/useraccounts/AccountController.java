package fewodre.useraccounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		if(accountEntity == null) {
			result.reject("RegistrationForm.username.Taken");
			LOG.info(result.getAllErrors().toString());
			return "register";
		}

		AccountEntity test = accountRepository.findByAccount_Email(tenantRegistrationForm.getEmail());
		LOG.info(test.toString());

		return "redirect:/login";
	}

	@GetMapping("/map")
	public String map(@Valid @ModelAttribute("coordinates")Coordinates coordinates){
		return "map";
	}

	@PostMapping("/map")
	public String postmap(@RequestParam(value = "size") String size, @Valid @ModelAttribute("coordinates") Coordinates coordinates) {
		System.out.println(size);
			return "map";
	}

	@GetMapping("/newhost")
	public String registerHost(Model model, HostRegistrationForm hostRegistrationForm) {
		model.addAttribute("registrationForm", hostRegistrationForm);
		return "newhost";
	}

	@PostMapping("/newhost")
	public String registerNewHost(@Valid @ModelAttribute("hostRegistrationForm") HostRegistrationForm hostRegistrationForm,
									 BindingResult result, Model model) {
		LOG.info(hostRegistrationForm.getBirthDate());
		if (result.hasErrors()) {
			LOG.info(result.getAllErrors().toString());
			return "newhost";
		}

		AccountEntity accountEntity = accountManagement.createHostAccount(hostRegistrationForm);
		if(accountEntity == null) {
			result.reject("RegistrationForm.username.Taken");
			LOG.info(result.getAllErrors().toString());
			return "newhost";
		}

		AccountEntity test = accountRepository.findByAccount_Email(hostRegistrationForm.getEmail());
		LOG.info(test.toString());

		return "redirect:/newhost";
	}

	@GetMapping("/neweventemployee")
	public String registerEventEmployee(Model model, EventEmployeeRegistrationForm eventEmployeeRegistrationForm) {
		model.addAttribute("registrationForm", eventEmployeeRegistrationForm);
		return "neweventemployee";
	}

	@PostMapping("/neweventemployee")
	public String registerNewEventEmployee(@Valid @ModelAttribute("eventEmployeeRegistrationForm") EventEmployeeRegistrationForm eventEmployeeRegistrationForm,
								  BindingResult result, Model model) {

		AccountEntity accountEntity = accountManagement.createEventEmployeeAccount(eventEmployeeRegistrationForm);
		if(accountEntity == null) {
			result.reject("RegistrationForm.username.Taken");
			LOG.info(result.getAllErrors().toString());
			return "neweventemployee";
		}

		AccountEntity test = accountRepository.findByAccount_Email(eventEmployeeRegistrationForm.getEmail());
		LOG.info(test.toString());

		return "redirect:/neweventemployee";
	}
}
