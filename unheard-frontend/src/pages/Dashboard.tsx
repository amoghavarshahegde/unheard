import React, { useEffect, useState } from 'react'
import {
  AlertTriangle, Shield, Eye, Activity,
  Users, Anchor, TrendingUp, RefreshCw, Play
} from 'lucide-react'
import { api } from '../api'
import type { DashboardSummary, AnalysisRun, Recommendation } from '../types'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'
import { ScoreBar } from '../components/ScoreBar'
import { FreshnessIndicator } from '../components/FreshnessIndicator'

function KpiCard({ label, value, sub, icon: Icon, color }: {
  label: string; value: number | string; sub?: string;
  icon: React.ElementType; color: string
}) {
  return (
    <div className="card flex items-start gap-4">
      <div className={`p-2 rounded-lg ${color}`}>
        <Icon size={20} className="text-white" />
      </div>
      <div className="min-w-0">
        <div className="text-2xl font-bold text-white">{value}</div>
        <div className="text-sm text-gray-400">{label}</div>
        {sub && <div className="text-xs text-gray-600 mt-0.5">{sub}</div>}
      </div>
    </div>
  )
}

export function Dashboard() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null)
  const [recs, setRecs] = useState<Recommendation[]>([])
  const [runs, setRuns] = useState<AnalysisRun[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [analyzing, setAnalyzing] = useState(false)
  const [analysisMsg, setAnalysisMsg] = useState<string | null>(null)

  const load = async () => {
    setLoading(true); setError(null)
    try {
      const [s, r, ru] = await Promise.all([
        api.getDashboardSummary(),
        api.getRecommendations('VERIFICATION_PRIORITY'),
        api.getAnalysisResults(),
      ])
      setSummary(s); setRecs(r.slice(0, 8)); setRuns(ru.slice(0, 5))
    } catch (e: any) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleRunAnalysis = async () => {
    setAnalyzing(true); setAnalysisMsg(null)
    try {
      const run = await api.runAnalysis()
      setAnalysisMsg(`Analysis run #${run.id} complete: ${run.totalRecommendations} recommendations generated.`)
      await load()
    } catch (e: any) {
      setAnalysisMsg(`Error: ${e.message}`)
    } finally {
      setAnalyzing(false)
    }
  }

  if (loading) return <LoadingSpinner label="Loading dashboard..." />
  if (error) return <div className="space-y-4"><ErrorBanner error={error} /></div>

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold text-white">Operations Dashboard</h1>
          <p className="text-sm text-gray-400 mt-1">Real-time disaster-response coordination overview</p>
        </div>
        <div className="flex gap-2">
          <button onClick={load} className="btn-ghost flex items-center gap-2 text-sm">
            <RefreshCw size={15} /> Refresh
          </button>
          <button onClick={handleRunAnalysis} disabled={analyzing} className="btn-primary flex items-center gap-2 text-sm">
            <Play size={15} /> {analyzing ? 'Running...' : 'Run Analysis'}
          </button>
        </div>
      </div>

      <SimulationBanner />

      {analysisMsg && (
        <div className="bg-ops-900/40 border border-ops-700/50 rounded-lg px-4 py-2 text-sm text-ops-300">
          {analysisMsg}
        </div>
      )}

      {/* KPI Grid */}
      {summary && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <KpiCard label="Monitored Villages" value={summary.totalVillages} icon={Shield} color="bg-blue-600" />
          <KpiCard label="High Risk Villages" value={summary.highRiskVillages} icon={AlertTriangle} color="bg-orange-600" />
          <KpiCard label="Silent Zones" value={summary.silentZoneVillages}
            sub="High-risk, zero reports" icon={Eye} color="bg-red-600" />
          <KpiCard label="Active Incidents" value={summary.activeIncidents} icon={Activity} color="bg-purple-600" />
          <KpiCard label="Rescue Teams" value={`${summary.availableRescueTeams} avail`}
            sub="of total deployed" icon={Users} color="bg-teal-600" />
          <KpiCard label="Boats" value={`${summary.availableBoats} avail`} icon={Anchor} color="bg-cyan-600" />
          <KpiCard label="Recommendations" value={summary.totalRecommendations} icon={TrendingUp} color="bg-indigo-600" />
          <KpiCard label="Pending Decisions" value={summary.pendingDecisions}
            sub="Awaiting coordinator" icon={RefreshCw} color="bg-gray-700" />
        </div>
      )}

      {/* Top Verification Priority */}
      <div className="grid lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 card">
          <h2 className="font-semibold text-white mb-3 flex items-center gap-2">
            <Eye size={16} className="text-red-400" />
            Top Verification Priority Zones (Unheard Detection)
          </h2>
          {recs.length === 0 ? (
            <p className="text-gray-500 text-sm py-4">No recommendations yet. Run Analysis to generate rankings.</p>
          ) : (
            <div className="space-y-2">
              {recs.map((rec) => (
                <div key={rec.id} className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-800 transition-colors">
                  <div className="w-6 h-6 rounded bg-gray-800 text-xs font-mono flex items-center justify-center text-gray-400 shrink-0">
                    {rec.rank}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="font-medium text-sm text-white truncate">{rec.villageName}</div>
                    <div className="flex items-center gap-2 mt-0.5">
                      <FreshnessIndicator freshness={rec.freshnessIndicator} />
                      <span className="text-xs text-gray-500">{rec.keyFactors?.split('|')[0]}</span>
                    </div>
                  </div>
                  <ScoreBar score={rec.score} className="w-32" />
                  {rec.decision && (
                    <span className={`text-xs px-1.5 py-0.5 rounded font-medium ${
                      rec.decision.decision === 'APPROVED' ? 'bg-emerald-900 text-emerald-300'
                      : rec.decision.decision === 'REJECTED' ? 'bg-gray-800 text-gray-400'
                      : 'bg-yellow-900 text-yellow-300'
                    }`}>{rec.decision.decision}</span>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Recent Analysis Runs */}
        <div className="card">
          <h2 className="font-semibold text-white mb-3 flex items-center gap-2">
            <Activity size={16} className="text-blue-400" />
            Recent Analysis Runs
          </h2>
          {runs.length === 0 ? (
            <p className="text-gray-500 text-sm py-4">No analysis runs yet.</p>
          ) : (
            <div className="space-y-2">
              {runs.map((run) => (
                <div key={run.id} className="card-sm">
                  <div className="flex justify-between items-start">
                    <span className="text-xs font-mono text-gray-400">Run #{run.id}</span>
                    <span className="text-xs text-gray-500">{run.triggerSource}</span>
                  </div>
                  <div className="text-xs text-gray-300 mt-1">
                    {run.villagesAnalyzed} villages · {run.highRiskCount} high-risk · {run.silentZoneCount} silent
                  </div>
                  <div className="text-xs text-gray-600 mt-0.5">
                    {new Date(run.runAt).toLocaleString()}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
