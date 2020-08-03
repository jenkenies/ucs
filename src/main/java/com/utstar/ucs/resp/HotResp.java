package com.utstar.ucs.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class HotResp {
    private String type;
    private String mc;
    private String currentnum;//第几集
    @JsonIgnore
    private String mode;
}
