package com.example.springChat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @NotBlank
    private String roomCode;

    @ManyToOne
    @JoinColumn(name="userId")
    private Users owner;

    @NotBlank
    private String title;

    private String description;

    private boolean isDeleted;

    private boolean isPrivate;

    @CreatedDate
    private LocalDateTime createdAt;
}
