package com.utstar.ucs.resp;

import lombok.Data;

@Data
public class Result extends Response{
    private int total;
    private int start;
    private int count;
    private int systemtotal;
}
