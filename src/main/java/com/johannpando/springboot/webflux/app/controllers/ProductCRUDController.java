package com.johannpando.springboot.webflux.app.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.johannpando.springboot.webflux.app.models.documents.Category;
import com.johannpando.springboot.webflux.app.models.documents.Product;
import com.johannpando.springboot.webflux.app.service.CategoryService;
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

	@Autowired
	private CategoryService categoryService;

	@GetMapping({ "/to-list", "" })
	public Mono<String> toList(Model model) {

		Flux<Product> products = productService.findAll();
		// products.subscribe();

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
	public Mono<String> saveProduct(@Valid Product product, BindingResult result, Model model, 
			SessionStatus sessionStatus, @RequestPart FilePart file) throws IOException {
		
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
			
			/*if (file != null && !file.filename().isEmpty()) {
				File temporalFile = new File(file.filename());
				file.transferTo(temporalFile);
				product.setImage(Files.readAllBytes(temporalFile.toPath()));
			}*/
			
			Mono<Category> categoryMono = categoryService.findById(product.getCategory().getId());
			
			return file.content()
	            .map(dataBuffer -> {
	                byte[] bytes = new byte[dataBuffer.readableByteCount()];
	                dataBuffer.read(bytes);
	                DataBufferUtils.release(dataBuffer);
	                return bytes;
	            })
	            .reduce((bytes1, bytes2) -> {
	                byte[] combined = new byte[bytes1.length + bytes2.length];
	                System.arraycopy(bytes1, 0, combined, 0, bytes1.length);
	                System.arraycopy(bytes2, 0, combined, bytes1.length, bytes2.length);
	                return combined;
	            })
	            .flatMap(combinedBytes -> {
	                product.setImage(combinedBytes);
	                return categoryMono.flatMap(category -> {
	                	product.setCategory(category);
	                	return productService.save(product)
	                			.doOnNext(p -> {
	            					log.info("Product save with name: " + p.getName() 
	            					+ " and ID: " + product.getId() + " and Catgery: " + category.getName());
	                			});
	                	});
	            })
	            .thenReturn("redirect:/api/crud/products/to-list?success=The+product+has+been+saved+successfully");
		}

	}

	@GetMapping("/form/{id}")
	public Mono<String> findById(@PathVariable(name = "id") String productId, Model model) {

		return productService.findById(productId)
				.doOnNext(product -> log.info("Product found " + product.getName() + " with image: " + product.getImage() ))
				.defaultIfEmpty(new Product())
			.flatMap(product -> {
				model.addAttribute("title", "Update product");
				model.addAttribute("button", "Update");
				model.addAttribute("product", product);
				
				if (product.getImage() != null) {
					String imageBase64 = Base64.getEncoder().encodeToString(product.getImage());
					model.addAttribute("imageBase64", imageBase64);
				}
				
				return Mono.just("form");
			});
	}

	@GetMapping("/{id}")
	public Mono<String> deleteProduct(@PathVariable String id) {

		return productService.findById(id)
				// Provides a default product to ensure the flow continues correctly
				.defaultIfEmpty(new Product()).flatMap(p -> {
					if (p.getId() == null) {
						return Mono.error(new InterruptedException("The product with id " + id + " does not exist"));
					} else {
						return Mono.just(p);
					}
				}).flatMap(p -> {
					log.info("The product with id " + id + "will be deleted");
					return productService.delete(p);
				})
				.then(Mono
						.just("redirect:/api/crud/products/to-list?success=The+product+has+been+deleted+successfully"))
				.onErrorResume(ex -> {
					log.error(ex.getMessage());
					return Mono.just("redirect:/api/crud/products/to-list?error=The+product+does+not+exist");
				});
	}

	@ModelAttribute("categories")
	public Flux<Category> findAll() {
		return categoryService.findAll();
	}

}
