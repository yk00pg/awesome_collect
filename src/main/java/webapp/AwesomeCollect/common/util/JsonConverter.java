package webapp.AwesomeCollect.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonConverter {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static List<List<String>> extractValues(List<String> jsonTagsList){
    List<List<String>> pureTagsList = new ArrayList<>();
    for(String jsonTag : jsonTagsList){
      try{
        if(jsonTag == null || jsonTag.trim().isEmpty()){
          pureTagsList.add(Collections.emptyList());
          continue;
        }

        List<Map<String, String>> parsed = objectMapper.readValue(jsonTag, new TypeReference<>() {
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

  public static List<String> extractValues(String jsonTags){
      try{
        if(jsonTags == null || jsonTags.trim().isEmpty()){
          return null;
        }

        List<Map<String, String>> parsed = objectMapper.readValue(jsonTags, new TypeReference<>() {
        });

        return parsed.stream()
            .map(m -> m.getOrDefault("value",""))
            .toList();

      }catch(JsonProcessingException ex){
        return null;
      }
    }
}
