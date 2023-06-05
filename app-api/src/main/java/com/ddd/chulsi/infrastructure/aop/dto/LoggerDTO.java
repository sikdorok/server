package com.ddd.chulsi.infrastructure.aop.dto;

import lombok.*;
import org.json.JSONObject;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoggerDTO {

    private String uuid; // 고유번호 랜덤 생성
    private String controller; // 컨트롤러 이름
    private String method; // 메서드 이름
    private String message; // 주석 값
    private String date; // 날짜
    private String httpMethod; // Http 메서드
    private String requestUri; // 요정 URI
    private Map<String, String> header;
    private String requestBody; // 요청 Request Body
    private String responseBody; // 응답 Response Body

    /**
     * Header To Json
     * @return
     */
    public JSONObject getHeaderToJson() {
        return new JSONObject(header);
    }

    /**
     * Response Body Get
     * @return String
     */
    public String getResponseBody() {
        if (responseBody == null || responseBody.equals("")) {
            return "{}";
        }

        return this.responseBody;
    }

    /**
     * 중괄호 앞 뒤 2개이상 자르기
     * @param str : String
     * @return String
     */
    public String replaceBrace(String str) {
        int index = 0;
        boolean checkFront = false;
        boolean checkBack = false;

        while(!checkFront && !checkBack) {
            if (str.contains("{{")) {
                str = str.replace("{{", "{");
                index++;
            } else {
                checkFront = true;
            }

            if (str.contains("}}")) {
                if (index == 0 && checkFront) {
                    // 2 depth toString이 1 depth toString의 마지막 순서 일 때
                    index = -1;
                    checkBack = true;
                }
                else str = str.replace("}}", "}");
            } else {
                checkBack = true;
            }
        }

        StringBuilder strBuilder = new StringBuilder(str);
        for(int i=0; i<=index; i++) strBuilder.append("}");
        str = fixedBrace(strBuilder.toString());

        return str;
    }

    @SneakyThrows
    @Override
    public String toString() {
        String toString = "{"
            + "\"uuid\":\"" + uuid + "\""
            + ",\"controller\":\"" + controller + "\""
            + ",\"method\":\"" + method + "\""
            + ",\"message\":\"" + message + "\""
            + ",\"date\": \"" + date + "\""
            + ",\"httpMethod\":\"" + httpMethod + "\""
            + ",\"requestUri\":\"" + requestUri + "\""
            + ",\"header\":" + getHeaderToJson()
            + ",\"requestBody\":\"" + requestBody + "\""
            + ",\"responseBody\":" + getResponseBody()
            + "}";

        return replaceBrace(toString);
    }

    /**
     * 괄호 개수
     * @param str
     * @param key
     * @return
     */
    public int checkBrace(String str, char key) {
        return (int) str.chars().filter(s -> s == key).count();
    }

    public String fixedBrace(String str) {
        int frontBrace = this.checkBrace(str, '{');
        int backBrace = this.checkBrace(str, '}');
        if (frontBrace > backBrace) { // 닫는 개수가 부족할 때
            StringBuilder strBuilder1 = new StringBuilder(str);
            for(int i = 0; i<frontBrace-backBrace; i++) strBuilder1.append("}");
            str = strBuilder1.toString();
        } else if (frontBrace < backBrace) { // 닫는 개수가 더 많을 때
            str = str.substring(0, str.length() - (backBrace - frontBrace));
        }
        return str;
    }

}
