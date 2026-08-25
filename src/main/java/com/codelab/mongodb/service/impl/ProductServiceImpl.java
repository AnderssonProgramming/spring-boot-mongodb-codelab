package com.codelab.mongodb.service.impl;

import com.codelab.mongodb.exception.ResourceNotFoundException;
import com.codelab.mongodb.model.Product;
import com.codelab.mongodb.repository.ProductRepository;
import com.codelab.mongodb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public Product update(String id, Product updatedData) {
        Product existingProduct = findById(id);

        existingProduct.setName(updatedData.getName());
        existingProduct.setDescription(updatedData.getDescription());
        existingProduct.setPrice(updatedData.getPrice());
        existingProduct.setStockQuantity(updatedData.getStockQuantity());

        return productRepository.save(existingProduct);
    }

    @Override
    public void delete(String id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}
