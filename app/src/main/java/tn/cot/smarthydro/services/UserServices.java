package tn.cot.smarthydro.services;

import jakarta.ejb.EJBException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import tn.cot.smarthydro.entities.User;
import tn.cot.smarthydro.enums.Role;
import tn.cot.smarthydro.repositories.UserRepository;
import tn.cot.smarthydro.utils.Argon2Utils;

import java.time.LocalDateTime;
import java.util.Collections;

@ApplicationScoped
public class UserServices {

    @Inject
    UserRepository userRepository;
    @Inject
    Argon2Utils argon2Utils;

    public void registerUser(@Valid User user) {

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new EJBException("User with username " + user.getEmail() + " already exists");
        }
        user.setCreationDate(LocalDateTime.now().toLocalDate().toString());
        user.setRoles(Collections.singleton(Role.USER));
        user.hashPassword(user.getPassword(), argon2Utils);
        userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        final User user = userRepository.findByEmail(email).orElseThrow(() -> new EJBException("User not found"));
        if(user != null && argon2Utils.check(user.getPassword(), password.toCharArray())){
            return user;
        }
        throw new EJBException("Failed log in with email: " + email + " [Unknown email or wrong password]");
    }
}







