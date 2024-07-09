package com.johannpando.springboot.webflux.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.johannpando.springboot.webflux.app.models.dao.ProductDAO;
import com.johannpando.springboot.webflux.app.models.documents.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

	private static final Logger log = LoggerFactory.getLogger(ProductRestController.class);
	
	@Autowired
	private ProductDAO productDAO;
	
	@GetMapping
	public Flux<Product> index() {
		Flux<Product> products = productDAO.findAll();
		return products;
	}
	
	@GetMapping("/{id}")
	public Mono<Product> productById(@PathVariable String id) {
		
		// With this example must be enough
		// Mono<Product> product = productDAO.findById(id);
		
		// We use the filter command
		Flux<Product> products = productDAO.findAll();
		
		Mono<Product> product = products.filter(p -> p.getId().equalsIgnoreCase(id))
			.next();
		
		
		return product;
		
	}
}
