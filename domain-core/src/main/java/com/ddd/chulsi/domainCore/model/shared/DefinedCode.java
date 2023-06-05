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
    C000100001("C0001", "계정권한", "00001", "슈퍼관리자");

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
