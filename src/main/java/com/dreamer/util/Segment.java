package com.dreamer.util;

public class Segment {
    private String text;
    private String color;

    public Segment() {} // Required for Jackson

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
