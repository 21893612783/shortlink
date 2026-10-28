package com.guanyu.shortlink.stat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessMessage {

    private String code;

    private String ip;

    private Long timestamp;
}