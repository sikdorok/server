package com.ddd.chulsi.infrastructure.format;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentOptionalGenerator {
    
    public static Attributes.Attribute setFormat(String value) {
        return key("format").value(value);
    }

    public static Attributes.Attribute revokeFormat() {
        return setFormat("1 : 비활성화, 2 : 삭제");
    }

}
