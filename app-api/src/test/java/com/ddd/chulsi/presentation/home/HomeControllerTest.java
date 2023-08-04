package com.ddd.chulsi.presentation.home;

import com.ddd.chulsi.application.feed.FeedFacade;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.jwt.JwtTokenUtil;
import com.ddd.chulsi.presentation.shared.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.ddd.chulsi.infrastructure.format.DocumentOptionalGenerator.*;
import static com.ddd.chulsi.infrastructure.inMemory.feed.FeedFactory.givenListResponse;
import static com.ddd.chulsi.infrastructure.inMemory.feed.FeedFactory.givenMonthlyResponse;
import static com.ddd.chulsi.infrastructure.util.ApiDocumentUtils.getDocumentRequest;
import static com.ddd.chulsi.infrastructure.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedFacade feedFacade;

    @Test
    void 월_단위_목록() throws Exception {

        given(feedFacade.monthly(anyString(), any(LocalDate.class))).willReturn(givenMonthlyResponse());

        mockMvc.perform(
                get("/home/monthly?date={date}", LocalDate.now())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenUtil.PREFIX + "AccessToken")
                    .with(user("user").authorities((GrantedAuthority) () -> String.valueOf(DefinedCode.C000100001)))
                    .with(user("user").authorities((GrantedAuthority) () -> String.valueOf(DefinedCode.C000100002)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("home/monthly",
                getDocumentRequest(),
                getDocumentResponse(),
                queryParameters(
                    parameterWithName("date").attributes(dateFormatYYYYMMDD()).description("날짜")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("data.date").type(JsonFieldType.STRING).description("조회 날짜"),
                    fieldWithPath("data.weeklyCovers[]").type(JsonFieldType.ARRAY).description("주차 목록"),
                    fieldWithPath("data.weeklyCovers[].week").type(JsonFieldType.NUMBER).description("주차"),
                    fieldWithPath("data.weeklyCovers[].weeklyFeeds[]").type(JsonFieldType.ARRAY).description("주차 별 목록"),
                    fieldWithPath("data.weeklyCovers[].weeklyFeeds[].time").type(JsonFieldType.STRING).description("시간"),
                    fieldWithPath("data.weeklyCovers[].weeklyFeeds[].icon").type(JsonFieldType.STRING).attributes(iconFormat()).description("아이콘 유형")
                )
            ));

    }

    @Test
    void 태그_목록_조회() throws Exception {

        given(feedFacade.list(anyString(), any(FeedCommand.ListCommand.class))).willReturn(givenListResponse());

        mockMvc.perform(
                get(
                    "/home/list?page={page}&size={size}&date={date}&tag={tag}",
                    1,
                    10,
                    LocalDate.now(),
                    DefinedCode.C000300001
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenUtil.PREFIX + "AccessToken")
                    .with(user("user").authorities((GrantedAuthority) () -> String.valueOf(DefinedCode.C000100001)))
                    .with(user("user").authorities((GrantedAuthority) () -> String.valueOf(DefinedCode.C000100002)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("home/list",
                getDocumentRequest(),
                getDocumentResponse(),
                queryParameters(
                    parameterWithName("page").description("요청 페이지"),
                    parameterWithName("size").description("한 페이지에 가져올 목록 개수"),
                    parameterWithName("date").attributes(dateFormatYYYYMMDD()).description("날짜"),
                    parameterWithName("tag").attributes(tagFormat()).description("태그")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("data.paging").type(JsonFieldType.OBJECT).description("페이징 정보"),
                    fieldWithPath("data.paging.number").type(JsonFieldType.NUMBER).description("페이징 정보 > 현재 페이지 번호"),
                    fieldWithPath("data.paging.totalPages").type(JsonFieldType.NUMBER).description("페이징 정보 > 전체 페이지 수"),
                    fieldWithPath("data.paging.totalElements").type(JsonFieldType.NUMBER).description("페이징 정보 > 전체 개수"),
                    fieldWithPath("data.dailyFeeds[]").type(JsonFieldType.ARRAY).description("주차 목록"),
                    fieldWithPath("data.dailyFeeds[].feedId").type(JsonFieldType.STRING).description("피드 고유번호"),
                    fieldWithPath("data.dailyFeeds[].icon").type(JsonFieldType.STRING).attributes(iconFormat()).description("아이콘 유형"),
                    fieldWithPath("data.dailyFeeds[].isMain").type(JsonFieldType.BOOLEAN).description("대표 유무"),
                    fieldWithPath("data.dailyFeeds[].tag").type(JsonFieldType.STRING).attributes(tagFormat()).description("태그 유형"),
                    fieldWithPath("data.dailyFeeds[].time").type(JsonFieldType.STRING).description("기록 시간"),
                    fieldWithPath("data.dailyFeeds[].memo").type(JsonFieldType.STRING).description("메모").optional(),
                    fieldWithPath("data.dailyFeeds[].photosInfoList[]").type(JsonFieldType.ARRAY).description("사진 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].photosInfoList[].token").type(JsonFieldType.STRING).description("사진 토큰").optional(),
                    fieldWithPath("data.dailyFeeds[].photosInfoList[].uploadFullPath").type(JsonFieldType.STRING).description("사진 주소").optional()
                )
            ));

    }

}