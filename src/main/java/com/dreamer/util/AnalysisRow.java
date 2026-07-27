package com.dreamer.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;


public class AnalysisRow {
    @JsonProperty("row_id")
    private int rowId;

    private List<Segment> segments = Collections.emptyList();

    public AnalysisRow() {}

    public int getRowId() { return rowId; }
    public void setRowId(int rowId) { this.rowId = rowId; }

    public List<Segment> getSegments() { return segments; }
    public void setSegments(List<Segment> segments) { this.segments = segments; }
}
