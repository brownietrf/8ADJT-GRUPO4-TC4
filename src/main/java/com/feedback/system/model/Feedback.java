package com.feedback.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um feedback de aluno.
 * Armazena informações sobre avaliações de cursos e comentários.
 */
@Entity
@Table(name = "feedbacks", indexes = {
    @Index(name = "idx_urgent", columnList = "urgent"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_student_email", columnList = "studentEmail")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Nome do aluno é obrigatório")
    @Column(nullable = false)
    private String studentName;

    @NotBlank(message = "Email do aluno é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false)
    private String studentEmail;

    @NotBlank(message = "Nome do curso é obrigatório")
    @Column(nullable = false)
    private String course;

    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    @Column(nullable = false)
    private int rating;

    @NotBlank(message = "Comentário é obrigatório")
    @Column(length = 2000, nullable = false)
    private String comment;

    @Column(nullable = false)
    private boolean urgent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Define automaticamente a data de criação antes de persistir.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Atualiza a data de modificação antes de atualizar.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
