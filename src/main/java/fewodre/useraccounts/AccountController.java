package fewodre.useraccounts;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class AccountController {

	private final AccountManagement accountManagement;

	AccountController(AccountManagement accountManagement) {
		Assert.notNull(accountManagement, "CustomerManagement must not be null!");
		this.accountManagement = accountManagement;
	}

	@GetMapping("/register")
	public String register(Model model, RegistrationForm registrationForm) {
		model.addAttribute("registrationForm", registrationForm);
		return "register";
	}

	@PostMapping("/register")
	public String registerNewAccount() {
		return "redirect:/login";
	}

}
