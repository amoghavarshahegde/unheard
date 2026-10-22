import React, { useState, useEffect } from 'react';
import { api, ApprovalItem } from '../services/api';
import { CheckSquare, CheckCircle, XCircle, Clock, RefreshCw, AlertCircle } from 'lucide-react';

interface ApprovalsQueueProps {
  onApprovalChanged?: () => void;
}

export const ApprovalsQueue: React.FC<ApprovalsQueueProps> = ({ onApprovalChanged }) => {
  const [approvals, setApprovals] = useState<ApprovalItem[]>([]);
  const [filter, setFilter] = useState<'PENDING' | 'APPROVED' | 'DENIED' | 'ALL'>('PENDING');
  const [loading, setLoading] = useState(false);
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);

  const fetchApprovals = async () => {
    setLoading(true);
    try {
      const data = await api.getApprovals(filter === 'ALL' ? undefined : filter);
      setApprovals(data);
    } catch (err: any) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchApprovals();
  }, [filter]);

  const handleApprove = async (id: string) => {
    setActionLoadingId(id);
    try {
      await api.approve(id, 'SecOps-Admin');
      await fetchApprovals();
      if (onApprovalChanged) onApprovalChanged();
    } catch (err: any) {
      alert('Failed to approve: ' + err.message);
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleDeny = async (id: string) => {
    const reason = prompt('Enter reason for denial:', 'Risk threshold exceeded');
    if (reason === null) return;
    setActionLoadingId(id);
    try {
      await api.deny(id, 'SecOps-Admin', reason);
      await fetchApprovals();
      if (onApprovalChanged) onApprovalChanged();
    } catch (err: any) {
      alert('Failed to deny: ' + err.message);
    } finally {
      setActionLoadingId(null);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 dark:border-slate-800 mb-6 gap-3">
        <div>
          <h2 className="text-base font-bold text-slate-900 dark:text-white flex items-center space-x-2">
            <CheckSquare className="h-5 w-5 text-amber-500" />
            <span>Human-In-The-Loop Approval Queue</span>
          </h2>
          <p className="text-xs text-slate-500 dark:text-slate-400">
            Actions flagged with HOLD (score 40–74) require explicit human authorization before release.
          </p>
        </div>

        <div className="flex items-center space-x-2">
          {/* Status filter tabs */}
          {(['PENDING', 'APPROVED', 'DENIED', 'ALL'] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => setFilter(tab)}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                filter === tab
                  ? 'bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900'
                  : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-700'
              }`}
            >
              {tab}
            </button>
          ))}

          <button
            onClick={fetchApprovals}
            disabled={loading}
            className="p-1.5 rounded-lg border border-slate-300 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 transition"
            title="Refresh"
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {approvals.length === 0 ? (
        <div className="py-12 text-center text-slate-400 text-xs">
          {loading ? 'Loading approvals...' : `No actions in ${filter} status.`}
        </div>
      ) : (
        <div className="space-y-4">
          {approvals.map((item) => (
            <div
              key={item.id}
              className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-850/50 hover:border-slate-300 dark:hover:border-slate-700 transition"
            >
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center space-x-2 mb-1">
                    <span className="font-bold text-sm text-slate-900 dark:text-white">{item.tool}</span>
                    <span className="text-xs px-2 py-0.5 rounded-full bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300 font-mono">
                      {item.agent}
                    </span>
                    <span
                      className={`text-xs px-2.5 py-0.5 rounded-full font-bold ${
                        item.status === 'PENDING'
                          ? 'bg-amber-100 dark:bg-amber-950 text-amber-800 dark:text-amber-300'
                          : item.status === 'APPROVED'
                          ? 'bg-emerald-100 dark:bg-emerald-950 text-emerald-800 dark:text-emerald-300'
                          : 'bg-red-100 dark:bg-red-950 text-red-800 dark:text-red-300'
                      }`}
                    >
                      {item.status}
                    </span>
                    <span className="text-xs font-semibold text-amber-600">Risk Score: {item.score}</span>
                  </div>
                  <div className="text-xs text-slate-500 flex items-center space-x-3">
                    {item.recipient && <span>Target: {item.recipient}</span>}
                    <span className="flex items-center space-x-1">
                      <Clock className="h-3 w-3" />
                      <span>{item.createdAt}</span>
                    </span>
                  </div>
                </div>

                {item.status === 'PENDING' && (
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleApprove(item.id)}
                      disabled={actionLoadingId === item.id}
                      className="flex items-center space-x-1.5 px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-semibold transition shadow-sm disabled:opacity-50"
                    >
                      <CheckCircle className="h-3.5 w-3.5" />
                      <span>Approve</span>
                    </button>
                    <button
                      onClick={() => handleDeny(item.id)}
                      disabled={actionLoadingId === item.id}
                      className="flex items-center space-x-1.5 px-3 py-1.5 bg-red-600 hover:bg-red-700 text-white rounded-lg text-xs font-semibold transition shadow-sm disabled:opacity-50"
                    >
                      <XCircle className="h-3.5 w-3.5" />
                      <span>Deny</span>
                    </button>
                  </div>
                )}
              </div>

              {/* Reasons & Redacted Payload */}
              <div className="mt-3 grid grid-cols-1 md:grid-cols-2 gap-3 text-xs">
                <div className="p-2.5 rounded-lg bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
                  <span className="font-semibold text-slate-700 dark:text-slate-300 block mb-1">Triggered Reasons:</span>
                  <p className="text-slate-600 dark:text-slate-400 font-mono text-[11px]">{item.reasons}</p>
                </div>
                <div className="p-2.5 rounded-lg bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
                  <span className="font-semibold text-slate-700 dark:text-slate-300 block mb-1">
                    Redacted Payload (Safe for release):
                  </span>
                  <p className="text-slate-600 dark:text-slate-400 font-mono text-[11px] truncate">
                    {item.redactedPayload || '(Empty text)'}
                  </p>
                </div>
              </div>

              {item.reviewer && (
                <div className="mt-2 text-[11px] text-slate-500 flex items-center space-x-2">
                  <span className="font-semibold">Reviewed by:</span>
                  <span>{item.reviewer} at {item.reviewedAt}</span>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
