package com.example.springChat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="join_chat_room",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"chat_room_id", "user_id"})
        }
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class JoinChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @LastModifiedDate
    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(name = "last_message_read_id")
    private Messages lastMessageRead;
}
