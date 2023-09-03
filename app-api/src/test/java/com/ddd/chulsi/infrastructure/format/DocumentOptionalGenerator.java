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
        return setFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static Attributes.Attribute dateFormatYYYYMMDD() {
        return setFormat("yyyy-MM-dd");
    }

    public static Attributes.Attribute oauthTypeFormat() {
        return setFormat("C000200001 : Kakao");
    }

    public static Attributes.Attribute tagFormat() {
        return setFormat("C000300001 : 아침, C000300002 : 점심, C000300003 : 저녁, C000300004 : 간식");
    }

    public static Attributes.Attribute iconFormat() {
        return setFormat("C000400001 : 미지정, C000400002 : 밥, C000400003 : 라면, C000400004 : 샐러드, C000400005 : 고기, C000400006 : 빵, C000400007 : 패스트푸드, C000400008 : 초밥C000400009 : 케이크");
    }
    public static Attributes.Attribute policyItemTypeFormat() {
        return setFormat("C000500001 : 이용약관, C000500002 : 개인정보 동의, C000500003 : 제 3자 정보 제공 동의, C000500004 : 온보딩 기본 이미지, C000500005 : 메인 목록 기본 이미지, C000500006 : 메인 리스트뷰 기본 이미지");
    }

    public static Attributes.Attribute deviceTypeFormat() {
        return setFormat("C000700001 : AOS, C000700002 : IOS");
    }

}
