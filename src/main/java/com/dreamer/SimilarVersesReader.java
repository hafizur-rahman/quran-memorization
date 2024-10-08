package com.dreamer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimilarVersesReader {
    private ObjectMapper mapper = new ObjectMapper();

    public Map loadSimilarVerses(int juz) throws IOException {
        if (juz == 26) {
            String fileName = "similar-verses-juz-26.json";

            return mapper.readValue(TafsirPane.class.getResource(fileName).openStream(), HashMap.class);
        }

        return Collections.emptyMap();
    }
}
