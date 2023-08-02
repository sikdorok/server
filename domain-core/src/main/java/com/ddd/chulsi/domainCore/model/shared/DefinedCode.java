package com.ddd.chulsi.domainCore.model.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum DefinedCode implements EnumType {

    C0001("C0001", "계정권한", "", ""),
    C000100001("C0001", "계정권한", "00001", "슈퍼관리자"),
    C000100002("C0001", "계정권한", "00002", "사용자"),
    C0002("C0002", "Oauth 종류", "", ""),
    C000200001("C0002", "Oauth 종류", "00001", "Kakao"),
    C0003("C0003", "태그 종류", "", ""),
    C000300001("C0003", "태그 종류", "00001", "아침"),
    C000300002("C0003", "태그 종류", "00002", "점심"),
    C000300003("C0003", "태그 종류", "00003", "저녁"),
    C000300004("C0003", "태그 종류", "00004", "간식"),
    C0004("C0004", "대표 아이콘 종류", "", ""),
    C000400001("C0004", "대표 아이콘 종류", "00001", "미지정"),
    C000400002("C0004", "대표 아이콘 종류", "00002", "밥"),
    C000400003("C0004", "대표 아이콘 종류", "00003", "라면"),
    C000400004("C0004", "대표 아이콘 종류", "00004", "샐러드"),
    C000400005("C0004", "대표 아이콘 종류", "00005", "고기"),
    C000400006("C0004", "대표 아이콘 종류", "00006", "빵"),
    C000400007("C0004", "대표 아이콘 종류", "00007", "패스트푸드"),
    C000400008("C0004", "대표 아이콘 종류", "00008", "초밥"),
    C000400009("C0004", "대표 아이콘 종류", "00009", "케이크"),
    C0005("C0005", "정책 카테고리", "", ""),
    C000500001("C0005", "정책 카테고리", "00001", "이용약관"),
    C000500002("C0005", "정책 카테고리", "00002", "개인정보 동의"),
    C000500003("C0005", "정책 카테고리", "00003", "제 3자 정보 제공 동의"),
    C000500004("C0005", "정책 카테고리", "00004", "온보딩 기본 이미지"),
    C000500005("C0005", "정책 카테고리", "00005", "메인 목록 기본 이미지"),
    C000500006("C0005", "정책 카테고리", "00006", "메인 리스트뷰 기본 이미지"),
    C0006("C0006", "도메인 종류", "", ""),
    C000600001("C0006", "도메인 종류", "00001", "피드"),
    C000600002("C0006", "도메인 종류", "00002", "정책");

    private final String sectionCode;
    private final String sectionCodeName;
    private final String divisionCode;
    private final String divisionCodeName;

    @Override
    public String getName() {
        return String.format("%s%s", this.sectionCode, this.divisionCode);
    }

    @Override
    public String getDescription() {
        return String.format("%s > %s", this.sectionCodeName, this.divisionCodeName);
    }

    public static Set<DefinedCode> setList(DefinedCode sectionCode) {
        return Arrays.stream(DefinedCode.values()).filter(definedCode -> definedCode.getSectionCode().equals(sectionCode.getSectionCode()) && !definedCode.getDivisionCode().isEmpty()).collect(Collectors.toSet());
    }

}
