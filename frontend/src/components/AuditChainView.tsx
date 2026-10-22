import React, { useState, useEffect } from 'react';
import { api, AuditRecordItem, VerificationResult } from '../services/api';
import { Lock, ShieldCheck, ShieldAlert, RefreshCw, AlertTriangle, Bug } from 'lucide-react';

export const AuditChainView: React.FC = () => {
  const [records, setRecords] = useState<AuditRecordItem[]>([]);
  const [verification, setVerification] = useState<VerificationResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [verifying, setVerifying] = useState(false);
  const [tamperingId, setTamperingId] = useState<string | null>(null);

  const fetchRecords = async () => {
    setLoading(true);
    try {
      const data = await api.getAuditLog();
      setRecords(data);
    } catch (err: any) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleVerify = async () => {
    setVerifying(true);
    try {
      const res = await api.verifyAuditChain();
      setVerification(res);
    } catch (err: any) {
      alert('Verification request failed: ' + err.message);
    } finally {
      setVerifying(false);
    }
  };

  const handleTamper = async (id: string) => {
    if (!confirm('This dev action will maliciously modify this row in the database without updating its cryptographic SHA-256 hash. Proceed to test tamper detection?')) {
      return;
    }
    setTamperingId(id);
    try {
      await api.tamperAuditRecord(id);
      await fetchRecords();
      // Re-verify immediately to demonstrate detection
      const verifyRes = await api.verifyAuditChain();
      setVerification(verifyRes);
    } catch (err: any) {
      alert(err.message);
    } finally {
      setTamperingId(null);
    }
  };

  useEffect(() => {
    fetchRecords();
    handleVerify();
  }, []);

  return (
    <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm space-y-6">
      {/* Header & Verification Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 dark:border-slate-800 gap-4">
        <div>
          <div className="flex items-center space-x-2">
            <Lock className="h-5 w-5 text-teal-600" />
            <h2 className="text-base font-bold text-slate-900 dark:text-white">
              Tamper-Evident Audit Hash Chain
            </h2>
          </div>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            Append-only linear sequence. Canonical SHA-256: <code className="text-[11px] text-teal-600 dark:text-teal-400 font-mono">prevHash|id|agent|tool|verdict|score|timestamp</code>. Serialized with lock to prevent branching.
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <button
            onClick={handleVerify}
            disabled={verifying}
            className="flex items-center space-x-1.5 px-3.5 py-1.5 bg-teal-600 hover:bg-teal-700 text-white rounded-lg text-xs font-semibold transition shadow-sm disabled:opacity-50"
          >
            <ShieldCheck className="h-4 w-4" />
            <span>{verifying ? 'Verifying Hashes...' : 'Verify Chain Integrity'}</span>
          </button>
          <button
            onClick={fetchRecords}
            disabled={loading}
            className="p-1.5 rounded-lg border border-slate-300 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 transition"
            title="Refresh"
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Verification status alert banner */}
      {verification && (
        <div
          className={`p-4 rounded-xl border flex items-center justify-between ${
            verification.valid
              ? 'bg-emerald-50 dark:bg-emerald-950/40 border-emerald-300 dark:border-emerald-800 text-emerald-800 dark:text-emerald-300'
              : 'bg-red-50 dark:bg-red-950/60 border-red-300 dark:border-red-800 text-red-800 dark:text-red-300 animate-pulse'
          }`}
        >
          <div className="flex items-center space-x-3 text-xs">
            {verification.valid ? (
              <ShieldCheck className="h-5 w-5 text-emerald-600 shrink-0" />
            ) : (
              <ShieldAlert className="h-5 w-5 text-red-600 shrink-0" />
            )}
            <div>
              <span className="font-bold text-sm block">
                {verification.valid ? 'Cryptographic Integrity Verified' : 'Cryptographic Tampering Detected!'}
              </span>
              <span>
                {verification.reason} (Inspected {verification.checkedCount} block records)
              </span>
              {verification.brokenRowId && (
                <div className="mt-1 font-mono text-[11px] font-bold text-red-700 dark:text-red-400">
                  First broken row ID: {verification.brokenRowId}
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Chain Cards */}
      <div className="space-y-3">
        {records.length === 0 ? (
          <div className="py-12 text-center text-slate-400 text-xs">
            {loading ? 'Loading audit chain...' : 'No audit records yet. Run an action in the sandbox or evals.'}
          </div>
        ) : (
          records.map((r, index) => {
            const isBroken = verification && !verification.valid && verification.brokenRowId === r.id;
            return (
              <div
                key={r.id}
                className={`p-4 rounded-xl border transition ${
                  isBroken
                    ? 'border-red-500 bg-red-50/50 dark:bg-red-950/30'
                    : 'border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-850/40 hover:border-slate-300 dark:hover:border-slate-700'
                }`}
              >
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-2 pb-2 border-b border-slate-200 dark:border-slate-800 text-xs">
                  <div className="flex items-center space-x-2">
                    <span className="px-2 py-0.5 rounded bg-teal-100 dark:bg-teal-950 text-teal-800 dark:text-teal-300 font-mono font-bold">
                      Block #{r.sequenceNumber}
                    </span>
                    <span className="font-semibold text-slate-800 dark:text-slate-200">{r.tool}</span>
                    <span className="text-slate-500 font-mono">({r.agent})</span>
                    <span
                      className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                        r.verdict === 'ALLOW' || r.verdict === 'APPROVED'
                          ? 'bg-emerald-100 dark:bg-emerald-950 text-emerald-800 dark:text-emerald-300'
                          : r.verdict === 'HOLD'
                          ? 'bg-amber-100 dark:bg-amber-950 text-amber-800 dark:text-amber-300'
                          : 'bg-red-100 dark:bg-red-950 text-red-800 dark:text-red-300'
                      }`}
                    >
                      {r.verdict}
                    </span>
                    <span className="text-slate-500">Score: {r.score}</span>
                  </div>

                  <div className="flex items-center space-x-3">
                    <span className="text-slate-400 font-mono text-[11px]">{r.timestamp}</span>
                    {/* Dev-Only Tamper Button */}
                    <button
                      onClick={() => handleTamper(r.id)}
                      disabled={tamperingId === r.id}
                      className="flex items-center space-x-1 px-2 py-1 rounded bg-slate-200 dark:bg-slate-800 hover:bg-red-100 hover:text-red-700 dark:hover:bg-red-950 dark:hover:text-red-300 text-[10px] font-semibold transition text-slate-600 dark:text-slate-300"
                      title="Dev profile: maliciously modify this record in database to demo tamper detection"
                    >
                      <Bug className="h-3 w-3 text-red-500" />
                      <span>Tamper Block</span>
                    </button>
                  </div>
                </div>

                {/* Hashes & Redacted Payload */}
                <div className="mt-2.5 grid grid-cols-1 md:grid-cols-2 gap-2 text-[11px] font-mono text-slate-600 dark:text-slate-400">
                  <div className="p-2 rounded bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 truncate">
                    <span className="text-slate-400 select-none">prev: </span>
                    <span title={r.prevHash}>{r.prevHash}</span>
                  </div>
                  <div className="p-2 rounded bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 truncate">
                    <span className="text-teal-600 dark:text-teal-400 font-bold select-none">hash: </span>
                    <span title={r.hash} className="text-slate-900 dark:text-slate-100 font-semibold">{r.hash}</span>
                  </div>
                </div>

                {r.redactedPayload && (
                  <div className="mt-2 text-[11px] text-slate-500 truncate">
                    <span className="font-semibold text-slate-600 dark:text-slate-400">Stored Payload (Redacted): </span>
                    <span className="font-mono">{r.redactedPayload}</span>
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
