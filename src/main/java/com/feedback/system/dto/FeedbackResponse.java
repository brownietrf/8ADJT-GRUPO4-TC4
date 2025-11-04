package com.feedback.system.dto;

import com.feedback.system.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de feedback.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private String id;
    private String studentName;
    private String studentEmail;
    private String course;
    private int rating;
    private String comment;
    private boolean urgent;
    private LocalDateTime createdAt;

    /**
     * Converte uma entidade Feedback para DTO.
     */
    public static FeedbackResponse fromEntity(Feedback feedback) {
        return new FeedbackResponse(
            feedback.getId(),
            feedback.getStudentName(),
            feedback.getStudentEmail(),
            feedback.getCourse(),
            feedback.getRating(),
            feedback.getComment(),
            feedback.isUrgent(),
            feedback.getCreatedAt()
        );
    }
}
