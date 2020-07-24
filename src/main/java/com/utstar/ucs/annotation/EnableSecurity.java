package com.utstar.ucs.annotation;

import com.utstar.ucs.advice.EncryptRequestBodyAdvice;
import com.utstar.ucs.advice.EncryptResponseBodyAdvice;
import com.utstar.ucs.conf.SecretKeyConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({SecretKeyConfig.class,
        EncryptResponseBodyAdvice.class,
        EncryptRequestBodyAdvice.class})
public @interface EnableSecurity{

}
