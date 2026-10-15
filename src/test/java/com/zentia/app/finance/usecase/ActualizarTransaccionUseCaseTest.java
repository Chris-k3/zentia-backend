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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ActualizarTransaccionUseCaseTest {

    private InMemoryTransactionRepositoryAdapter repositoryAdapter;
    private CategoryService categoryService;
    private ActualizarTransaccionUseCase useCase;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new InMemoryTransactionRepositoryAdapter();
        categoryService = Mockito.mock(CategoryService.class);
        useCase = new ActualizarTransaccionUseCase(repositoryAdapter, categoryService);
    }

    @Test
    @DisplayName("Debe actualizar monto y descripción en el adaptador en memoria exitosamente")
    void debeActualizarTransaccionExitosamente() {
        // Arrange: Guardamos una transacción inicial en el adaptador en memoria
        User user = new User();
        user.setId(1L);

        Transaction original = new Transaction();
        original.setUser(user);
        original.setAmount(new BigDecimal("100.00"));
        original.setDescription("Descripción anterior");
        original.setType(TransactionType.GASTO);
        original.setTransactionDate(LocalDate.now());

        Transaction guardada = repositoryAdapter.save(original);
        Long transactionId = guardada.getId();

        BigDecimal nuevoMonto = new BigDecimal("250.00");
        String nuevaDescripcion = "Descripción actualizada";

        // Act
        Transaction resultado = useCase.ejecutar(
                1L,
                transactionId,
                nuevoMonto,
                null,
                nuevaDescripcion
        );

        // Assert
        assertThat(resultado.getAmount()).isEqualByComparingTo(nuevoMonto);
        assertThat(resultado.getDescription()).isEqualTo(nuevaDescripcion);

        // Validamos que en el repositorio en memoria se actualizó el registro
        Optional<Transaction> enMemoria = repositoryAdapter.findById(transactionId);
        assertThat(enMemoria).isPresent();
        assertThat(enMemoria.get().getAmount()).isEqualByComparingTo(nuevoMonto);
        assertThat(enMemoria.get().getDescription()).isEqualTo(nuevaDescripcion);
    }

    @Test
    @DisplayName("Debe lanzar SecurityException cuando el usuario no es el dueño de la transacción")
    void debeLanzarExcepcionCuandoUsuarioNoEsPropietario() {
        // Arrange
        User owner = new User();
        owner.setId(1L);

        Transaction original = new Transaction();
        original.setUser(owner);
        original.setAmount(new BigDecimal("50.00"));
        original.setDescription("Gasto personal");
        original.setType(TransactionType.GASTO);

        Transaction guardada = repositoryAdapter.save(original);

        Long attackerUserId = 2L;

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(
                attackerUserId,
                guardada.getId(),
                new BigDecimal("500.00"),
                null,
                "Intento de alteración"
        ))
        .isInstanceOf(SecurityException.class)
        .hasMessageContaining("No tienes permiso para modificar esta transacción.");
    }
}
