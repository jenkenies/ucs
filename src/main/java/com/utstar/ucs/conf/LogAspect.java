package com.utstar.ucs.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utstar.ucs.constants.UcsConstants;
import com.utstar.ucs.resp.Response;
import com.utstar.ucs.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Aspect
@Slf4j
public class LogAspect {

    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("execution(public * com.utstar.ucs.controller.*Controller.*(..))")
    public void pointCut() {
    }

    @Before(value = "pointCut()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        // 这段代码会生成到controller 函数之前运行
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        try {

            log.info(String.format("method:[%s] uri:[%s] params:[%s]", request.getMethod(), request.getRequestURI(),
                    objectMapper.writeValueAsString(args[0])));
        } catch (JsonProcessingException e) {
            log.info("aspect[{}]", e.fillInStackTrace());
        }
    }

    /**
     * 在切入点开始处切入内容
     *
     * @param joinPoint
     */
    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Response res = new Response();
        res.setCode(UcsConstants.UCS_SUCESS);
        res.setMsg(UcsConstants.UCS_SUCESS_MSG);
        // 获取所有的请求参数
        Object[] args = joinPoint.getArgs();
        if (null != args && args.length > 0) {
            for (Object obj : args) {
                if (obj instanceof BindingResult) {
                    // 参数验证
                    res = validate((BindingResult) obj);
                    break;
                }
            }
        }
        // 验证通过执行拦截方法，否则不执行
        Object object = new Object();
        if (res.getCode() == UcsConstants.UCS_SUCESS) {
            try {
                // 执行拦截方法
                object =  joinPoint.proceed();
                return object;
            } catch (Throwable ex) {
                log.error("AOP执行拦截方法时异常, {}", ex);
                res.setCode(UcsConstants.UCS_FAIL);
                res.setMsg("AOP执行拦截方法时异常！" + ex.getMessage());
                return res;
            }
        } else{
            return  res;
        }


    }

    /**
     * 验证
     *
     * @param bindingResult
     * @return
     */
    private Response validate(BindingResult bindingResult) {
        Response res = new Response();
        // 参数验证结果
        if (bindingResult.hasErrors()) {
            res = new Response();
            res.setCode(UcsConstants.UCS_FAIL);
            res.setMsg(bindingResult.getFieldError().getDefaultMessage());
            return res;
        }
        res.setCode(UcsConstants.UCS_SUCESS);
        res.setMsg(UcsConstants.UCS_SUCESS_MSG);
        return res;

    }
}
