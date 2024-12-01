package com.dmh.transaction_service.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

//@Entity
@Getter
@Setter
@NoArgsConstructor
//@Table(name = "transactions")
public class Transaction {
  //  @Id
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //  @Setter(AccessLevel.NONE)
    private Integer id;
    private Integer account_id;
    private BigDecimal amount;
    private String dated;
    private String description;
    private String destination;
    private String origin;
    private String type;

}
