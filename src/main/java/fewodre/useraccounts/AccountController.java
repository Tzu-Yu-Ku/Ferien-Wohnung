package fewodre.useraccounts;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

	@GetMapping("/account")
	public String account() {
		return "account";
	}


}
