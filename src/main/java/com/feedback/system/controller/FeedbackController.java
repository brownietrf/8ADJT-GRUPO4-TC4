package com.feedback.system.controller;

import com.feedback.system.dto.FeedbackRequest;
import com.feedback.system.dto.FeedbackResponse;
import com.feedback.system.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações de feedback de alunos.
 * Acessível por ROLE_STUDENT.
 */
@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * Criar novo feedback.
     * POST /api/feedbacks
     * Acesso: STUDENT ou ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<FeedbackResponse> createFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication authentication
    ) {
        log.info("Criando feedback - Usuário: {}", authentication.getName());
        FeedbackResponse response = feedbackService.createFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar feedbacks do aluno logado.
     * GET /api/feedbacks/me
     * Acesso: STUDENT
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedbacks(Authentication authentication) {
        String email = authentication.getName();
        log.info("Buscando feedbacks do aluno: {}", email);
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByStudent(email);
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * Buscar feedback por ID.
     * GET /api/feedbacks/{id}
     * Acesso: STUDENT ou ADMIN
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable String id) {
        log.info("Buscando feedback ID: {}", id);
        FeedbackResponse feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }
}
