package co.bilira.quote.controller;

import co.bilira.quote.model.QuoteResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class QuoteController {
	@PostMapping(value = "/quote", produces = {"application/json"})
	public @ResponseBody
	QuoteResponseDto quote() {
		// TODO:
		return null;
	}
}
