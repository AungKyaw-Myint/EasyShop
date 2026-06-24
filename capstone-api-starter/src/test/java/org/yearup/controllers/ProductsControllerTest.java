package org.yearup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.yearup.models.Product;
import org.yearup.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductsController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductsControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private Product createProduct()
    {
        Product product = new Product();
        product.setProductId(1);
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setDescription("Gaming Laptop");

        return product;
    }

    @Test
    void search_shouldReturnProducts() throws Exception
    {
        Product product = createProduct();

        when(productService.search(any(), any(), any(), any()))
                .thenReturn(List.of(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        verify(productService).search(null, null, null, null);
    }

    @Test
    void getById_shouldReturnProduct() throws Exception
    {
        Product product = createProduct();

        when(productService.getById(1))
                .thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService).getById(1);
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception
    {
        when(productService.getById(1))
                .thenReturn(null);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isNotFound());

        verify(productService).getById(1);
    }


    @Test
    void addProduct_ShouldReturnCreatedProduct() throws Exception
    {
        Product product = new Product();
        product.setProductId(1);
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setFeatured(true);
        product.setStock(10);
        product.setCategoryId(1);

        when(productService.create(any(Product.class)))
                .thenReturn(product);

        String json = objectMapper.writeValueAsString(product);
        System.out.println("JSON = " + json);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                });
    }

    @Test
//    @WithMockUser(roles = "ADMIN")
    void deleteProduct_shouldDeleteProduct() throws Exception
    {
        Product product = createProduct();

        when(productService.getById(1))
                .thenReturn(product);

        doNothing().when(productService).delete(1);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).delete(1);
    }

    @Test
//    @WithMockUser(roles = "ADMIN")
    void deleteProduct_shouldReturn404WhenProductNotFound() throws Exception
    {
        when(productService.getById(1))
                .thenReturn(null);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNotFound());

        verify(productService, never()).delete(anyInt());
    }

}