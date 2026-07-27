package com.dreamer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonParserService {
    public static List<AnalysisRow> loadData(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Maps the JSON array directly to a List of AnalysisRow objects
        return mapper.readValue(new File(filePath), new TypeReference<List<AnalysisRow>>() {});
    }
}
