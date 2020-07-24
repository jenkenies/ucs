package com.utstar.ucs.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "aes.encrypt")
@Configuration
@Data
public class SecretKeyConfig{

    private String key;

    private String charset = "UTF-8";

    private boolean open = true;

    private boolean showLog = false;

    /**
     * 请求数据时间戳校验时间差
     * 超过指定时间的数据认定为伪造
     */
    private boolean timestampCheck = false;

}
