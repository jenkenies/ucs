package com.utstar.ucs.resp;

import com.utstar.ucs.init.Favorite;
import lombok.Data;

@Data
public class GetFavorite extends Favorite {

    private int start = 0;//默认从0开始

    private int count = 100;//100

    private String typelist;

    private int total;
}
