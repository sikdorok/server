package com.ddd.chulsi.presentation.oauth;

import com.ddd.chulsi.application.oauth.OauthFacade;
import com.ddd.chulsi.infrastructure.oauth.OauthCommand;
import com.ddd.chulsi.presentation.oauth.dto.OauthDTO;
import com.ddd.chulsi.presentation.shared.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static com.ddd.chulsi.infrastructure.inMemory.oauth.OauthFactory.givenLoginResponse;
import static com.ddd.chulsi.infrastructure.util.ApiDocumentUtils.getDocumentRequest;
import static com.ddd.chulsi.infrastructure.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OauthController.class)
class OauthControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OauthFacade oauthFacade;

    @Test
    @DisplayName("카카오 로그인")
    void kakaoLogin() throws Exception {

        given(oauthFacade.kakaoLogin(any(OauthCommand.LoginCommand.class))).willReturn(givenLoginResponse());

        OauthDTO.LoginRequest request = new OauthDTO.LoginRequest("authorizationCode");

        mockMvc.perform(
                post("/oauth/kakao/login")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("oauth/kakao/login",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("authorizationCode").type(JsonFieldType.STRING).description("인가 코드")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("access token")
                )
            ));

    }

}