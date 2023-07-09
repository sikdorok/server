package com.ddd.chulsi.presentation.oauth;

import com.ddd.chulsi.application.users.UsersFacade;
import com.ddd.chulsi.infrastructure.jwt.JwtTokenUtil;
import com.ddd.chulsi.infrastructure.oauth.OauthCommand;
import com.ddd.chulsi.presentation.shared.ControllerTest;
import com.ddd.chulsi.presentation.users.UsersController;
import com.ddd.chulsi.presentation.users.dto.UsersDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static com.ddd.chulsi.infrastructure.inMemory.users.UsersFactory.givenLoginResponse;
import static com.ddd.chulsi.infrastructure.util.ApiDocumentUtils.getDocumentRequest;
import static com.ddd.chulsi.infrastructure.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsersController.class)
class UsersControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersFacade usersFacade;

    @Test
    void 카카오_로그인() throws Exception {

        given(usersFacade.kakaoLogin(any(OauthCommand.LoginCommand.class), any(HttpServletResponse.class))).willReturn(givenLoginResponse());

        UsersDTO.OauthLoginRequest request = new UsersDTO.OauthLoginRequest("authorizationCode");

        mockMvc.perform(
                post("/users/kakao/login")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("users/kakao/login",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("authorizationCode").type(JsonFieldType.STRING).description("인가 코드")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("data.usersInfoLogin").type(JsonFieldType.OBJECT).description("유저 정보"),
                    fieldWithPath("data.usersInfoLogin.usersId").type(JsonFieldType.STRING).description("유저 고유번호"),
                    fieldWithPath("data.usersInfoLogin.accessToken").type(JsonFieldType.STRING).description("access token"),
                    fieldWithPath("data.usersInfoLogin.lastLoginAt").type(JsonFieldType.STRING).description("마지막 로그인 일시")
                )
            ));

    }

    @Test
    void 로그아웃() throws Exception {

        doNothing().when(usersFacade).logout(anyString());

        mockMvc.perform(
                put("/users/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenUtil.PREFIX + "AccessToken")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("users/logout",
                getDocumentRequest(),
                getDocumentResponse(),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("결과 데이터")
                )
            ));

    }

}