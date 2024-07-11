package com.johannpando.springboot.webflux.app.controllers;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.johannpando.springboot.webflux.app.models.documents.Product;
import com.johannpando.springboot.webflux.app.service.ProductService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SessionAttributes
@Controller
@RequestMapping("/api/crud/products")
public class ProductCRUDController {

	private static final Logger log = LoggerFactory.getLogger(ProductCRUDController.class);
	
	@Autowired
	private ProductService productService;
	
	@GetMapping({"/to-list", ""})
	public Mono<String> toList(Model model) {
		
		Flux<Product> products = productService.findAll();
		//products.subscribe();
		
		model.addAttribute("products", products);
		model.addAttribute("title", "List of products");
		
		return Mono.just("toList");
	}
	
	@GetMapping("/form")
	public Mono<String> createForm(Model model) {
		
		model.addAttribute("product", new Product());
		model.addAttribute("title", "Form of product");
		model.addAttribute("button", "Create");
		
		return Mono.just("form");
	}
	
	@PostMapping("/form")
	// @Valid and BindingResult must be declared one after the other
	// SessionStatus is Spring interface that allow to handlers to mark a session like completed
	
	public Mono<String> saveProduct(@Valid Product product, BindingResult result, Model model, SessionStatus sessionStatus) {
		
		if (result.hasErrors()) {
			model.addAttribute("title", "There are errors in the form");
			model.addAttribute("button", "Save Product");
			return Mono.just("form");
		} else {
			
			/**
			 * Mark the current handler's session processing as complete, allowing for
			 * cleanup of session attributes.
			 */
			sessionStatus.setComplete();
			if (product.getCreateAt() == null) {
				product.setCreateAt(new Date());
			}
			
			return productService.save(product)
				.doOnNext(p -> {
					log.info("Product save with name: " + p.getName() + " and ID: " + product.getId());
				}).thenReturn("redirect:/api/crud/products/to-list?success=The+product+has+been+saved+successfully");
		}
	}
}
