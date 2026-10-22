package com.agentguard.model;

public class FindingDto {
    private String type;
    private String value;
    private String reason;
    private boolean sensitive;
    private int start;
    private int end;

    public FindingDto() {
    }

    public FindingDto(String type, String value, String reason, boolean sensitive, int start, int end) {
        this.type = type;
        this.value = value;
        this.reason = reason;
        this.sensitive = sensitive;
        this.start = start;
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
