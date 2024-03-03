package com.sikdorok.appapi.presentation.enumDocument;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.presentation.enumDocument.snippet.ApiResponseDto;
import com.sikdorok.appapi.presentation.enumDocument.snippet.EnumDocs;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/enum-code")
public class EnumCodeController {

    @GetMapping(value = "/defined-code", name = "DefinedCode 정의")
    public ApiResponseDto<EnumDocs> definedCode() {

        Map<String, String> definedCodeMap = sortMapByKey(getDocs(DefinedCode.values()));

        return ApiResponseDto.of(EnumDocs.builder()
                .definedCode(definedCodeMap)
            .build());

    }

    /**
     * 문서화 할 데이터 파싱
     */
    private Map<String, String> getDocs(DefinedCode[] definedCodes) {
        return Arrays.stream(definedCodes)
            .filter(definedCode -> StringUtils.isNotBlank(definedCode.getDivisionCode()))
            .collect(Collectors.toMap(DefinedCode::getName, DefinedCode::getDescription));
    }

    /**
     * key 기준으로 정렬
     */
    private static LinkedHashMap<String, String> sortMapByKey(Map<String, String> map) {
        List<Map.Entry<String, String>> entries = new LinkedList<>(map.entrySet());
        entries.sort(Map.Entry.comparingByKey());

        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
