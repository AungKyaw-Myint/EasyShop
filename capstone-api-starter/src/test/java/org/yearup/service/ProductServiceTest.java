package org.yearup.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest
{
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp()
    {
        product = new Product();
        product.setProductId(1);
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setCategoryId(1);
        product.setDescription("Gaming Laptop");
        product.setSubCategory("Electronics");
        product.setFeatured(true);
        product.setImageUrl("image.jpg");
        product.setStock(10);
    }

    @Test
    void getById_ShouldReturnProduct()
    {
        when(productRepository.findById(1))
                .thenReturn(Optional.of(product));

        Product result = productService.getById(1);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());

        verify(productRepository).findById(1);
    }

    @Test
    void create_ShouldSaveAndReturnProduct()
    {
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        Product result = productService.create(product);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_ShouldModifyAndSaveProduct()
    {
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setPrice(1200.00);
        updatedProduct.setCategoryId(2);
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setSubCategory("Computers");
        updatedProduct.setFeatured(false);
        updatedProduct.setImageUrl("updated.jpg");
        updatedProduct.setStock(5);

        when(productRepository.findById(1))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.update(1, updatedProduct);

        assertEquals("Updated Laptop", result.getName());
        assertEquals(1200.00, result.getPrice());
        assertEquals(2, result.getCategoryId());
        assertEquals(5, result.getStock());

        verify(productRepository).findById(1);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void delete_ShouldCallRepositoryDelete()
    {
        productService.delete(1);

        verify(productRepository).deleteById(1);
    }

    @Test
    void listByCategoryId_ShouldReturnProducts()
    {
        when(productRepository.findByCategoryId(1))
                .thenReturn(List.of(product));

        List<Product> products = productService.listByCategoryId(1);

        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());

        verify(productRepository).findByCategoryId(1);
    }

    @Test
    void search_ShouldFilterProductsByPriceAndSubCategory()
    {
        Product p1 = new Product();
        p1.setProductId(1);
        p1.setPrice(100.0);
        p1.setSubCategory("Electronics");

        Product p2 = new Product();
        p2.setProductId(2);
        p2.setPrice(500.0);
        p2.setSubCategory("Furniture");

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(p1, p2));

        List<Product> results =
                productService.search(null, 50.0, 200.0, "Electronics");

        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getProductId());

        verify(productRepository).findAll();
    }
}