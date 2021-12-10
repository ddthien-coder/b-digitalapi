package com.devteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
public class BApiApplication {

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "B-DIGITAL API SERVER START";
	}

	public static void main(String[] args) {
		SpringApplication.run(BApiApplication.class, args);
	}

}
