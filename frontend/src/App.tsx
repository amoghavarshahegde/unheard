import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { RedTeamSandbox } from './components/RedTeamSandbox';
import { ApprovalsQueue } from './components/ApprovalsQueue';
import { PrivacyLensView } from './components/PrivacyLensView';
import { AuditChainView } from './components/AuditChainView';
import { EvalsScorecardView } from './components/EvalsScorecardView';
import { PolicyComplianceView } from './components/PolicyComplianceView';
import { api, GatewayStats, GatewayVerdictResponse } from './services/api';
import { ShieldAlert, ShieldCheck, DollarSign, Activity } from 'lucide-react';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState('sandbox');
  const [darkMode, setDarkMode] = useState(false);
  const [stats, setStats] = useState<GatewayStats | null>(null);
  const [killSwitchActive, setKillSwitchActive] = useState(false);
  const [pendingApprovalsCount, setPendingApprovalsCount] = useState(0);
  const [lastResponse, setLastResponse] = useState<GatewayVerdictResponse | null>(null);

  // Apply dark mode class to root HTML element
  useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [darkMode]);

  const loadGatewayState = async () => {
    try {
      const s = await api.getStats();
      setStats(s);
      setKillSwitchActive(s.killSwitchActive);

      const approvals = await api.getApprovals('PENDING');
      setPendingApprovalsCount(approvals.length);
    } catch (e) {
      console.error('Failed to load gateway state:', e);
    }
  };

  useEffect(() => {
    loadGatewayState();
    const interval = setInterval(loadGatewayState, 4000);
    return () => clearInterval(interval);
  }, []);

  const handleToggleKillSwitch = async () => {
    try {
      const res = await api.toggleKillSwitch();
      setKillSwitchActive(res.active);
      loadGatewayState();
    } catch (err: any) {
      alert('Error toggling kill switch: ' + err.message);
    }
  };

  const handleActionProcessed = (resp: GatewayVerdictResponse) => {
    setLastResponse(resp);
    loadGatewayState();
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 flex flex-col font-sans transition-colors duration-200">
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        darkMode={darkMode}
        setDarkMode={setDarkMode}
        killSwitchActive={killSwitchActive}
        onToggleKillSwitch={handleToggleKillSwitch}
        pendingApprovalsCount={pendingApprovalsCount}
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
        {/* Real-time stats bar */}
        {stats && (
          <div className="grid grid-cols-2 sm:grid-cols-5 gap-3">
            <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm flex items-center space-x-3">
              <div className="p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
                <Activity className="h-4 w-4" />
              </div>
              <div>
                <span className="text-[11px] text-slate-500 font-medium">Total Inspected</span>
                <div className="text-lg font-extrabold text-slate-900 dark:text-white leading-tight">
                  {stats.totalActions}
                </div>
              </div>
            </div>

            <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm flex items-center space-x-3">
              <div className="p-2 rounded-lg bg-emerald-100 dark:bg-emerald-950 text-emerald-600">
                <ShieldCheck className="h-4 w-4" />
              </div>
              <div>
                <span className="text-[11px] text-emerald-700 dark:text-emerald-400 font-medium">Allowed</span>
                <div className="text-lg font-extrabold text-emerald-600 dark:text-emerald-400 leading-tight">
                  {stats.allowed}
                </div>
              </div>
            </div>

            <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm flex items-center space-x-3">
              <div className="p-2 rounded-lg bg-amber-100 dark:bg-amber-950 text-amber-600">
                <ShieldAlert className="h-4 w-4" />
              </div>
              <div>
                <span className="text-[11px] text-amber-700 dark:text-amber-400 font-medium">Held (HITL)</span>
                <div className="text-lg font-extrabold text-amber-600 dark:text-amber-400 leading-tight">
                  {stats.held}
                </div>
              </div>
            </div>

            <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm flex items-center space-x-3">
              <div className="p-2 rounded-lg bg-rose-100 dark:bg-rose-950 text-rose-600">
                <ShieldAlert className="h-4 w-4" />
              </div>
              <div>
                <span className="text-[11px] text-rose-700 dark:text-rose-400 font-medium">Blocked</span>
                <div className="text-lg font-extrabold text-rose-600 dark:text-rose-400 leading-tight">
                  {stats.blocked}
                </div>
              </div>
            </div>

            <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm flex items-center space-x-3 col-span-2 sm:col-span-1">
              <div className="p-2 rounded-lg bg-teal-100 dark:bg-teal-950 text-teal-600">
                <DollarSign className="h-4 w-4" />
              </div>
              <div>
                <span className="text-[11px] text-teal-700 dark:text-teal-400 font-medium">Cost Saved (est.)</span>
                <div className="text-lg font-extrabold text-teal-600 dark:text-teal-400 leading-tight font-mono">
                  ${stats.estimatedCostSavedUsd.toFixed(4)}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Tab views */}
        {activeTab === 'sandbox' && (
          <RedTeamSandbox onActionProcessed={handleActionProcessed} lastResponse={lastResponse} />
        )}
        {activeTab === 'approvals' && (
          <ApprovalsQueue onApprovalChanged={loadGatewayState} />
        )}
        {activeTab === 'privacy' && <PrivacyLensView />}
        {activeTab === 'audit' && <AuditChainView />}
        {activeTab === 'evals' && <EvalsScorecardView />}
        {activeTab === 'policy' && <PolicyComplianceView />}
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-200 dark:border-slate-800 py-4 bg-white dark:bg-slate-900 text-center text-xs text-slate-500 dark:text-slate-400">
        AgentGuard • Free & Open Source Hackathon Platform (PS 2.1 & PS 2.2) • Cryptographically Sealed with SHA-256 Audit Chain
      </footer>
    </div>
  );
};
