package ma.inpt.rentingCarApp.controllers;

import ma.inpt.rentingCarApp.entities.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ma.inpt.rentingCarApp.services.UserService;

@Controller
public class SecurityController {

	// class attributes :
	final BCryptPasswordEncoder pwEncoder;
	final UserService accService;

	// class constructor :
	public SecurityController(BCryptPasswordEncoder pwEncoder, UserService accService) {
		this.pwEncoder = pwEncoder;
		this.accService = accService;
	}

	// class methods :
	@GetMapping(value="/login")
	public String login() {
		return "security/login.html";
	}
	
	@GetMapping(value="/logout")
	public String logout() {
		return "security/logout.html";
	}
	
	@GetMapping(value="/register")
	public String register(Model model) {
		model.addAttribute("newAccount", new User());
		return "security/register.html";
	}
	
	@PostMapping(value="/register/save")
	public String saveNewAccount(User account) {
		account.setPassword(pwEncoder.encode(account.getPassword()));
		accService.save(account);
		return "redirect:/register/accountcreated";
	}
	
	@GetMapping(value="/register/accountcreated")
	public String accountCreated() {
		return "security/account-created.html";
	}	
}
