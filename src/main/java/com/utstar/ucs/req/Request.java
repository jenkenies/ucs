package com.utstar.ucs.req;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Request {
    @NotBlank(message = "userid不能为空")
    private String userid;
}
