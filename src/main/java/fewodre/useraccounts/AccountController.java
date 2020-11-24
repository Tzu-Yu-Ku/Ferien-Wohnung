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
	public String registerNewAccount(@Valid @ModelAttribute("registrationForm") TenantRegistrationForm tenantRegistrationForm,
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

}
