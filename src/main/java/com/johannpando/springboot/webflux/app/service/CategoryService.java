package com.johannpando.springboot.webflux.app.service;

import com.johannpando.springboot.webflux.app.models.documents.Category;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {

	Flux<Category> findAll();
	
	Mono<Category> findById(String id);
	
	Mono<Category> save(Category category);
}
