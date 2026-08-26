package com.agri.market.sms.client;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.sms.config.Msg91Properties;
import com.agri.market.sms.dto.SmsProviderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class Msg91Client implements SmsProviderClient {

    private final RestClient restClient;
    private final Msg91Properties msg91Properties;

    @Override
    public SmsProviderResponse sendOtp(
            final String phoneNumber
    ) {
        try {
            final String path =
                    msg91Properties.getOtp().getSendPath();

            final SmsProviderResponse response = restClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam(
                                    "template_id",
                                    msg91Properties
                                            .getOtp()
                                            .getTemplateId()
                            )
                            .queryParam(
                                    "mobile",
                                    phoneNumber
                            )
                            .queryParam(
                                    "authkey",
                                    msg91Properties.getAuthKey()
                            )
                            .build()
                    )
                    .header(
                            HttpHeaders.CONTENT_TYPE,
                            MediaType.APPLICATION_JSON_VALUE
                    )
                    .retrieve()
                    .body(SmsProviderResponse.class);

            validateResponse(response);

            log.info("MSG91 OTP send request completed successfully");

            return response;

        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error(
                    "MSG91 OTP send request failed",
                    exception
            );

            throw new BusinessException(
                    ErrorCode.SMS_PROVIDER_ERROR
            );
        }
    }

    @Override
    public SmsProviderResponse verifyOtp(
            final String phoneNumber,
            final String otp
    ) {
        try {
            final String path =
                    msg91Properties.getOtp().getVerifyPath();

            final SmsProviderResponse response = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam(
                                    "mobile",
                                    phoneNumber
                            )
                            .queryParam(
                                    "otp",
                                    otp
                            )
                            .build()
                    )
                    .header(
                            "authkey",
                            msg91Properties.getAuthKey()
                    )
                    .retrieve()
                    .body(SmsProviderResponse.class);

            validateResponse(response);

            log.info(
                    "MSG91 OTP verification request completed successfully"
            );

            return response;

        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error(
                    "MSG91 OTP verification request failed",
                    exception
            );

            throw new BusinessException(
                    ErrorCode.SMS_PROVIDER_ERROR
            );
        }
    }

    @Override
    public SmsProviderResponse resendOtp(
            final String phoneNumber
    ) {
        try {
            final String path =
                    msg91Properties.getOtp().getResendPath();

            final SmsProviderResponse response = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam(
                                    "authkey",
                                    msg91Properties.getAuthKey()
                            )
                            .queryParam(
                                    "retrytype",
                                    msg91Properties
                                            .getOtp()
                                            .getRetryType()
                            )
                            .queryParam(
                                    "mobile",
                                    phoneNumber
                            )
                            .build()
                    )
                    .retrieve()
                    .body(SmsProviderResponse.class);

            validateResponse(response);

            log.info("MSG91 OTP resend request completed successfully");

            return response;

        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error(
                    "MSG91 OTP resend request failed",
                    exception
            );

            throw new BusinessException(
                    ErrorCode.SMS_PROVIDER_ERROR
            );
        }
    }

    private void validateResponse(
            final SmsProviderResponse response
    ) {
        if (response == null || !response.isSuccess()) {
            throw new BusinessException(
                    ErrorCode.SMS_PROVIDER_ERROR
            );
        }
    }
}