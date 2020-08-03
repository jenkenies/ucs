package com.utstar.ucs.util;

import lombok.Data;

@Data
public class Pagination {

    private int start = 0;//默认从0开始

    private int count = 100;//100

    private String typelist;

    private int total;
}
