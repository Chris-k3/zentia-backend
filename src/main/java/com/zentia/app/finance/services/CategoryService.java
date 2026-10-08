package com.zentia.app.finance.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zentia.app.finance.model.Category;
import com.zentia.app.finance.model.TransactionType;
import com.zentia.app.finance.repository.CategoryRepository;

@Service 
public class CategoryService {
    //Mandar a llamar al repositorio de categorias para realizar operaciones relacionadas con las categorias.
    private final CategoryRepository categoryRepository;
    //Inyección de dependencias a través del constructor
    public CategoryService(CategoryRepository categoryRepository) {this.categoryRepository = categoryRepository;}


    //Metodo interno para uso de transacciones, para validar que la categoria exista antes de realizar una transaccion

    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            //Si la categoria no se encuentra, lanzamos una excepción con un mensaje de error
            //orElseThrow() es un método que se utiliza para manejar el caso en el que un valor no está presente en un Optional.
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada con ID: " + categoryId));
    }

    //Metodo externo para uso de controller

    public List<Category> getCategoriesByType(String typeString) {
        //Convertimos el string a enum para poder buscar las categorias por tipo
        TransactionType type = TransactionType.valueOf(typeString.toUpperCase());
        return categoryRepository.findByType(type);
    } 

    //Metodo externo para mostrar todas las categorias, sin importar el tipo, para uso de controller
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

}
