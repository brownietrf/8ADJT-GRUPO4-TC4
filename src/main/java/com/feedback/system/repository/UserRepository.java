package com.feedback.system.repository;

import com.feedback.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de banco de dados com User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Busca um usuário por email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email fornecido.
     */
    boolean existsByEmail(String email);
}
