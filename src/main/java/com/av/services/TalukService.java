package com.av.services;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

@Service
public class TalukService {


  private Map<String, List<String>> talukData;

  @PostConstruct
  public void loadData() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      InputStream is = getClass().getClassLoader().getResourceAsStream("kerala_taluks.json");
      talukData = mapper.readValue(is, new TypeReference<Map<String, List<String>>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Failed to load taluk data", e);
    }
  }

  public Map<String, List<String>> getAllTaluks() {
    return talukData;
  }

  public List<String> getTaluksByDistrict(String district) {
    return talukData.getOrDefault(district, null);
  }

}
