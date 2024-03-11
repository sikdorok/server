package com.sikdorok.appapi.presentation.home;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.sikdorok.appapi.application.feed.FeedFacade;
import com.sikdorok.domaincore.model.feed.FeedCommand;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.infrastructure.format.DocumentOptionalGenerator;
import com.sikdorok.appapi.infrastructure.inMemory.feed.FeedFactory;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import com.sikdorok.appapi.infrastructure.util.ApiDocumentUtils;
import com.sikdorok.appapi.presentation.shared.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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

        given(feedFacade.monthly(anyString(), any(LocalDate.class))).willReturn(FeedFactory.givenMonthlyResponse());

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
            .andDo(MockMvcRestDocumentationWrapper.document("home/monthly",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                queryParameters(
                    parameterWithName("date").attributes(DocumentOptionalGenerator.dateFormatYYYYMMDD()).description("날짜")
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
                    fieldWithPath("data.weeklyCovers[].weeklyFeeds[].icon").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.iconFormat()).description("아이콘 유형")
                )
            ));

    }

    @Test
    void 태그_목록_조회() throws Exception {

        given(feedFacade.list(anyString(), any(FeedCommand.ListCommand.class))).willReturn(FeedFactory.givenListResponse());

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
            .andDo(MockMvcRestDocumentationWrapper.document("home/list",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                queryParameters(
                    parameterWithName("page").description("요청 페이지"),
                    parameterWithName("size").description("한 페이지에 가져올 목록 개수"),
                    parameterWithName("date").attributes(DocumentOptionalGenerator.dateFormatYYYYMMDD()).description("날짜"),
                    parameterWithName("tag").attributes(DocumentOptionalGenerator.tagFormat()).description("태그").optional()
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
                    fieldWithPath("data.dailyFeeds[].icon").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.iconFormat()).description("아이콘 유형"),
                    fieldWithPath("data.dailyFeeds[].isMain").type(JsonFieldType.BOOLEAN).description("대표 유무"),
                    fieldWithPath("data.dailyFeeds[].tag").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.tagFormat()).description("태그 유형"),
                    fieldWithPath("data.dailyFeeds[].time").type(JsonFieldType.STRING).description("기록 시간"),
                    fieldWithPath("data.dailyFeeds[].memo").type(JsonFieldType.STRING).description("메모").optional(),
                    fieldWithPath("data.dailyFeeds[].photosInfoList[]").type(JsonFieldType.ARRAY).description("사진 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].photosInfoList[].token").type(JsonFieldType.STRING).description("사진 토큰").optional(),
                    fieldWithPath("data.dailyFeeds[].photosInfoList[].uploadFullPath").type(JsonFieldType.STRING).description("사진 주소").optional(),
                    fieldWithPath("data.initTag").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.tagFormat()).description("초기 태그 값"),
                    fieldWithPath("data.tags").type(JsonFieldType.ARRAY).attributes(DocumentOptionalGenerator.tagFormat()).description("검색 가능한 태그 목록").optional()
                )
            ));

    }

    @Test
    void 리스트뷰_목록_조회() throws Exception {

        given(feedFacade.listView(anyString(), any(FeedCommand.ListViewCommand.class))).willReturn(FeedFactory.givenListViewResponse());

        mockMvc.perform(
                get(
                    "/home/list-view?size={size}&date={date}&cursorDate={cursorDate}",
                    3,
                    LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1),
                    LocalDate.now()
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
            .andDo(MockMvcRestDocumentationWrapper.document("home/list-view",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                queryParameters(
                    parameterWithName("size").description("한 페이지에 가져올 목록 개수"),
                    parameterWithName("date").attributes(DocumentOptionalGenerator.dateFormatYYYYMMDD()).description("날짜"),
                    parameterWithName("cursorDate").attributes(DocumentOptionalGenerator.dateFormatYYYYMMDD()).description("커서 처리할 날짜").optional()
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 데이터 존재 유무"),
                    fieldWithPath("data.cursorDate").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.dateFormatYYYYMMDD()).description("커서 처리할 날짜").optional(),
                    fieldWithPath("data.dailyFeeds[]").type(JsonFieldType.ARRAY).description("피드 전체 목록"),
                    fieldWithPath("data.dailyFeeds[].date").type(JsonFieldType.STRING).description("날짜"),
                    fieldWithPath("data.dailyFeeds[].feeds").type(JsonFieldType.OBJECT).description("피드 상세 목록"),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[]").type(JsonFieldType.ARRAY).description("아침 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[].feedId").type(JsonFieldType.STRING).description("피드 고유번호"),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[].icon").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.iconFormat()).description("아이콘 유형"),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[].isMain").type(JsonFieldType.BOOLEAN).description("대표 유무"),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[].time").type(JsonFieldType.STRING).description("기록 시간"),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[].memo").type(JsonFieldType.STRING).description("메모").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[].photosInfoList[]").type(JsonFieldType.ARRAY).description("사진 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[].photosInfoList[].token").type(JsonFieldType.STRING).description("사진 토큰").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.morning[].photosInfoList[].uploadFullPath").type(JsonFieldType.STRING).description("사진 주소").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[]").type(JsonFieldType.ARRAY).description("아침 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[].feedId").type(JsonFieldType.STRING).description("피드 고유번호"),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[].icon").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.iconFormat()).description("아이콘 유형"),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[].isMain").type(JsonFieldType.BOOLEAN).description("대표 유무"),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[].time").type(JsonFieldType.STRING).description("기록 시간"),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[].memo").type(JsonFieldType.STRING).description("메모").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[].photosInfoList[]").type(JsonFieldType.ARRAY).description("사진 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[].photosInfoList[].token").type(JsonFieldType.STRING).description("사진 토큰").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.afternoon[].photosInfoList[].uploadFullPath").type(JsonFieldType.STRING).description("사진 주소").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[]").type(JsonFieldType.ARRAY).description("아침 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[].feedId").type(JsonFieldType.STRING).description("피드 고유번호"),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[].icon").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.iconFormat()).description("아이콘 유형"),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[].isMain").type(JsonFieldType.BOOLEAN).description("대표 유무"),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[].time").type(JsonFieldType.STRING).description("기록 시간"),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[].memo").type(JsonFieldType.STRING).description("메모").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[].photosInfoList[]").type(JsonFieldType.ARRAY).description("사진 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[].photosInfoList[].token").type(JsonFieldType.STRING).description("사진 토큰").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.evening[].photosInfoList[].uploadFullPath").type(JsonFieldType.STRING).description("사진 주소").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[]").type(JsonFieldType.ARRAY).description("아침 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[].feedId").type(JsonFieldType.STRING).description("피드 고유번호"),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[].icon").type(JsonFieldType.STRING).attributes(DocumentOptionalGenerator.iconFormat()).description("아이콘 유형"),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[].isMain").type(JsonFieldType.BOOLEAN).description("대표 유무"),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[].time").type(JsonFieldType.STRING).description("기록 시간"),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[].memo").type(JsonFieldType.STRING).description("메모").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[].photosInfoList[]").type(JsonFieldType.ARRAY).description("사진 목록").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[].photosInfoList[].token").type(JsonFieldType.STRING).description("사진 토큰").optional(),
                    fieldWithPath("data.dailyFeeds[].feeds.snack[].photosInfoList[].uploadFullPath").type(JsonFieldType.STRING).description("사진 주소").optional()
                )
            ));

    }

}