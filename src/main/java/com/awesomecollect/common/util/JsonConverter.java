package com.awesomecollect.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JSON文字列をDB登録用の文字列に変換するクラス。
 */
public final class JsonConverter {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * （DailyDone用）
   * JSON文字列のタグリストを確認し、要素がnullまたは空の場合は空のリストを、
   * そうでない場合は値のみを抽出してリストを作成し、リスト形式でまとめる。
   *
   * @param jsonTagsList  JSON文字列のタグリスト
   * @return  変換後のタグリスト
   */
  public static List<List<String>> extractValues(List<String> jsonTagsList){
    List<List<String>> pureTagsList = new ArrayList<>();
    for(String jsonTag : jsonTagsList){
      try{
        if(jsonTag == null || jsonTag.trim().isEmpty()){
          pureTagsList.add(Collections.emptyList());
          continue;
        }

        List<Map<String, String>> parsed =
            objectMapper.readValue(jsonTag, new TypeReference<>() {
            });

        List<String> pureTag = parsed.stream()
            .map(m -> m.getOrDefault("value",""))
            .toList();

        pureTagsList.add(pureTag);
      }catch(JsonProcessingException ex){
        pureTagsList.add(Collections.emptyList());
      }
    }
    return pureTagsList;
  }

  /**
   * （Goal, Memo, ArticleStork用）
   * JSON文字列のタグがnullまたは空の場合はnullを返し、
   * そうでない場合は値のみを抽出してリストを作成する。
   *
   * @param jsonTags  JSON文字列のタグ
   * @return  変換後のタグリスト
   */
  public static List<String> extractValues(String jsonTags){
      try{
        if(jsonTags == null || jsonTags.trim().isEmpty()){
          return null;
        }

        List<Map<String, String>> parsed =
            objectMapper.readValue(jsonTags, new TypeReference<>() {
            });

        return parsed.stream()
            .map(m -> m.getOrDefault("value",""))
            .toList();

      }catch(JsonProcessingException ex){
        return null;
      }
    }
}
