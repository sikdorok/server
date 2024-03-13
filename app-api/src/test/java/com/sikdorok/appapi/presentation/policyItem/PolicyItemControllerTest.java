package com.sikdorok.appapi.presentation.policyItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sikdorok.appapi.application.policyItem.PolicyItemFacade;
import com.sikdorok.appapi.infrastructure.format.DocumentOptionalGenerator;
import com.sikdorok.appapi.infrastructure.inMemory.policyItem.PolicyItemFactory;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import com.sikdorok.appapi.infrastructure.util.ApiDocumentUtils;
import com.sikdorok.appapi.presentation.policyItem.dto.PolicyItemDTO;
import com.sikdorok.appapi.presentation.shared.ControllerTest;
import com.sikdorok.domaincore.model.policyItem.PolicyItemCommand;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyItemController.class)
class PolicyItemControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PolicyItemFacade policyItemFacade;

    @Test
    void 단일_조회() throws Exception {

        given(policyItemFacade.info(any(UUID.class))).willReturn(PolicyItemFactory.givenPolicyItemInfoResponse());

        mockMvc.perform(
                get(
                    "/policy-item/{policyItemId}",
                    UUID.randomUUID()
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcRestDocumentation.document("policy-item/info",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                pathParameters(
                    parameterWithName("policyItemId").description("정책 아이템 고유번호")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("data.policyItemId").type(JsonFieldType.STRING).description("정책 아이템 고유번호"),
                    fieldWithPath("data.type").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.policyItemTypeFormat()).description("유형"),
                    fieldWithPath("data.data").type(JsonFieldType.STRING).description("데이터").optional(),
                    fieldWithPath("data.sort").type(JsonFieldType.NUMBER).description("정렬"),
                    fieldWithPath("data.photosInfoList[]").type(JsonFieldType.ARRAY).description("태그").optional(),
                    fieldWithPath("data.photosInfoList[].photosId").type(JsonFieldType.STRING).description("이미지 고유번호").optional(),
                    fieldWithPath("data.photosInfoList[].token").type(JsonFieldType.STRING).description("이미지 토큰").optional(),
                    fieldWithPath("data.photosInfoList[].uploadFullPath").type(JsonFieldType.STRING).description("이미지 경로").optional()
                )
            ));

    }

    @Test
    void 등록() throws Exception {

        doNothing().when(policyItemFacade).register(anyString(), any(PolicyItemCommand.RegisterCommand.class), any(MultipartFile.class));

        PolicyItemDTO.RegisterRequest policyItemRegisterRequest = PolicyItemFactory.givenRegisterRequest();

        MockMultipartFile file = new MockMultipartFile("file", "profile.png", "multipart/form-data", "uploadFile".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile request = new MockMultipartFile("request", null, "application/json", objectMapper.writeValueAsString(policyItemRegisterRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/policy-item")
                    .file(file)
                    .file(request)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenUtil.PREFIX + "AccessToken")
                    .with(user("user").authorities((GrantedAuthority) () -> String.valueOf(DefinedCode.C000100001)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcRestDocumentation.document("policy-item/register",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),requestParts(
                    partWithName("request").description("요청값"),
                    partWithName("file").description("업로드 파일").optional()
                ),
                requestPartFields(
                    "request",
                    fieldWithPath("type").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.policyItemTypeFormat()).description("유형"),
                    fieldWithPath("data").type(JsonFieldType.STRING).description("데이터").optional(),
                    fieldWithPath("sort").type(JsonFieldType.NUMBER).description("정렬")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("결과 데이터")
                )
            ));

    }

    @Test
    void 수정() throws Exception {

        doNothing().when(policyItemFacade).register(anyString(), any(PolicyItemCommand.RegisterCommand.class), any(MultipartFile.class));

        PolicyItemDTO.UpdateRequest policyItemInfoUpdateRequest = PolicyItemFactory.givenUpdateRequest();

        MockMultipartFile file = new MockMultipartFile("file", "profile.png", "multipart/form-data", "uploadFile".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile request = new MockMultipartFile("request", null, "application/json", objectMapper.writeValueAsString(policyItemInfoUpdateRequest).getBytes(StandardCharsets.UTF_8));

        MockMultipartHttpServletRequestBuilder builder =
            RestDocumentationRequestBuilders.
                multipart("/policy-item");

        builder.with(builderRequest -> {
            builderRequest.setMethod("PUT");
            return builderRequest;
        });

        mockMvc.perform(
                builder
                    .file(file)
                    .file(request)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenUtil.PREFIX + "AccessToken")
                    .with(user("user").authorities((GrantedAuthority) () -> String.valueOf(DefinedCode.C000100001)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcRestDocumentation.document("policy-item/info-update",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),requestParts(
                    partWithName("request").description("요청값"),
                    partWithName("file").description("업로드 파일").optional()
                ),
                requestPartFields(
                    "request",
                    fieldWithPath("policyItemId").type(JsonFieldType.STRING).description("정책 아이템 고유번호"),
                    fieldWithPath("type").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.policyItemTypeFormat()).description("유형"),
                    fieldWithPath("data").type(JsonFieldType.STRING).description("데이터").optional(),
                    fieldWithPath("sort").type(JsonFieldType.NUMBER).description("정렬"),
                    fieldWithPath("deletePhotoTokens").type(JsonFieldType.ARRAY).description("삭제할 사진 토큰").optional()
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("결과 데이터")
                )
            ));

    }

    @Test
    void 삭제() throws Exception {

        doNothing().when(policyItemFacade).delete(anyString(), any(UUID.class));

        mockMvc.perform(
                delete(
                    "/policy-item/{policyItemId}",
                    UUID.randomUUID()
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenUtil.PREFIX + "AccessToken")
                    .with(user("user").authorities((GrantedAuthority) () -> String.valueOf(DefinedCode.C000100001)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcRestDocumentation.document("policy-item/delete",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                pathParameters(
                    parameterWithName("policyItemId").description("정책 아이템 고유번호")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("결과 데이터")
                )
            ));

    }

}