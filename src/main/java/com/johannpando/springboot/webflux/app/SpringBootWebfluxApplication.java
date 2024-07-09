package com.johannpando.springboot.webflux.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.johannpando.springboot.webflux.app.models.dao.ProductDAO;
import com.johannpando.springboot.webflux.app.models.documents.Product;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(SpringBootApplication.class);
	
	@Autowired
	private ProductDAO productDAO;
	
	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		create();
	}

	private void create() {
		
		// We drop the collection before it is created
		reactiveMongoTemplate.dropCollection("products")
		// If we do not subscribe, nothing will happen
		.subscribe();
		
		Flux.just(new Product("IPhone 5", 450.89),
				new Product("IPhone 6", 500.89),
				new Product("Iphone 7", 790.90))
		//.map(product -> productDAO.save(product)) //Return the Mono<T>
		.flatMap(product -> productDAO.save(product))
		.subscribe(product -> log.info("Insert: " + product.getId() + " " + product.getName()));
	}

}
