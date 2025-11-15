package org.ostad.assignment_14.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ostad.assignment_14.entity.Product;
import org.ostad.assignment_14.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        log.debug("Received request to create product: {}", product);
        Product createdProduct = productService.createProduct(product);
        log.info("Product created successfully with ID: {}", createdProduct.getId());
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.debug("Received request to fetch all products");
        List<Product> products = productService.getAllProducts();
        log.info("Retrieved {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.debug("Received request to fetch product with ID: {}", id);
        Product product = productService.getProductById(id);
        log.info("Product retrieved successfully with ID: {}", id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        log.debug("Received request to update product with ID: {}", id);
        Product updatedProduct = productService.updateProduct(id, product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("Received request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        log.info("Product deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}