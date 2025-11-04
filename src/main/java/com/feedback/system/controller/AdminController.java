package com.feedback.system.controller;

import com.feedback.system.dto.FeedbackResponse;
import com.feedback.system.service.FeedbackService;
import com.feedback.system.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para operações administrativas.
 * Acessível apenas por ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FeedbackService feedbackService;
    private final ReportService reportService;

    /**
     * Listar todos os feedbacks.
     * GET /api/admin/feedbacks
     * Acesso: ADMIN
     */
    @GetMapping("/feedbacks")
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks(
            @RequestParam(required = false) Boolean lastWeek
    ) {
        log.info("Admin solicitando todos os feedbacks. LastWeek: {}", lastWeek);

        if (Boolean.TRUE.equals(lastWeek)) {
            // Retorna feedbacks da última semana
            List<FeedbackResponse> feedbacks = feedbackService.getLastWeekFeedbacks()
                    .stream()
                    .map(FeedbackResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(feedbacks);
        }

        List<FeedbackResponse> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * Listar feedbacks urgentes.
     * GET /api/admin/feedbacks/urgent
     * Acesso: ADMIN
     */
    @GetMapping("/feedbacks/urgent")
    public ResponseEntity<List<FeedbackResponse>> getUrgentFeedbacks() {
        log.info("Admin solicitando feedbacks urgentes");
        List<FeedbackResponse> feedbacks = feedbackService.getUrgentFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * Gerar relatório semanal manualmente.
     * POST /api/admin/report/weekly
     * Acesso: ADMIN
     */
    @PostMapping("/report/weekly")
    public ResponseEntity<Map<String, Object>> generateWeeklyReport() {
        log.info("Admin solicitando geração de relatório semanal");
        Map<String, Object> report = reportService.generateWeeklyReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Gerar relatório semanal em formato texto.
     * GET /api/admin/report/weekly/text
     * Acesso: ADMIN
     */
    @GetMapping("/report/weekly/text")
    public ResponseEntity<String> generateWeeklyReportText() {
        log.info("Admin solicitando relatório semanal em texto");
        String report = reportService.generateWeeklyReportText();
        return ResponseEntity.ok(report);
    }

    /**
     * Gerar relatório completo.
     * POST /api/admin/report/full
     * Acesso: ADMIN
     */
    @PostMapping("/report/full")
    public ResponseEntity<Map<String, Object>> generateFullReport() {
        log.info("Admin solicitando relatório completo");
        Map<String, Object> report = reportService.generateFullReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Estatísticas gerais.
     * GET /api/admin/stats
     * Acesso: ADMIN
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("Admin solicitando estatísticas");

        Map<String, Object> stats = Map.of(
                "totalFeedbacks", feedbackService.getAllFeedbacks().size(),
                "urgentFeedbacks", feedbackService.countUrgentFeedbacks(),
                "averageRating", feedbackService.calculateOverallAverageRating()
        );

        return ResponseEntity.ok(stats);
    }
}
