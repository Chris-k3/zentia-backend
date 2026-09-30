package com.zentia.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.zentia.app.identity.model.User;
import com.zentia.app.identity.repository.UserRepository;

@SpringBootApplication
public class ZentiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZentiaApplication.class, args);
    }

    @Bean 
    public CommandLineRunner probarRepositorio(UserRepository userRepository) {
        return args -> {
            // Aquí puedes probar el repositorio de usuarios
            // Por ejemplo, crear un nuevo usuario y guardarlo en la base de datos
            System.out.println("Probando el repositorio de usuarios...");
            User testUser = new User(
                "Christiam@zentia.com",
                "googleId123",
                "Christian Gonzalez", 
                "https://example.com/profile.jpg"

            );
            User UsuarioGuardado = userRepository.save(testUser);
            System.out.println("Usuario guardado: " + UsuarioGuardado.getEmail());

            userRepository.findByEmail("Christiam@zentia.com").ifPresent(usuarioEncontrado -> {
                System.out.println("Usuario encontrado por email: " + usuarioEncontrado.getEmail());
            });
            
        };

}
}