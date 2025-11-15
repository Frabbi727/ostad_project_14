package org.ostad.assignment_14.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ostad.assignment_14.entity.Product;
import org.ostad.assignment_14.exception.InvalidSkuFormatException;
import org.ostad.assignment_14.exception.ProductNotFoundException;
import org.ostad.assignment_14.exception.SkuAlreadyExistsException;
import org.ostad.assignment_14.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private static final Pattern SKU_PATTERN = Pattern.compile("^SKU-[A-Za-z0-9]{8}$");

    public Product createProduct(Product product) {
        log.debug("Attempting to create product with SKU: {}", product.getSku());

        validateSkuFormat(product.getSku());

        if (productRepository.existsBySku(product.getSku())) {
            log.warn("SKU already exists: {}", product.getSku());
            throw new SkuAlreadyExistsException("Product with SKU '" + product.getSku() + "' already exists");
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {} and SKU: {}", savedProduct.getId(), savedProduct.getSku());
        return savedProduct;
    }

    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.info("Retrieved {} products", products.size());
        return products;
    }

    public Product getProductById(Long id) {
        log.debug("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Failed to find product with ID: {}", id);
                    return new ProductNotFoundException("Product with ID " + id + " not found");
                });
        log.info("Product found with ID: {}", id);
        return product;
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        log.debug("Attempting to update product with ID: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Failed to find product with ID: {}", id);
                    return new ProductNotFoundException("Product with ID " + id + " not found");
                });

        // Check if SKU is being changed
        if (!existingProduct.getSku().equals(updatedProduct.getSku())) {
            log.warn("Attempt to change SKU from {} to {} for product ID: {}",
                    existingProduct.getSku(), updatedProduct.getSku(), id);
            throw new InvalidSkuFormatException("SKU of an existing product cannot be changed");
        }

        validateSkuFormat(updatedProduct.getSku());

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setStatus(updatedProduct.getStatus());

        Product savedProduct = productRepository.save(existingProduct);
        log.info("Product updated with ID: {} and SKU: {}", savedProduct.getId(), savedProduct.getSku());
        return savedProduct;
    }

    public void deleteProduct(Long id) {
        log.debug("Attempting to delete product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            log.warn("Failed to find product with ID: {}", id);
            throw new ProductNotFoundException("Product with ID " + id + " not found");
        }

        productRepository.deleteById(id);
        log.info("Product deleted with ID: {}", id);
    }

    private void validateSkuFormat(String sku) {
        if (!SKU_PATTERN.matcher(sku).matches()) {
            log.warn("Invalid SKU format: {}", sku);
            throw new InvalidSkuFormatException(
                    "Invalid SKU format. SKU must start with 'SKU-' followed by 8 alphanumeric characters (e.g., SKU-A1B2C3D4)"
            );
        }
    }
}