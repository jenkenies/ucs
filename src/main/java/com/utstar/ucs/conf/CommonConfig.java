package com.utstar.ucs.conf;

import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommonConfig {

    @Value("${ucs.domainid}")
    private Integer domainid;

    @Value("${ucs.bookmarktotal}")
    private Integer bookmarktotal;

    @Value("${ucs.expiretime}")
    private Integer expiretime;

    @Value("${ucs.hottotal}")
    private Integer hottotal;

    @Value("${ucs.bookmarktotal}")
    private Integer favoritektotal;



    @Value("${ucs.reservetotal}")
    private Integer reservetotal;
}
