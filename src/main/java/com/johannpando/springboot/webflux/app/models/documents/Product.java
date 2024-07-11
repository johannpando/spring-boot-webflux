package com.johannpando.springboot.webflux.app.models.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products")
@Data
@NoArgsConstructor
public class Product {

	@Id
	private String id;
	
	@NotEmpty
	private String name;
	
	@NotNull
	private Double price;
	
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date createAt;
	
	public Product(String name, double price) {
		this.name = name;
		this.price = price;
	}
}
