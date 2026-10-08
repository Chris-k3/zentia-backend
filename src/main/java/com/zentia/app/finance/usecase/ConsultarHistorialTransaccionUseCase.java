package com.zentia.app.finance.usecase;

import com.zentia.app.finance.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zentia.app.finance.model.Category;
import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.port.TransactionRepositoryPort;
import com.zentia.app.finance.services.CategoryService;
import com.zentia.app.identity.model.User;
import com.zentia.app.identity.services.UserService;

@Service 
public class ConsultarHistorialTransaccionUseCase {
    private final TransactionRepository transactionRepository;
    private final UserService userService; 
    private final CategoryService categoryService; 
    //Inyeccion del puente
    private final TransactionRepositoryPort transactionRepositoryPort;
    public ConsultarHistorialTransaccionUseCase(UserService userService, CategoryService categoryService,
            TransactionRepositoryPort transactionRepositoryPort, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.transactionRepository = transactionRepository;
    } 


    //Leer y devolver las transaccion de un usuario de x fecha a x fecha

    public List<Transaction> consultar(Long userId, Long categoryId, LocalDateTime starDate, LocalDateTime enDate){
        //Regla 1: Las transacciones estas enlazadas con el usuario 
        User user = userService.findUserById(userId);
        //Regla 2: Las transacciones debe de tener una categoria exacta 
        Category category = categoryService.findCategoryById(categoryId);

        //Regla 4: Si todo esta correcto 
        return transactionRepositoryPort.findByUserIdAndTransactionDateBetween(userId, starDate, enDate);
    }

    


}
