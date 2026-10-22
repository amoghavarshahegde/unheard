export interface ActionRequest {
  agent: string;
  tool: string;
  args: {
    to?: string;
    text?: string;
    rows?: number;
    amount?: number;
    attachments?: string[];
  };
}

export interface Finding {
  type: string;
  value: string;
  reason: string;
  sensitive: boolean;
  start: number;
  end: number;
}

export interface GatewayVerdictResponse {
  id: string;
  agent: string;
  tool: string;
  verdict: 'ALLOW' | 'HOLD' | 'BLOCK';
  score: number;
  reasons: string[];
  findings: Finding[];
  safeOutput: Record<string, any>;
  router: {
    modelCalled: boolean;
    costSavedUsd: number;
    details: string;
  };
  timestamp: string;
}

export interface GatewayStats {
  totalActions: number;
  allowed: number;
  held: number;
  blocked: number;
  killSwitchActive: boolean;
  modelCalls: number;
  callsSaved: number;
  estimatedCostSavedUsd: number;
  chainIntegrityValid: boolean;
}

export interface ApprovalItem {
  id: string;
  actionId: string;
  agent: string;
  tool: string;
  recipient: string;
  score: number;
  reasons: string;
  redactedPayload: string;
  status: 'PENDING' | 'APPROVED' | 'DENIED';
  reviewer?: string;
  reviewedAt?: string;
  createdAt: string;
}

export interface AuditRecordItem {
  id: string;
  sequenceNumber: number;
  prevHash: string;
  hash: string;
  agent: string;
  tool: string;
  verdict: string;
  score: number;
  timestamp: string;
  redactedPayload: string;
  reasons: string;
}

export interface VerificationResult {
  valid: boolean;
  brokenRowId?: string;
  reason: string;
  checkedCount: number;
}

export interface EvalScorecard {
  timestamp: string;
  totalScenarios: number;
  totalAttacks: number;
  totalNormalTasks: number;
  noGatewayBaseline: {
    attacksSucceeded: number;
    attacksTotal: number;
    attacksMitigated: number;
  };
  gatewayResults: {
    blocked: number;
    held: number;
    succeeded: number;
    totalMitigated: number;
    attacksTotal: number;
  };
  normalTaskResults: {
    allowed: number;
    held: number;
    wronglyBlocked: number;
    normalTotal: number;
  };
  misses: Array<{ scenarioId: string; name: string; details: string }>;
  scenarioResults: Array<{
    id: string;
    name: string;
    isAttack: boolean;
    expected: string;
    actual: string;
    score: number;
    matchesExpected: boolean;
    reasons: string[];
  }>;
}

export interface PolicyConfig {
  allowedTools: Record<string, string[]>;
  promptInjectionPatterns: string[];
  internalDomain: string;
  unpermittedToolScore: number;
  promptInjectionScore: number;
  externalRecipientScore: number;
  sensitiveExternalScore: number;
  sensitiveInternalScore: number;
  maxAttachments: number;
  excessiveAttachmentsScore: number;
  deleteTier1MaxRows: number;
  deleteTier1Score: number;
  deleteTier2MaxRows: number;
  deleteTier2Score: number;
  deleteTier3Score: number;
  allowThreshold: number;
  blockThreshold: number;
}

const API_BASE = '/api';

export const api = {
  async evaluateAction(req: ActionRequest): Promise<GatewayVerdictResponse> {
    const res = await fetch(`${API_BASE}/gateway/actions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(req),
    });
    if (!res.ok) throw new Error(`Gateway returned error status: ${res.status}`);
    return res.json();
  },

  async getStats(): Promise<GatewayStats> {
    const res = await fetch(`${API_BASE}/gateway/stats`);
    if (!res.ok) throw new Error('Failed to fetch stats');
    return res.json();
  },

  async getKillSwitch(): Promise<{ active: boolean }> {
    const res = await fetch(`${API_BASE}/policy/killswitch`);
    if (!res.ok) throw new Error('Failed to fetch kill switch state');
    return res.json();
  },

  async toggleKillSwitch(): Promise<{ active: boolean; message: string }> {
    const res = await fetch(`${API_BASE}/policy/killswitch/toggle`, { method: 'POST' });
    if (!res.ok) throw new Error('Failed to toggle kill switch');
    return res.json();
  },

  async getApprovals(status?: string): Promise<ApprovalItem[]> {
    const url = status ? `${API_BASE}/approvals?status=${status}` : `${API_BASE}/approvals`;
    const res = await fetch(url);
    if (!res.ok) throw new Error('Failed to fetch approvals');
    return res.json();
  },

  async approve(id: string, reviewer: string = 'SecurityAdmin'): Promise<any> {
    const res = await fetch(`${API_BASE}/approvals/${id}/approve`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reviewer }),
    });
    if (!res.ok) throw new Error('Failed to approve action');
    return res.json();
  },

  async deny(id: string, reviewer: string = 'SecurityAdmin', reason: string = 'Rejected'): Promise<any> {
    const res = await fetch(`${API_BASE}/approvals/${id}/deny`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reviewer, reason }),
    });
    if (!res.ok) throw new Error('Failed to deny action');
    return res.json();
  },

  async getAuditLog(): Promise<AuditRecordItem[]> {
    const res = await fetch(`${API_BASE}/audit`);
    if (!res.ok) throw new Error('Failed to fetch audit records');
    return res.json();
  },

  async verifyAuditChain(): Promise<VerificationResult> {
    const res = await fetch(`${API_BASE}/audit/verify`);
    if (!res.ok) throw new Error('Failed to verify audit chain');
    return res.json();
  },

  async tamperAuditRecord(id: string): Promise<any> {
    const res = await fetch(`${API_BASE}/audit/tamper/${id}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ tool: 'tampered_exfiltration_tool' }),
    });
    if (!res.ok) throw new Error(`Tamper failed (is dev profile active?): status ${res.status}`);
    return res.json();
  },

  async runEvals(): Promise<EvalScorecard> {
    const res = await fetch(`${API_BASE}/evals/run`, { method: 'POST' });
    if (!res.ok) throw new Error('Failed to run eval benchmark');
    return res.json();
  },

  async addCustomEval(name: string, req: ActionRequest): Promise<any> {
    const res = await fetch(`${API_BASE}/evals/custom`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, isAttack: true, ...req }),
    });
    if (!res.ok) throw new Error('Failed to add custom scenario');
    return res.json();
  },

  async getPolicyConfig(): Promise<PolicyConfig> {
    const res = await fetch(`${API_BASE}/policy/config`);
    if (!res.ok) throw new Error('Failed to fetch policy configuration');
    return res.json();
  },

  async updatePolicyConfig(config: PolicyConfig): Promise<{ config: PolicyConfig; diff: any }> {
    const res = await fetch(`${API_BASE}/policy/config`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(config),
    });
    if (!res.ok) throw new Error('Failed to update policy configuration');
    return res.json();
  },
};
