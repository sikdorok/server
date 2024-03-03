package com.sikdorok.appapi.infrastructure.oauth;

import com.sikdorok.appapi.infrastructure.exception.OauthException;
import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthKakaoServiceImpl implements OauthKakaoService {

    private final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
    private final String KAKAO_API_URL = "https://kapi.kakao.com";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Override
    public OauthInfo.KakaoInfoResponse getAccessToken(String authorizationCode) {
        String url = KAKAO_AUTH_URL + "/oauth/token";

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("code", authorizationCode);
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, httpHeaders);

            OauthInfo.KakaoInfoResponse response = restTemplate.postForObject(
                url,
                request,
                OauthInfo.KakaoInfoResponse.class
            );

            if (response == null) throw new OauthException(702, ErrorMessage.OAUTH_RESPONSE_EMPTY.replace("{oauth}", "Kakao"));

            log.info("accessToken : {}", response.accessToken());

            return response;
        } catch (HttpClientErrorException e) {
            throw new OauthException(701, ErrorMessage.OAUTH_REQUEST_FAILED.replace("{oauth}", "Kakao"));
        } catch (Exception e) {
            throw new OauthException(700, ErrorMessage.OAUTH_REQUEST_FAILED.replace("{oauth}", "Kakao"));
        }
    }

    @Override
    public void logout(String accessToken) {
        String url = KAKAO_API_URL + "/v1/user/logout";

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
            httpHeaders.set("Authorization", accessToken.startsWith(JwtTokenUtil.PREFIX) ? accessToken : JwtTokenUtil.PREFIX + accessToken);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, httpHeaders);

            restTemplate.postForObject(
                url,
                request,
                OauthInfo.KakaoLogoutResponse.class
            );

        } catch (Exception ignored) {

        }
    }

    @Override
    public OauthInfo.KakaoUserMe getUserName(String accessToken) {
        String url = KAKAO_API_URL + "/v2/user/me";

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
            httpHeaders.set("Authorization", accessToken.startsWith(JwtTokenUtil.PREFIX) ? accessToken : JwtTokenUtil.PREFIX + accessToken);

            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("property_keys", "[\"kakao_account.profile\", \"kakao_account.email\"]");
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(requestBody, httpHeaders);

            OauthInfo.KakaoUserMe response = restTemplate.postForObject(
                url,
                request,
                OauthInfo.KakaoUserMe.class
            );

            if (response == null) throw new OauthException(702, ErrorMessage.OAUTH_RESPONSE_EMPTY.replace("{oauth}", "Kakao"));

            log.info("response : {}", response);

            return response;
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new OauthException(701, ErrorMessage.OAUTH_REQUEST_FAILED.replace("{oauth}", "Kakao"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new OauthException(700, ErrorMessage.OAUTH_REQUEST_FAILED.replace("{oauth}", "Kakao"));
        }
    }

}
