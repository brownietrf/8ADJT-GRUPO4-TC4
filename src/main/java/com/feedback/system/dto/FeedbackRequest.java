package com.feedback.system.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de feedback.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    @NotBlank(message = "Nome do aluno é obrigatório")
    private String studentName;

    @NotBlank(message = "Email do aluno é obrigatório")
    @Email(message = "Email inválido")
    private String studentEmail;

    @NotBlank(message = "Nome do curso é obrigatório")
    private String course;

    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    private int rating;

    @NotBlank(message = "Comentário é obrigatório")
    @Size(max = 2000, message = "Comentário deve ter no máximo 2000 caracteres")
    private String comment;

    private boolean urgent;
}
