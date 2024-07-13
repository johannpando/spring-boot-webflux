package com.johannpando.springboot.webflux.app.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "categories")
@Data
@NoArgsConstructor
public class Category {

	@Id
	@NotEmpty
	private String id;
	
	private String name;
	
	public Category(String name) {
		this.name = name;
	}
}
