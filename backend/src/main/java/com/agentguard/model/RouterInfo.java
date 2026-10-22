package com.agentguard.model;

public class RouterInfo {
    private boolean modelCalled;
    private double costSavedUsd;
    private String details;

    public RouterInfo() {
    }

    public RouterInfo(boolean modelCalled, double costSavedUsd, String details) {
        this.modelCalled = modelCalled;
        this.costSavedUsd = costSavedUsd;
        this.details = details;
    }

    public boolean isModelCalled() {
        return modelCalled;
    }

    public void setModelCalled(boolean modelCalled) {
        this.modelCalled = modelCalled;
    }

    public double getCostSavedUsd() {
        return costSavedUsd;
    }

    public void setCostSavedUsd(double costSavedUsd) {
        this.costSavedUsd = costSavedUsd;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
