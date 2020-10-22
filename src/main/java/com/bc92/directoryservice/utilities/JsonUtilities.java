package com.bc92.directoryservice.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtilities {

  private static final Logger logger = LoggerFactory.getLogger(JsonUtilities.class);

  private static ObjectMapper mapper = new ObjectMapper();

  private JsonUtilities() {}

  public static <T> T jsonToObject(final String jsonString, final Class<T> clazz) {
    try {
      return mapper.readValue(jsonString, clazz);
    } catch (JsonProcessingException e) {
      logger.error("Failed to read Json to class: {}", clazz.getCanonicalName(), e);
    }
    return null;
  }

  public static <T> String objectToJson(final T object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      logger.error("Failed to read class to json. Object: {}", object, e);
    }
    return null;
  }

  public static <T> String objectToPrettyJson(final T object) {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      logger.error("Failed to read class to json. Object: {}", object, e);
    }
    return null;
  }

}
