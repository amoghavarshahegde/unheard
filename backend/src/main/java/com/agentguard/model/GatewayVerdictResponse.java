package com.agentguard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatewayVerdictResponse {
    private String id;
    private String agent;
    private String tool;
    private Verdict verdict;
    private int score;
    private List<String> reasons = new ArrayList<>();
    private List<FindingDto> findings = new ArrayList<>();
    private Map<String, Object> safeOutput = new HashMap<>();
    private RouterInfo router = new RouterInfo();
    private String timestamp;

    public GatewayVerdictResponse() {
    }

    public GatewayVerdictResponse(String id, String agent, String tool, Verdict verdict, int score,
                                  List<String> reasons, List<FindingDto> findings,
                                  Map<String, Object> safeOutput, RouterInfo router, String timestamp) {
        this.id = id;
        this.agent = agent;
        this.tool = tool;
        this.verdict = verdict;
        this.score = score;
        this.reasons = reasons != null ? reasons : new ArrayList<>();
        this.findings = findings != null ? findings : new ArrayList<>();
        this.safeOutput = safeOutput != null ? safeOutput : new HashMap<>();
        this.router = router != null ? router : new RouterInfo();
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public void setVerdict(Verdict verdict) {
        this.verdict = verdict;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons != null ? reasons : new ArrayList<>();
    }

    public List<FindingDto> getFindings() {
        return findings;
    }

    public void setFindings(List<FindingDto> findings) {
        this.findings = findings != null ? findings : new ArrayList<>();
    }

    public Map<String, Object> getSafeOutput() {
        return safeOutput;
    }

    public void setSafeOutput(Map<String, Object> safeOutput) {
        this.safeOutput = safeOutput != null ? safeOutput : new HashMap<>();
    }

    public RouterInfo getRouter() {
        return router;
    }

    public void setRouter(RouterInfo router) {
        this.router = router != null ? router : new RouterInfo();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
