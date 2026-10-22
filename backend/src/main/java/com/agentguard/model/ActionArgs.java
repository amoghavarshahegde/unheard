package com.agentguard.model;

import java.util.ArrayList;
import java.util.List;

public class ActionArgs {
    private String to;
    private String text;
    private Integer rows;
    private Double amount;
    private List<String> attachments = new ArrayList<>();

    public ActionArgs() {
    }

    public ActionArgs(String to, String text, Integer rows, Double amount, List<String> attachments) {
        this.to = to;
        this.text = text;
        this.rows = rows;
        this.amount = amount;
        this.attachments = attachments != null ? attachments : new ArrayList<>();
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments != null ? attachments : new ArrayList<>();
    }
}
