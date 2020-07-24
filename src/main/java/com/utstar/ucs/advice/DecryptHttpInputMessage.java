package com.utstar.ucs.advice;

import com.utstar.ucs.annotation.Decrypt;
import com.utstar.ucs.conf.SecretKeyConfig;
import com.utstar.ucs.exception.EncryptRequestException;
import com.utstar.ucs.util.AESEncryptAndDecrypt;
import com.utstar.ucs.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
public class DecryptHttpInputMessage implements HttpInputMessage {

    private HttpHeaders headers;
    private InputStream body;


    public DecryptHttpInputMessage(HttpInputMessage inputMessage, SecretKeyConfig secretKeyConfig, Decrypt decrypt) throws Exception {
        String privateKey =  secretKeyConfig.getKey();
        String charset = secretKeyConfig.getCharset();
        boolean showLog = secretKeyConfig.isShowLog();
        boolean timestampCheck = secretKeyConfig.isTimestampCheck();

        if (StringUtils.isEmpty(privateKey)) {
            throw new IllegalArgumentException("privateKey is null");
        }

        this.headers = inputMessage.getHeaders();
        String content = new BufferedReader(new InputStreamReader(inputMessage.getBody()))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        String decryptBody;
        // 未加密内容
        if (secretKeyConfig.isOpen() && content.startsWith("{")) {
            // 必须加密
            if (decrypt.required()) {
                log.error("not support unencrypted content:{}", content);
                throw new EncryptRequestException("not support unencrypted content");
            }
            log.info("Unencrypted without decryption:{}", content);
            decryptBody = content;
        } else {
            StringBuilder json = new StringBuilder();
            content = content.replaceAll(" ", "+");

            if (!StringUtils.isEmpty(content)) {
                String[] contents = content.split("\\|");
                for (String value : contents) {
                    value = new String(AESEncryptAndDecrypt.decrypt(value, privateKey));
                    json.append(value);
                }
            }
            decryptBody = json.toString();
            if(showLog) {
                log.info("Encrypted data received：{},After decryption：{}", content, decryptBody);
            }
        }

        // 开启时间戳检查
        if (timestampCheck) {
            // 容忍最小请求时间
            long toleranceTime = System.currentTimeMillis() - decrypt.timeout();
            long requestTime = JsonUtils.getNode(decryptBody, "timestamp").asLong();
            // 如果请求时间小于最小容忍请求时间, 判定为超时
            if (requestTime < toleranceTime) {
                log.error("Encryption request has timed out, toleranceTime:{}, requestTime:{}, After decryption：{}", toleranceTime, requestTime, decryptBody);
                throw new EncryptRequestException("request timeout");
            }
        }

        this.body = new ByteArrayInputStream(decryptBody.getBytes());
    }

    @Override
    public InputStream getBody(){
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
