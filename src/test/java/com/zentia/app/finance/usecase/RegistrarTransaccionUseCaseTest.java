package com.zentia.app.finance.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.zentia.app.finance.adapter.InMemoryTransactionRepositoryAdapter;
import com.zentia.app.finance.model.Category;
import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.model.TransactionType;
import com.zentia.app.finance.services.CategoryService;
import com.zentia.app.identity.model.User;
import com.zentia.app.identity.services.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class RegistrarTransaccionUseCaseTest {

    private InMemoryTransactionRepositoryAdapter repositoryAdapter;
    private UserService userService;
    private CategoryService categoryService;
    private RegistrarTransaccionUseCase useCase;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new InMemoryTransactionRepositoryAdapter();
        userService = Mockito.mock(UserService.class);
        categoryService = Mockito.mock(CategoryService.class);
        useCase = new RegistrarTransaccionUseCase(userService, categoryService, repositoryAdapter);
    }

    @Test
    @DisplayName("Debe registrar y persistir una transacción exitosamente en el adaptador en memoria")
    void debeRegistrarTransaccionExitosamente() {
        // Arrange
        Long userId = 1L;
        Long categoryId = 10L;

        User user = new User();
        user.setId(userId);
        user.setName("Usuario Test");

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Comida");
        category.setType(TransactionType.GASTO);

        when(userService.findUserById(userId)).thenReturn(user);
        when(categoryService.findCategoryById(categoryId)).thenReturn(category);

        BigDecimal amount = new BigDecimal("150.50");
        LocalDate date = LocalDate.now();

        // Act
        Transaction resultado = useCase.ejecutar(
                userId,
                categoryId,
                amount,
                "GASTO",
                "Almuerzo de trabajo",
                date
        );

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getAmount()).isEqualByComparingTo(amount);
        assertThat(resultado.getType()).isEqualTo(TransactionType.GASTO);
        assertThat(resultado.getUser().getId()).isEqualTo(userId);
        assertThat(resultado.getCategory().getId()).isEqualTo(categoryId);

        // Verificamos que realmente se persistió en el adaptador en memoria sin tocar base de datos
        Optional<Transaction> enMemoria = repositoryAdapter.findById(resultado.getId());
        assertThat(enMemoria).isPresent();
        assertThat(enMemoria.get().getDescription()).isEqualTo("Almuerzo de trabajo");
        assertThat(repositoryAdapter.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción y no guardar nada en memoria cuando el monto es menor o igual a cero")
    void debeLanzarExcepcionCuandoMontoEsInvalido() {
        // Arrange
        Long userId = 1L;
        Long categoryId = 10L;
        BigDecimal montoInvalido = BigDecimal.ZERO;

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(
                userId,
                categoryId,
                montoInvalido,
                "GASTO",
                "Intento inválido",
                LocalDate.now()
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El monto de la transacción debe ser mayor que cero y no puede ser nulo.");

        // Verificamos que el adaptador en memoria permanezca vacío
        assertThat(repositoryAdapter.count()).isZero();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el monto es nulo y no persistir en memoria")
    void debeLanzarExcepcionCuandoMontoEsNulo() {
        // Arrange
        Long userId = 1L;
        Long categoryId = 10L;

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(
                userId,
                categoryId,
                null,
                "INGRESO",
                "Monto nulo",
                LocalDate.now()
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El monto de la transacción debe ser mayor que cero y no puede ser nulo.");

        assertThat(repositoryAdapter.count()).isZero();
    }
}
