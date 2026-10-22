import React, { useState, useEffect } from 'react';
import { ShieldCheck, FileText, ExternalLink, RefreshCw } from 'lucide-react';

export const PolicyComplianceView: React.FC = () => {
  const [report, setReport] = useState<any | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchReport = async () => {
    setLoading(true);
    try {
      const res = await fetch('/api/compliance/report');
      if (res.ok) {
        const data = await res.json();
        setReport(data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReport();
  }, []);

  return (
    <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 dark:border-slate-800 gap-4">
        <div>
          <div className="flex items-center space-x-2">
            <ShieldCheck className="h-5 w-5 text-emerald-600" />
            <h2 className="text-base font-bold text-slate-900 dark:text-white">
              OWASP Top 10 for LLM Applications Governance
            </h2>
          </div>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            Automated alignment mapping to AI security standards with cryptographic audit evidence.
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <a
            href="/api/compliance/report/html"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center space-x-1.5 px-3.5 py-1.5 bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900 rounded-lg text-xs font-semibold hover:opacity-90 transition shadow-sm"
          >
            <FileText className="h-3.5 w-3.5" />
            <span>Export Printable Compliance Report</span>
            <ExternalLink className="h-3 w-3 ml-1" />
          </a>
          <button
            onClick={fetchReport}
            className="p-1.5 rounded-lg border border-slate-300 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 transition"
            title="Refresh"
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {report && (
        <div className="space-y-6">
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <div className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 text-center">
              <span className="text-xs text-slate-500">Total Audit Events</span>
              <div className="text-2xl font-extrabold text-slate-900 dark:text-white mt-1">
                {report.totalAuditRecords}
              </div>
            </div>
            <div className="p-4 rounded-xl border border-emerald-200 dark:border-emerald-950/80 bg-emerald-50/50 dark:bg-emerald-950/30 text-center">
              <span className="text-xs text-emerald-700 dark:text-emerald-400">Total Allowed</span>
              <div className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 mt-1">
                {report.allowedCount}
              </div>
            </div>
            <div className="p-4 rounded-xl border border-amber-200 dark:border-amber-950/80 bg-amber-50/50 dark:bg-amber-950/30 text-center">
              <span className="text-xs text-amber-700 dark:text-amber-400">Total Held (HITL)</span>
              <div className="text-2xl font-extrabold text-amber-600 dark:text-amber-400 mt-1">
                {report.heldCount}
              </div>
            </div>
            <div className="p-4 rounded-xl border border-rose-200 dark:border-rose-950/80 bg-rose-50/50 dark:bg-rose-950/30 text-center">
              <span className="text-xs text-rose-700 dark:text-rose-400">Total Blocked</span>
              <div className="text-2xl font-extrabold text-rose-600 dark:text-rose-400 mt-1">
                {report.blockedCount}
              </div>
            </div>
          </div>

          <div className="space-y-3">
            <h3 className="text-sm font-bold text-slate-900 dark:text-white">Active Controls & Safeguards</h3>
            <div className="space-y-3">
              {report.controls.map((c: any) => (
                <div
                  key={c.id}
                  className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-850/50 flex flex-col md:flex-row md:items-center justify-between gap-3 text-xs"
                >
                  <div className="space-y-1">
                    <div className="flex items-center space-x-2">
                      <span className="font-bold text-slate-900 dark:text-white">{c.id}: {c.name}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-100 dark:bg-emerald-950 text-emerald-800 dark:text-emerald-300">
                        {c.status}
                      </span>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400">{c.safeguard}</p>
                  </div>

                  <div className="text-right shrink-0">
                    <span className="text-slate-500 block text-[11px]">Detections in Log</span>
                    <span className="text-lg font-bold text-slate-900 dark:text-white font-mono">
                      {c.detectionsCount}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
