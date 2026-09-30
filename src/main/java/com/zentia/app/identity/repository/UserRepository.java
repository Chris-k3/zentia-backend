package com.zentia.app.identity.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.zentia.app.identity.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); //SELECT * FROM user WHERE email = ?
    Optional<User> findByGoogleId(String googleId); //SELECT * FROM user WHERE google_id = ?

    //Optional = un valor que puede estar presente o no, es decir, puede contener un valor o ser nulo.
}
