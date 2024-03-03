package com.sikdorok.appapi.presentation.enumDocument;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sikdorok.appapi.presentation.enumDocument.snippet.ApiResponseDto;
import com.sikdorok.appapi.presentation.enumDocument.snippet.CustomResponseFieldsSnippet;
import com.sikdorok.appapi.presentation.enumDocument.snippet.EnumDocs;
import com.sikdorok.appapi.presentation.shared.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnumCodeController.class)
public class EnumCodeControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void definedCode_정의() throws Exception {

        // 요청
        ResultActions result = mockMvc.perform(
            get("/enum-code/defined-code")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // 결과값
        MvcResult mvcResult = result.andReturn();

        // 데이터 파싱
        EnumDocs enumDocs = getData(mvcResult);

        // 문서화 진행
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("enum-code/defined-code",
                customResponseFields("custom-response", beneathPath("data.definedCode").withSubsectionId("definedCode"),
                    attributes(key("title").value("코드 정의")),
                    enumConvertFieldDescriptor((enumDocs.getDefinedCode()))
                )
            ));
    }

    // 커스텀 템플릿 사용을 위한 함수
    public static CustomResponseFieldsSnippet customResponseFields (String type,
                                                                    PayloadSubsectionExtractor<?> subsectionExtractor,
                                                                    Map<String, Object> attributes, FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(type, subsectionExtractor, Arrays.asList(descriptors), attributes
            , true);
    }

    // Map으로 넘어온 enumValue를 fieldWithPath로 변경하여 리턴
    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
            .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
            .toArray(FieldDescriptor[]::new);
    }

    // mvc result 데이터 파싱
    private EnumDocs getData(MvcResult result) throws IOException {
        ApiResponseDto<EnumDocs> apiResponseDto = objectMapper
            .readValue(result.getResponse().getContentAsByteArray(),
                new TypeReference<>() {}
            );
        return apiResponseDto.getData();
    }

}