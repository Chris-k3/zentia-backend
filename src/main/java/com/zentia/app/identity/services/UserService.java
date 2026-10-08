package com.zentia.app.identity.services;

import org.springframework.stereotype.Service;

import com.zentia.app.identity.dto.UserResponseDTO;
import com.zentia.app.identity.model.User;
import com.zentia.app.identity.repository.UserRepository;

@Service //spring lo cargue al iniciar el programa
public class UserService {
    //Mandar a llamar al repositorio de usuarios para realizar operaciones relacionadas con los usuarios.
    private final UserRepository userRepository;
         
    //Inyección de dependencias a través del constructor
    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}

    //Metodo para uso interno, lo usara trsacciones para validar que el usuario exista antes de realizar una transaccion
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
            //Si el usuario no se encuentra, lanzamos una excepción con un mensaje de error
            //orElseThrow() es un método que se utiliza para manejar el caso en el que un valor no está presente en un Optional.
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
    }

    //Metodo para uso externo, lo utilizara controller para exponerlo en la API, para que el front pueda validar si el usuario existe antes de realizar una transaccion
    public UserResponseDTO getUserById(Long userId) {
        User user = findUserById(userId);
        //Convertimos el objeto User a UserResponseDTO para no exponer datos sensibles al front
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPictureUrl(), user.isOnboardingCompleted());
    }

    
}
