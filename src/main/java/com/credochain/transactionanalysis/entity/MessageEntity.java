package com.credochain.transactionanalysis.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "messages", indexes = @Index(columnList = "phone"))
public class MessageEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String message_id;
    @NonNull
    private Long phone;
    @NonNull
    private LocalDateTime date;
    @NonNull
    @Column(columnDefinition = "LONGTEXT")
    private String message;
}
