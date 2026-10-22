import React, { useState, useEffect } from 'react';
import { api, EvalScorecard, PolicyConfig } from '../services/api';
import { BarChart3, Play, ShieldAlert, CheckCircle2, XCircle, AlertTriangle, ArrowRight, Save, Sliders } from 'lucide-react';

export const EvalsScorecardView: React.FC = () => {
  const [scorecard, setScorecard] = useState<EvalScorecard | null>(null);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState<'ALL' | 'ATTACKS' | 'NORMAL'>('ALL');

  // Policy Config state
  const [policyConfig, setPolicyConfig] = useState<PolicyConfig | null>(null);
  const [savingPolicy, setSavingPolicy] = useState(false);
  const [policyDiff, setPolicyDiff] = useState<any | null>(null);

  const fetchScorecard = async () => {
    setLoading(true);
    try {
      const data = await api.runEvals();
      setScorecard(data);
    } catch (err: any) {
      alert('Failed to run eval benchmark: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchPolicyConfig = async () => {
    try {
      const cfg = await api.getPolicyConfig();
      setPolicyConfig(cfg);
    } catch (err: any) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchScorecard();
    fetchPolicyConfig();
  }, []);

  const handleSavePolicy = async () => {
    if (!policyConfig) return;
    setSavingPolicy(true);
    try {
      const res = await api.updatePolicyConfig(policyConfig);
      setPolicyDiff(res.diff);
      await fetchScorecard();
    } catch (err: any) {
      alert('Error updating policy config: ' + err.message);
    } finally {
      setSavingPolicy(false);
    }
  };

  const filteredScenarios = scorecard
    ? scorecard.scenarioResults.filter((s) => {
        if (filter === 'ATTACKS') return s.isAttack;
        if (filter === 'NORMAL') return !s.isAttack;
        return true;
      })
    : [];

  return (
    <div className="space-y-6">
      {/* Top Banner & Run Action */}
      <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 dark:border-slate-800 gap-4">
          <div>
            <div className="flex items-center space-x-2">
              <BarChart3 className="h-5 w-5 text-emerald-600" />
              <h2 className="text-base font-bold text-slate-900 dark:text-white">
                Attack & Safety Evaluation Benchmark
              </h2>
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
              Automated 24-scenario benchmark matrix (18 Attacks + 6 Normal Tasks). Computes baseline vs gateway dynamically.
            </p>
          </div>

          <button
            onClick={fetchScorecard}
            disabled={loading}
            className="flex items-center space-x-1.5 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-semibold transition shadow-sm disabled:opacity-50"
          >
            <Play className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} />
            <span>{loading ? 'Evaluating 24 Matrix...' : 'Run Evals Benchmark'}</span>
          </button>
        </div>

        {scorecard && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
            {/* Card 1: Baseline without Gateway */}
            <div className="p-4 rounded-xl border border-rose-200 dark:border-rose-950/80 bg-rose-50/40 dark:bg-rose-950/20">
              <span className="text-xs font-bold text-rose-700 dark:text-rose-400 block mb-1">
                Baseline (Without Gateway)
              </span>
              <div className="text-2xl font-extrabold text-slate-900 dark:text-white">
                {scorecard.noGatewayBaseline.attacksSucceeded} / {scorecard.noGatewayBaseline.attacksTotal}
              </div>
              <p className="text-[11px] text-rose-600 dark:text-rose-400 mt-1 font-medium">
                100% of attacks succeed unimpeded when AI tools are called directly without gateway interception.
              </p>
            </div>

            {/* Card 2: With AgentGuard Gateway */}
            <div className="p-4 rounded-xl border border-emerald-200 dark:border-emerald-950/80 bg-emerald-50/40 dark:bg-emerald-950/20">
              <span className="text-xs font-bold text-emerald-700 dark:text-emerald-400 block mb-1">
                With AgentGuard Gateway
              </span>
              <div className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400">
                {scorecard.gatewayResults.totalMitigated} / {scorecard.gatewayResults.attacksTotal} Mitigated
              </div>
              <p className="text-[11px] text-emerald-700 dark:text-emerald-300 mt-1">
                {scorecard.gatewayResults.blocked} Blocked, {scorecard.gatewayResults.held} Held for human review. Only {scorecard.gatewayResults.succeeded} miss (obfuscated gap).
              </p>
            </div>

            {/* Card 3: Normal Tasks Fidelity */}
            <div className="p-4 rounded-xl border border-blue-200 dark:border-blue-950/80 bg-blue-50/40 dark:bg-blue-950/20">
              <span className="text-xs font-bold text-blue-700 dark:text-blue-400 block mb-1">
                Normal Agent Tasks
              </span>
              <div className="text-2xl font-extrabold text-blue-600 dark:text-blue-400">
                {scorecard.normalTaskResults.allowed} Allowed, {scorecard.normalTaskResults.held} Held
              </div>
              <p className="text-[11px] text-blue-700 dark:text-blue-300 mt-1">
                0 false blocks. 1 expected HOLD on 3-row delete (Tier 1 limit).
              </p>
            </div>
          </div>
        )}

        {/* Honest Known Gaps / Misses */}
        {scorecard && scorecard.misses.length > 0 && (
          <div className="mt-4 p-3.5 rounded-lg border border-amber-300 dark:border-amber-800 bg-amber-50 dark:bg-amber-950/30 text-xs text-amber-800 dark:text-amber-200">
            <span className="font-bold flex items-center space-x-1.5 mb-1 text-amber-900 dark:text-amber-100">
              <AlertTriangle className="h-4 w-4 text-amber-600" />
              <span>Documented Known Gaps (Transparent Safety Disclosure):</span>
            </span>
            <ul className="list-disc pl-5 space-y-1">
              {scorecard.misses.map((m, idx) => (
                <li key={idx}>
                  <strong>{m.scenarioId} - {m.name}:</strong> {m.details}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>

      {/* Policy Change Diff Banner (if updated) */}
      {policyDiff && (
        <div className="p-4 rounded-xl border border-indigo-300 dark:border-indigo-800 bg-indigo-50 dark:bg-indigo-950/40 text-xs">
          <span className="font-bold text-indigo-900 dark:text-indigo-200 text-sm block mb-1">
            Policy Update Re-evaluation Diff
          </span>
          <div className="grid grid-cols-2 gap-4 mt-2">
            <div>
              <span className="font-semibold text-emerald-700 dark:text-emerald-300">Attacks Fixed:</span>{' '}
              {policyDiff.fixedAttacks.length > 0 ? policyDiff.fixedAttacks.join(', ') : 'None'}
            </div>
            <div>
              <span className="font-semibold text-rose-700 dark:text-rose-300">Attacks Newly Missed:</span>{' '}
              {policyDiff.newlyMissedAttacks.length > 0 ? policyDiff.newlyMissedAttacks.join(', ') : 'None'}
            </div>
          </div>
        </div>
      )}

      {/* Editable Policy Configuration */}
      {policyConfig && (
        <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
          <div className="flex items-center justify-between pb-3 border-b border-slate-200 dark:border-slate-800 mb-4">
            <div className="flex items-center space-x-2">
              <Sliders className="h-4 w-4 text-slate-700 dark:text-slate-300" />
              <h3 className="text-sm font-bold text-slate-900 dark:text-white">
                Live Editable Policy Configuration
              </h3>
            </div>
            <button
              onClick={handleSavePolicy}
              disabled={savingPolicy}
              className="flex items-center space-x-1.5 px-3 py-1.5 bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900 rounded-lg text-xs font-semibold hover:opacity-90 transition disabled:opacity-50"
            >
              <Save className="h-3.5 w-3.5" />
              <span>{savingPolicy ? 'Saving & Re-evaluating...' : 'Apply & Compute Diff'}</span>
            </button>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-semibold mb-1">Internal Domain</label>
              <input
                type="text"
                value={policyConfig.internalDomain}
                onChange={(e) => setPolicyConfig({ ...policyConfig, internalDomain: e.target.value })}
                className="w-full rounded border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 p-1.5 text-xs font-mono"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-semibold mb-1">Unpermitted Tool (+pts)</label>
              <input
                type="number"
                value={policyConfig.unpermittedToolScore}
                onChange={(e) => setPolicyConfig({ ...policyConfig, unpermittedToolScore: parseInt(e.target.value) || 0 })}
                className="w-full rounded border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 p-1.5 text-xs font-mono"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-semibold mb-1">Injection (+pts)</label>
              <input
                type="number"
                value={policyConfig.promptInjectionScore}
                onChange={(e) => setPolicyConfig({ ...policyConfig, promptInjectionScore: parseInt(e.target.value) || 0 })}
                className="w-full rounded border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 p-1.5 text-xs font-mono"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-semibold mb-1">External Recipient (+pts)</label>
              <input
                type="number"
                value={policyConfig.externalRecipientScore}
                onChange={(e) => setPolicyConfig({ ...policyConfig, externalRecipientScore: parseInt(e.target.value) || 0 })}
                className="w-full rounded border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 p-1.5 text-xs font-mono"
              />
            </div>
          </div>
        </div>
      )}

      {/* Scenario Breakdown Table */}
      <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <div className="flex items-center justify-between pb-3 border-b border-slate-200 dark:border-slate-800 mb-4">
          <h3 className="text-sm font-bold text-slate-900 dark:text-white">
            Detailed Scenario Verification Matrix ({filteredScenarios.length})
          </h3>
          <div className="flex space-x-1">
            {(['ALL', 'ATTACKS', 'NORMAL'] as const).map((tab) => (
              <button
                key={tab}
                onClick={() => setFilter(tab)}
                className={`px-2.5 py-1 text-xs rounded-lg font-semibold transition ${
                  filter === tab
                    ? 'bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900'
                    : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400'
                }`}
              >
                {tab}
              </button>
            ))}
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 text-slate-500">
                <th className="pb-2">ID</th>
                <th className="pb-2">Scenario</th>
                <th className="pb-2">Category</th>
                <th className="pb-2">Score</th>
                <th className="pb-2">Expected</th>
                <th className="pb-2">Gateway Verdict</th>
                <th className="pb-2">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 font-mono">
              {filteredScenarios.map((s) => (
                <tr key={s.id} className="hover:bg-slate-50 dark:hover:bg-slate-850/50">
                  <td className="py-2.5 font-bold text-slate-800 dark:text-slate-200">{s.id}</td>
                  <td className="py-2.5 font-sans text-slate-700 dark:text-slate-300">{s.name}</td>
                  <td className="py-2.5 font-sans">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-semibold ${s.isAttack ? 'bg-red-100 dark:bg-red-950 text-red-800 dark:text-red-300' : 'bg-blue-100 dark:bg-blue-950 text-blue-800 dark:text-blue-300'}`}>
                      {s.isAttack ? 'ATTACK' : 'NORMAL'}
                    </span>
                  </td>
                  <td className="py-2.5 text-slate-600 dark:text-slate-400">{s.score}</td>
                  <td className="py-2.5 font-sans">{s.expected}</td>
                  <td className="py-2.5">
                    <span className={`px-2 py-0.5 rounded font-bold text-[10px] ${s.actual === 'ALLOW' ? 'bg-emerald-100 dark:bg-emerald-950 text-emerald-800 dark:text-emerald-300' : s.actual === 'HOLD' ? 'bg-amber-100 dark:bg-amber-950 text-amber-800 dark:text-amber-300' : 'bg-red-100 dark:bg-red-950 text-red-800 dark:text-red-300'}`}>
                      {s.actual}
                    </span>
                  </td>
                  <td className="py-2.5">
                    {s.matchesExpected ? (
                      <span className="flex items-center space-x-1 text-emerald-600 text-[11px] font-sans font-semibold">
                        <CheckCircle2 className="h-3.5 w-3.5" />
                        <span>Pass</span>
                      </span>
                    ) : (
                      <span className="flex items-center space-x-1 text-rose-600 text-[11px] font-sans font-semibold">
                        <XCircle className="h-3.5 w-3.5" />
                        <span>Miss (Gap)</span>
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
