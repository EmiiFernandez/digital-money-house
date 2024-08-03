package com.dmh.account_service.dto;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDTO {
    private String alias;
    private Number available_amount;
    private String cvu;
    private Integer user_id;
}
