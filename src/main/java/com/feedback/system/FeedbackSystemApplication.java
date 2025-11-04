package com.feedback.system;

import com.feedback.system.model.User;
import com.feedback.system.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Classe principal da aplicação Spring Boot.
 * Sistema de Gerenciamento de Feedbacks com Arquitetura Serverless.
 */
@SpringBootApplication
@Slf4j
public class FeedbackSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedbackSystemApplication.class, args);
    }

    /**
     * Inicializa dados mock para testes.
     * Cria usuários de exemplo (aluno e administrador).
     */
    @Bean
    CommandLineRunner initData(AuthService authService) {
        return args -> {
            log.info("═══════════════════════════════════════════════════");
            log.info("   SISTEMA DE FEEDBACKS - INICIANDO");
            log.info("═══════════════════════════════════════════════════");

            try {
                // Criar usuário ADMIN
                authService.registerUser(
                        "admin@feedback.com",
                        "admin123",
                        "Administrador do Sistema",
                        User.Role.ROLE_ADMIN
                );
                log.info("✓ Usuário ADMIN criado: admin@feedback.com / admin123");

                // Criar usuário STUDENT 1
                authService.registerUser(
                        "maria.silva@aluno.com",
                        "maria123",
                        "Maria Silva",
                        User.Role.ROLE_STUDENT
                );
                log.info("✓ Usuário STUDENT criado: maria.silva@aluno.com / maria123");

                // Criar usuário STUDENT 2
                authService.registerUser(
                        "joao.santos@aluno.com",
                        "joao123",
                        "João Santos",
                        User.Role.ROLE_STUDENT
                );
                log.info("✓ Usuário STUDENT criado: joao.santos@aluno.com / joao123");

            } catch (Exception e) {
                log.warn("Usuários já existem no banco de dados");
            }

            log.info("═══════════════════════════════════════════════════");
            log.info("   SISTEMA PRONTO! Acesse: http://localhost:8080");
            log.info("═══════════════════════════════════════════════════");
        };
    }
}
