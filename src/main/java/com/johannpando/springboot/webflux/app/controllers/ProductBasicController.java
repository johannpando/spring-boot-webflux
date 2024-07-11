package com.johannpando.springboot.webflux.app.controllers;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import com.johannpando.springboot.webflux.app.models.dao.ProductDAO;
import com.johannpando.springboot.webflux.app.models.documents.Product;

import reactor.core.publisher.Flux;

@Controller
public class ProductBasicController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductBasicController.class);
	
	@Autowired
	private ProductDAO productDAO;
	
	@GetMapping({"/tolist", "/"})
	public String toList(Model model) {
		
		// In this example, the view displays the BBDD results
		// But the log displays with the name in upperCase
		
		/*Flux<Product> products = productDAO.findAll();
		
		products.map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		})
		.subscribe(product -> log.info(product.getName()));*/
		
		// In this case, the view displays both names in upperCase
		Flux<Product> products = productDAO.findAll()		
		.map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		});
		
		products.subscribe(product -> log.info(product.getName()));
		
		model.addAttribute("title", "Product List");
		model.addAttribute("products", products);
		
		return "toList";
	}
	
	@GetMapping("/tolist-datadriver")
	public String toListDataDriver(Model model) {
		
		Flux<Product> products = productDAO.findAll()		
		.map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		})
		.delayElements(Duration.ofSeconds(1));
		
		products.subscribe(product -> log.info(product.getName()));
		
		model.addAttribute("title", "Product List");
		model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 1));
		
		return "toList";
	}
	
	@GetMapping("/tolist-chunked-full")
	public String toListDataDriverChunkedFull(Model model) {
		
		Flux<Product> products = productDAO.findAll()		
		.map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		})
		.repeat(5000);
					
		model.addAttribute("title", "Product List");
		model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 1));
		
		return "toList";
	}

}
