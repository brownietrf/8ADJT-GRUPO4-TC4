package com.feedback.system.service;

import com.feedback.system.dto.FeedbackResponse;
import com.feedback.system.model.Feedback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço para geração de relatórios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final FeedbackService feedbackService;

    /**
     * Gera relatório semanal com estatísticas de feedbacks.
     */
    public Map<String, Object> generateWeeklyReport() {
        log.info("Gerando relatório semanal");

        List<Feedback> weeklyFeedbacks = feedbackService.getLastWeekFeedbacks();

        Map<String, Object> report = new HashMap<>();
        report.put("reportGeneratedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        report.put("periodStart", LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ISO_DATE_TIME));
        report.put("periodEnd", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        // Estatísticas gerais
        report.put("totalFeedbacks", weeklyFeedbacks.size());
        report.put("urgentFeedbacks", weeklyFeedbacks.stream().filter(Feedback::isUrgent).count());

        // Média de avaliações
        double averageRating = weeklyFeedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
        report.put("averageRating", String.format("%.2f", averageRating));

        // Distribuição por nota
        Map<Integer, Long> ratingDistribution = weeklyFeedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting()));
        report.put("ratingDistribution", ratingDistribution);

        // Cursos mais avaliados
        Map<String, Long> courseDistribution = weeklyFeedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getCourse, Collectors.counting()));
        report.put("topCourses", courseDistribution);

        // Comentários mais recentes (últimos 5)
        List<Map<String, String>> recentComments = weeklyFeedbacks.stream()
                .limit(5)
                .map(f -> {
                    Map<String, String> comment = new HashMap<>();
                    comment.put("course", f.getCourse());
                    comment.put("rating", String.valueOf(f.getRating()));
                    comment.put("comment", f.getComment());
                    comment.put("date", f.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
                    return comment;
                })
                .collect(Collectors.toList());
        report.put("recentComments", recentComments);

        // Alertas (feedbacks com nota 1 ou 2)
        long criticalFeedbacks = weeklyFeedbacks.stream()
                .filter(f -> f.getRating() <= 2)
                .count();
        report.put("criticalFeedbacks", criticalFeedbacks);

        log.info("Relatório semanal gerado com sucesso. Total de feedbacks: {}", weeklyFeedbacks.size());

        return report;
    }

    /**
     * Gera relatório completo (todos os feedbacks).
     */
    public Map<String, Object> generateFullReport() {
        log.info("Gerando relatório completo");

        List<FeedbackResponse> allFeedbacks = feedbackService.getAllFeedbacks();

        Map<String, Object> report = new HashMap<>();
        report.put("reportGeneratedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        report.put("totalFeedbacks", allFeedbacks.size());
        report.put("overallAverageRating", String.format("%.2f", feedbackService.calculateOverallAverageRating()));
        report.put("totalUrgentFeedbacks", feedbackService.countUrgentFeedbacks());

        log.info("Relatório completo gerado com sucesso");

        return report;
    }

    /**
     * Gera resumo formatado em texto do relatório semanal.
     */
    public String generateWeeklyReportText() {
        Map<String, Object> report = generateWeeklyReport();

        StringBuilder text = new StringBuilder();
        text.append("═══════════════════════════════════════════════════\n");
        text.append("      RELATÓRIO SEMANAL DE FEEDBACKS\n");
        text.append("═══════════════════════════════════════════════════\n\n");
        text.append("📅 Período: ").append(report.get("periodStart")).append(" até ").append(report.get("periodEnd")).append("\n");
        text.append("🕐 Gerado em: ").append(report.get("reportGeneratedAt")).append("\n\n");

        text.append("📊 ESTATÍSTICAS GERAIS\n");
        text.append("─────────────────────────────────────────────────\n");
        text.append("Total de Feedbacks: ").append(report.get("totalFeedbacks")).append("\n");
        text.append("Feedbacks Urgentes: ").append(report.get("urgentFeedbacks")).append("\n");
        text.append("Feedbacks Críticos (nota ≤ 2): ").append(report.get("criticalFeedbacks")).append("\n");
        text.append("Média Geral: ").append(report.get("averageRating")).append(" ⭐\n\n");

        text.append("📈 DISTRIBUIÇÃO DE NOTAS\n");
        text.append("─────────────────────────────────────────────────\n");
        @SuppressWarnings("unchecked")
        Map<Integer, Long> distribution = (Map<Integer, Long>) report.get("ratingDistribution");
        for (int i = 5; i >= 1; i--) {
            long count = distribution.getOrDefault(i, 0L);
            text.append("⭐ ").append(i).append(" estrelas: ").append(count).append("\n");
        }

        text.append("\n═══════════════════════════════════════════════════\n");

        return text.toString();
    }
}
