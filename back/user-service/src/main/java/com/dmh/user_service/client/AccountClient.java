package com.dmh.user_service.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountClient {
    private Integer id;
    private String alias;
    private Number available_amount;
    private String cvu;
    private Integer user_id;

}

