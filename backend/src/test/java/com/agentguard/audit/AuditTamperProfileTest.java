package com.agentguard.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditTamperProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/audit/tamper/{id} returns 404 when 'dev' profile is not active")
    void tamperEndpointReturns404UnderNonDevProfile() throws Exception {
        mockMvc.perform(post("/api/audit/tamper/any-record-id"))
                .andExpect(status().isNotFound());
    }
}
