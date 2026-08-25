package com.codelab.mongodb.service;

import com.codelab.mongodb.model.Product;

import java.util.List;

public interface ProductService {

    Product create(Product product);

    List<Product> findAll();

    Product findById(String id);

    List<Product> searchByName(String name);

    Product update(String id, Product product);

    void delete(String id);
}
