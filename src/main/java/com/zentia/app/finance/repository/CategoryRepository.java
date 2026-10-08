package com.zentia.app.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zentia.app.finance.model.Category;
import com.zentia.app.finance.model.TransactionType;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByType(TransactionType type); //SELECT * FROM category WHERE type = ?
    //List = una colección que puede contener múltiples elementos, en este caso, 
    // categorías asociadas a un usuario específico.
}
