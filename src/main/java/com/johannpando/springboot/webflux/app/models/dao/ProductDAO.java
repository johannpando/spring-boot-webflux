package com.johannpando.springboot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.johannpando.springboot.webflux.app.models.documents.Product;

public interface ProductDAO extends ReactiveMongoRepository<Product, String>{
	
	
}
