package com.agentguard.compliance;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

    private final ComplianceReportService complianceReportService;

    public ComplianceController(ComplianceReportService complianceReportService) {
        this.complianceReportService = complianceReportService;
    }

    @GetMapping("/report")
    public ResponseEntity<ComplianceReportService.ComplianceReport> getJsonReport() {
        return ResponseEntity.ok(complianceReportService.generateReport());
    }

    @GetMapping(value = "/report/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getPrintableHtmlReport() {
        var report = complianceReportService.generateReport();
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='utf-8'><title>")
                .append(report.reportTitle()).append("</title>")
                .append("<style>")
                .append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; padding: 40px; color: #1e293b; max-width: 900px; margin: auto; }")
                .append("h1 { color: #0f172a; border-bottom: 2px solid #e2e8f0; padding-bottom: 12px; }")
                .append(".badge { display: inline-block; padding: 4px 10px; border-radius: 9999px; font-size: 12px; font-weight: 600; }")
                .append(".badge-green { background: #dcfce7; color: #166534; }")
                .append(".stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin: 24px 0; }")
                .append(".stat-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 16px; text-align: center; }")
                .append(".stat-card .val { font-size: 28px; font-weight: 700; color: #0f172a; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 24px; }")
                .append("th, td { border: 1px solid #cbd5e1; padding: 12px; text-align: left; font-size: 14px; }")
                .append("th { background: #f1f5f9; font-weight: 600; }")
                .append(".footer { margin-top: 40px; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; padding-top: 16px; }")
                .append("@media print { body { padding: 0; } }")
                .append("</style></head><body>")
                .append("<h1>").append(report.reportTitle()).append("</h1>")
                .append("<p><strong>Standard:</strong> ").append(report.standard()).append("<br/>")
                .append("<strong>Generated:</strong> ").append(report.generatedAt()).append("<br/>")
                .append("<strong>Cryptographic Hash Chain Integrity:</strong> ")
                .append(report.chainIntegrityValid() ? "<span class='badge badge-green'>VERIFIED OK</span>" : "<span class='badge' style='background:#fee2e2;color:#991b1b'>COMPROMISED</span>")
                .append("</p>")
                .append("<div class='stat-grid'>")
                .append("<div class='stat-card'><div>Total Inspected</div><div class='val'>").append(report.totalAuditRecords()).append("</div></div>")
                .append("<div class='stat-card'><div>Allowed</div><div class='val' style='color:#16a34a'>").append(report.allowedCount()).append("</div></div>")
                .append("<div class='stat-card'><div>Held (Human Approval)</div><div class='val' style='color:#ca8a04'>").append(report.heldCount()).append("</div></div>")
                .append("<div class='stat-card'><div>Blocked</div><div class='val' style='color:#dc2626'>").append(report.blockedCount()).append("</div></div>")
                .append("</div>")
                .append("<h2>OWASP Top 10 for LLM Applications Mapping</h2>")
                .append("<table><thead><tr><th>Control ID</th><th>Risk Category</th><th>Safeguard & Enforcement</th><th>Detections</th><th>Status</th></tr></thead><tbody>");

        for (var c : report.controls()) {
            html.append("<tr>")
                    .append("<td><strong>").append(c.id()).append("</strong></td>")
                    .append("<td>").append(c.name()).append("</td>")
                    .append("<td>").append(c.safeguard()).append("</td>")
                    .append("<td style='font-weight:600;text-align:center;'>").append(c.detectionsCount()).append("</td>")
                    .append("<td><span class='badge badge-green'>").append(c.status()).append("</span></td>")
                    .append("</tr>");
        }

        html.append("</tbody></table>")
                .append("<div class='footer'>Cryptographically sealed by AgentGuard Gateway. Append-only SHA-256 Hash Chain verification active.</div>")
                .append("</body></html>");

        return ResponseEntity.ok(html.toString());
    }
}
