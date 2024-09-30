package com.example.inventory02;

import com.example.inventory02.model.Product;
import com.example.inventory02.repository.ProductRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class ProductView extends VerticalLayout {

    private final ProductRepository productRepository;
    private Grid<Product> grid = new Grid<>(Product.class);
    private TextField name = new TextField("Product Name");
    private TextField category = new TextField("Category");
    private TextField price = new TextField("Price");
    private TextField quantity = new TextField("Quantity");

    private Product selectedProduct;  // Armazena o produto selecionado para edição

    @Autowired
    public ProductView(ProductRepository productRepository) {
        this.productRepository = productRepository;

        grid.setColumns("id", "name", "category", "price", "quantity");

        Button addButton = new Button("Add Product", event -> addProduct());
        Button deleteButton = new Button("Delete Product", event -> deleteProduct());
        Button editButton = new Button("Edit Product", event -> editProduct());

        HorizontalLayout form = new HorizontalLayout(name, category, price, quantity, addButton, editButton, deleteButton);
        add(grid, form);

        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue())); // Preenche o formulário com o produto selecionado

        updateGrid();
    }

    private void addProduct() {
        if (selectedProduct == null) {
            // Adiciona um novo produto
            Product product = new Product();
            product.setName(name.getValue());
            product.setCategory(category.getValue());
            product.setPrice(Double.parseDouble(price.getValue()));
            product.setQuantity(Integer.parseInt(quantity.getValue()));

            productRepository.save(product);
            updateGrid();
            Notification.show("Product added successfully.");
        } else {
            // Edita o produto selecionado
            selectedProduct.setName(name.getValue());
            selectedProduct.setCategory(category.getValue());
            selectedProduct.setPrice(Double.parseDouble(price.getValue()));
            selectedProduct.setQuantity(Integer.parseInt(quantity.getValue()));

            productRepository.save(selectedProduct);
            updateGrid();
            Notification.show("Product updated successfully.");
            clearForm();
        }
    }

    private void deleteProduct() {
        Product selectedProduct = grid.asSingleSelect().getValue();
        if (selectedProduct != null) {
            productRepository.delete(selectedProduct);
            updateGrid();
            Notification.show("Product deleted successfully.");
            clearForm();
        } else {
            Notification.show("Select a product to delete.");
        }
    }

    private void editProduct() {
        if (selectedProduct != null) {
            // Edita o produto selecionado
            selectedProduct.setName(name.getValue());
            selectedProduct.setCategory(category.getValue());
            selectedProduct.setPrice(Double.parseDouble(price.getValue()));
            selectedProduct.setQuantity(Integer.parseInt(quantity.getValue()));

            productRepository.save(selectedProduct);
            updateGrid();
            Notification.show("Product updated successfully.");
            clearForm();
        } else {
            Notification.show("Select a product to edit.");
        }
    }

    private void updateGrid() {
        grid.setItems(productRepository.findAll());
    }

    private void populateForm(Product product) {
        if (product != null) {
            selectedProduct = product;
            name.setValue(product.getName());
            category.setValue(product.getCategory());
            price.setValue(String.valueOf(product.getPrice()));
            quantity.setValue(String.valueOf(product.getQuantity()));
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        selectedProduct = null;
        name.clear();
        category.clear();
        price.clear();
        quantity.clear();
    }
}
