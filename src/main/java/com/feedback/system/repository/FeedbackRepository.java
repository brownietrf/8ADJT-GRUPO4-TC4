package com.feedback.system.repository;

import com.feedback.system.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de banco de dados com Feedback.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    /**
     * Busca todos os feedbacks marcados como urgentes.
     */
    List<Feedback> findByUrgentTrue();

    /**
     * Busca feedbacks criados entre duas datas.
     */
    List<Feedback> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Busca feedbacks por email do aluno.
     */
    List<Feedback> findByStudentEmailOrderByCreatedAtDesc(String studentEmail);

    /**
     * Busca feedbacks por curso.
     */
    List<Feedback> findByCourseOrderByCreatedAtDesc(String course);

    /**
     * Calcula a média de avaliações de um curso.
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.course = :course")
    Double calculateAverageRatingByCourse(String course);

    /**
     * Calcula a média geral de todas as avaliações.
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double calculateOverallAverageRating();

    /**
     * Conta quantos feedbacks urgentes existem.
     */
    long countByUrgentTrue();

    /**
     * Busca os feedbacks mais recentes (últimos N).
     */
    List<Feedback> findTop10ByOrderByCreatedAtDesc();
}
