package com.dmh.account_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;
    private String alias;
    private BigDecimal available_amount;
    private String cvu;
    private Integer user_id;

    @PrePersist
    protected void onCreate() {
        this.available_amount = BigDecimal.valueOf(0.00);
    }
}

