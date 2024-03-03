package com.sikdorok.appapi.presentation.shared.response.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PagingDTO {

    private int number; // 현재 페이지
    private int totalPages; // 전체 페이지수
    private long totalElements; // 전체 데이터 개수

    @Override
    public String toString() {
        return "{"
            + "\"number\":\"" + number + "\""
            + ", \"totalPages\":\"" + totalPages + "\""
            + ", \"totalElements\":\"" + totalElements + "\""
            + "}";
    }

    public int getNumber() {
        return number + 1;
    }
}
