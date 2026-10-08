package com.zentia.app.finance.usecase;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.zentia.app.finance.model.Category;
import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.port.TransactionRepositoryPort;
import com.zentia.app.finance.services.CategoryService;
import com.zentia.app.identity.model.User;
import com.zentia.app.identity.services.UserService;

@Service 
public class RegistrarTransaccionUseCase {
    //Inyectar a los que validan que la informacion sea correcta
    private final UserService userService;
    private final CategoryService categoryService;

    //Inyectamos el enchufe/puerto para guardar la transaccion 
    //No se inyecta el repositorio directamente, sino que se inyecta la interfaz 
    //(puerto) que define la operación de guardar transacciones.
    private final TransactionRepositoryPort transactionRepositoryPort;
    //Constructor para la inyeccion 
    public RegistrarTransaccionUseCase(UserService userService, CategoryService categoryService, TransactionRepositoryPort transactionRepositoryPort) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }


    //El caso de Uso principal es registrar una transacción, que implica validar al usuario y la categoría,
    // y luego guardar la transacción en la base de datos.
    public Transaction ejecutar(Long userId, Long categoryId, BigDecimal amount, String type, String description, java.time.LocalDateTime transactionDate) {
       
        //Regla 1: monto valido obligatorio (amount > 0 and no puede ser nullo)
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            //.compareTo(BigDecimal.ZERO) = 
            //Si el monto es nulo o menor o igual a cero, se lanza una excepción indicando que el monto de la transacción debe ser mayor que cero y no puede ser nulo.
            throw new IllegalArgumentException("El monto de la transacción debe ser mayor que cero y no puede ser nulo.");
        }

        //Regla 2: El usuario debe de existir esto con el fin de que cada transaccion este relacionada con un usuario
        User user = userService.findUserById(userId);

        //Regla 3: La transaccion debe de tener una categoria exacta
        //Creamos la variable categoria que busca la categoria para asignarla a la transaccio 
        Category category = categoryService.findCategoryById(categoryId);

        //Regla 4: si todo esta en orden, creamos la entidad transaccion 
        Transaction nuevaTransaction = new Transaction(); 
        nuevaTransaction.setUser(user);
        nuevaTransaction.setCategory(category);
        nuevaTransaction.setAmount(amount);
        //Convertir el String que recibimos "GASTO/INGRESO" a nuestro Enum Estricto 
        nuevaTransaction.setType(com.zentia.app.finance.model.TransactionType.valueOf(type.toUpperCase()));
        nuevaTransaction.setDescription(description);
        nuevaTransaction.setTransactionDate(transactionDate);
        //Regla 5: le hablamos al puerto para que gaurde la transaccion 
        return transactionRepositoryPort.save(nuevaTransaction);
    }
    
}
