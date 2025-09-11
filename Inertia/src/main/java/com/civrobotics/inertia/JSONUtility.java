package com.civrobotics.inertia;

import android.os.Environment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

class JSONUtility {

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    public static String alternativePath = "";
    public static boolean useAlternativePath = false;
    private final String DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FIRST/settings/";

    void saveVariablesToJSON(Map<String, Object> dataMap, String fileName) throws IOException {
        File destFile = new File(DIRECTORY_PATH + fileName + ".json");
        if (useAlternativePath) {
            destFile = new File(alternativePath + fileName + ".json");
        }
        objectMapper.writeValue(destFile, dataMap);
    }

    Object getVariableFromJSON(String fileName, String key) throws IOException {
        return getAllVariablesFromJSON(fileName).get(key);
    }

    Map<String, Object> getAllVariablesFromJSON(String fileName) throws IOException {
        File destFile = new File(DIRECTORY_PATH + fileName + ".json");
        if (useAlternativePath) {
            destFile = new File(alternativePath + fileName + ".json");
        }
        return objectMapper.readValue(destFile, new TypeReference<Map<String, Object>>() {
        });
    }


}
