package com.johannpando.springboot.webflux.app.models.documents;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products")
@Data
@NoArgsConstructor
public class Product {

	private String id;
	
	private String name;
	
	private double price;
	
	private Date creatAt;
	
	public Product(String name, double price) {
		this.name = name;
		this.price = price;
	}
}
