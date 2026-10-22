package com.agentguard.gateway;

import com.agentguard.model.ActionArgs;
import com.agentguard.model.ActionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/gateway/actions allows benign read_document")
    void testAllowAction() throws Exception {
        ActionArgs args = new ActionArgs(null, "Annual financial summary report.", 0, 0.0, List.of());
        ActionRequest req = new ActionRequest("DocBot", "read_document", args);

        mockMvc.perform(post("/api/gateway/actions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verdict").value("ALLOW"))
                .andExpect(jsonPath("$.score").value(0))
                .andExpect(jsonPath("$.safeOutput.status").value("MOCK_EXECUTED"));
    }

    @Test
    @DisplayName("POST /api/gateway/actions blocks shell command injection")
    void testBlockAction() throws Exception {
        ActionArgs args = new ActionArgs(null, "rm -rf /var/data", 0, 0.0, List.of());
        ActionRequest req = new ActionRequest("DocBot", "shell", args);

        mockMvc.perform(post("/api/gateway/actions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verdict").value("BLOCK"))
                .andExpect(jsonPath("$.score").value(80))
                .andExpect(jsonPath("$.safeOutput.status").value("WITHHELD"));
    }
}
