package org.example.domain.wiseSaying.util;

import org.example.domain.wiseSaying.entity.WiseSaying;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 명언 객체와 JSON 문자열 간의 변환을 담당하는 유틸리티 클래스
 */
public class WiseSayingJsonUtil {

    /**
     * WiseSaying 객체를 JSON 문자열로 변환.
     * @param wiseSaying 변환할 WiseSaying 객체
     * @return JSON 형식의 문자열
     */
    public static String toJson(WiseSaying wiseSaying) {
        return String.format(
                """
                {
                  "id": %d,
                  "content": "%s",
                  "author": "%s"
                }""",
                wiseSaying.getId(),
                escape(wiseSaying.getContent()),
                escape(wiseSaying.getAuthor())
        );
    }

    /**
     * JSON 문자열을 WiseSaying 객체로 변환.
     * @param json 변환할 JSON 문자열
     * @return 변환된 WiseSaying 객체
     */
    public static WiseSaying fromJson(String json) {
        Map<String, String> keyValuePairs = parseJson(json);
        return new WiseSaying(
                Integer.parseInt(keyValuePairs.get("id")),
                unescape(keyValuePairs.get("content")),
                unescape(keyValuePairs.get("author"))
        );
    }

    /**
     * WiseSaying 객체 리스트를 ID를 기준으로 오름차순 정렬한 뒤, JSON 배열 문자열로 변환.
     *
     * @param wiseSayings 변환할 WiseSaying 객체 리스트
     * @return JSON 배열 형식의 문자열 (ID 기준 오름차순 정렬)
     */
    public static String toJsonArray(List<WiseSaying> wiseSayings) {
        List<WiseSaying> sortedWiseSayings = wiseSayings.stream()
                .sorted(Comparator.comparingInt(WiseSaying::getId))
                .collect(Collectors.toList());

        StringBuilder jsonBuilder = new StringBuilder("[\n");
        for (int i = 0; i < sortedWiseSayings.size(); i++) {
            String json = toJson(sortedWiseSayings.get(i));
            jsonBuilder.append(json.indent(2).stripTrailing());
            if (i < sortedWiseSayings.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    /**
     * 특수 문자를 이스케이프 처리.
     * @param str 이스케이프 처리할 문자열
     * @return 이스케이프 처리된 문자열
     */
    private static String escape(String str) {
        return str.replace("\"", "\\\"").replace("\n", "\\n");
    }

    /**
     * 이스케이프 처리된 문자열을 원래 형태로 복원.
     * @param str 복원할 문자열
     * @return 복원된 문자열
     */
    private static String unescape(String str) {
        return str.replace("\\\"", "\"").replace("\\n", "\n");
    }

    /**
     * JSON 문자열을 파싱하여 키-값 쌍의 Map으로 변환.
     * @param json 파싱할 JSON 문자열
     * @return 파싱된 키-값 쌍의 Map
     */
    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().substring(1, json.length() - 1); // Remove { and }
        String[] pairs = json.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by commas outside quotes
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            map.put(key, value);
        }
        return map;
    }
}

