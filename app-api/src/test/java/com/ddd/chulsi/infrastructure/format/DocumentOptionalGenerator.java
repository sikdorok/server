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

    public static Attributes.Attribute dateFormatFull() {
        return setFormat("yyyy-MM-dd HH:mm");
    }

    public static Attributes.Attribute tagFormat() {
        return setFormat("C000300001 : 아침, C000300002 : 점심, C000300003 : 저녁, C000300004 : 간식");
    }

    public static Attributes.Attribute iconFormat() {
        return setFormat("C000400001 : 미지정, C000400002 : 밥, C000400003 : 라면, C000400004 : 샐러드, C000400005 : 고기, C000400006 : 빵, C000400007 : 패스트푸드, C000400008 : 초밥C000400009 : 케이크");
    }

}
