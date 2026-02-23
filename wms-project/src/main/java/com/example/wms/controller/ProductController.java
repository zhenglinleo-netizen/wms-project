package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Product;
import com.example.wms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return Result.success(products);
    }

    @PostMapping("/search")
    public Result<List<Product>> searchProducts(@RequestBody Product product) {
        List<Product> products = productService.getProductsByCondition(product);
        return Result.success(products);
    }

    @GetMapping("/deleted")
    public Result<List<Product>> getDeletedProducts() {
        List<Product> products = productService.getDeletedProducts();
        return Result.success(products);
    }

    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    @PostMapping
    public Result<Product> saveProduct(@RequestBody Product product) {
        productService.saveProduct(product);
        return Result.success("添加成功", product);
    }

    @PutMapping
    public Result<String> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/restore/{id}")
    public Result<String> restoreProduct(@PathVariable Long id) {
        productService.restoreProduct(id);
        return Result.success("恢复成功", null);
    }

    @DeleteMapping("/permanent/{id}")
    public Result<String> permanentlyDeleteProduct(@PathVariable Long id) {
        productService.permanentlyDeleteProduct(id);
        return Result.success("永久删除成功", null);
    }
}




