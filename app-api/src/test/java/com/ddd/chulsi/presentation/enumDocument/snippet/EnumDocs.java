package com.ddd.chulsi.presentation.enumDocument.snippet;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumDocs {

    /*
     * 문서화하고 싶은 모든 enum 명시
     */
    Map<String, String> definedCode;

}
