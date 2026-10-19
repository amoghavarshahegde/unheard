package com.unheard.service.engine;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configurable scoring weights for the UNHEARD decision engine.
 *
 * PROTOTYPE DISCLAIMER:
 * These weights are illustrative defaults provided for a hackathon MVP.
 * They have NOT been validated against real disaster-response outcomes.
 * Operational systems must involve domain experts (disaster management,
 * epidemiology, social vulnerability research) in weight selection.
 * Results must always be reviewed by human coordinators before any action.
 */
@Component
@ConfigurationProperties(prefix = "unheard.scoring")
public class ScoringWeights {

    private Verification verification = new Verification();
    private Response response = new Response();

    public Verification getVerification() { return verification; }
    public void setVerification(Verification verification) { this.verification = verification; }
    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }

    public static class Verification {
        private double hazardWeight = 0.35;
        private double vulnerabilityWeight = 0.30;
        private double reportingGapWeight = 0.35;

        public double getHazardWeight() { return hazardWeight; }
        public void setHazardWeight(double hazardWeight) { this.hazardWeight = hazardWeight; }
        public double getVulnerabilityWeight() { return vulnerabilityWeight; }
        public void setVulnerabilityWeight(double vulnerabilityWeight) { this.vulnerabilityWeight = vulnerabilityWeight; }
        public double getReportingGapWeight() { return reportingGapWeight; }
        public void setReportingGapWeight(double reportingGapWeight) { this.reportingGapWeight = reportingGapWeight; }
    }

    public static class Response {
        private double severityWeight = 0.50;
        private double vulnerabilityWeight = 0.30;
        private double unmetNeedWeight = 0.20;

        public double getSeverityWeight() { return severityWeight; }
        public void setSeverityWeight(double severityWeight) { this.severityWeight = severityWeight; }
        public double getVulnerabilityWeight() { return vulnerabilityWeight; }
        public void setVulnerabilityWeight(double vulnerabilityWeight) { this.vulnerabilityWeight = vulnerabilityWeight; }
        public double getUnmetNeedWeight() { return unmetNeedWeight; }
        public void setUnmetNeedWeight(double unmetNeedWeight) { this.unmetNeedWeight = unmetNeedWeight; }
    }
}
