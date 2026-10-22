package com.agentguard.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ActionRequest {
    @NotBlank(message = "agent name is required")
    private String agent;

    @NotBlank(message = "tool name is required")
    private String tool;

    @NotNull(message = "args object is required")
    private ActionArgs args = new ActionArgs();

    public ActionRequest() {
    }

    public ActionRequest(String agent, String tool, ActionArgs args) {
        this.agent = agent;
        this.tool = tool;
        this.args = args != null ? args : new ActionArgs();
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

    public ActionArgs getArgs() {
        return args;
    }

    public void setArgs(ActionArgs args) {
        this.args = args != null ? args : new ActionArgs();
    }
}
