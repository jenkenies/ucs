package com.utstar.ucs.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Favorite {
    @NotBlank(message = "userid不能为空")
    private String userid;
    @NotBlank(message = "type不能为空")
    private String type;
    @NotBlank(message = "mc不能为空")
    private String mc;
    @NotBlank(message = "mode不能为空")
    private String mode;
    @JsonIgnore
    private String createtime;
    @JsonIgnore
    private String st;

}
