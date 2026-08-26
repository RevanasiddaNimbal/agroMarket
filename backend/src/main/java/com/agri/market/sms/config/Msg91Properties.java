package com.agri.market.sms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "msg91")
public class Msg91Properties {

    private String baseUrl;

    private String authKey;

    private Otp otp = new Otp();

    @Getter
    @Setter
    public static class Otp {

        private String templateId;

        private String sendPath;

        private String verifyPath;

        private String resendPath;

        private String retryType;
    }
}