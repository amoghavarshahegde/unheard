import React, { useEffect, useState } from 'react'
import { api } from '../api'
import type { BaselineComparison, VillageRankingEntry } from '../types'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Legend } from 'recharts'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'
import { AlertTriangle } from 'lucide-react'

export function BaselineComparisonPage() {
  const [data, setData] = useState<BaselineComparison | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    api.getBaselineComparison()
      .then(setData)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner label="Loading comparison..." />
  if (error) return <ErrorBanner error={error} />
  if (!data) return null

  const { metrics } = data

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Baseline Comparison & Fairness Audit</h1>
        <p className="text-sm text-gray-400 mt-1">
          Comparing a traditional report-volume ranking against UNHEARD explainable verification priority.
        </p>
      </div>

      <SimulationBanner />

      <div className="bg-yellow-900/20 border border-yellow-700/40 rounded-lg px-4 py-3 text-sm text-yellow-300">
        <strong>Disclaimer:</strong> {data.disclaimer}
      </div>

      {/* Metrics summary */}
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="card">
          <div className="text-2xl font-bold text-white">{metrics.totalVillages}</div>
          <div className="text-sm text-gray-400">Total Villages</div>
        </div>
        <div className="card border-red-800">
          <div className="text-2xl font-bold text-red-400">{metrics.highRiskUnderreportedCount}</div>
          <div className="text-sm text-gray-400">Silent High-Risk Zones</div>
        </div>
        <div className="card">
          <div className="text-2xl font-bold text-emerald-400">{metrics.detectedByUnheard}</div>
          <div className="text-sm text-gray-400">Detected by UNHEARD</div>
          <div className="text-xs text-gray-500">{(metrics.unheardDetectionRate * 100).toFixed(0)}% of silent zones surfaced</div>
        </div>
        <div className="card">
          <div className="text-2xl font-bold text-orange-400">{metrics.detectedByBaseline}</div>
          <div className="text-sm text-gray-400">Detected by Baseline</div>
          <div className="text-xs text-gray-500">{(metrics.baselineDetectionRate * 100).toFixed(0)}% (naive volume bias)</div>
        </div>
      </div>

      {/* Chart */}
      <div className="card">
        <h2 className="font-semibold text-white mb-4">Silent High-Risk Zone Detection (Simulated Comparison)</h2>
        <ResponsiveContainer width="100%" height={250}>
          <BarChart data={[
            { method: 'Report Volume Baseline', detected: metrics.detectedByBaseline, missed: metrics.missedByBaseline },
            { method: 'UNHEARD Verification Engine', detected: metrics.detectedByUnheard, missed: metrics.missedByUnheard },
          ]} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
            <XAxis dataKey="method" tick={{ fill: '#9ca3af', fontSize: 13 }} />
            <YAxis tick={{ fill: '#9ca3af', fontSize: 11 }} />
            <Tooltip
              contentStyle={{ background: '#111827', border: '1px solid #374151', color: '#e5e7eb', fontSize: 12 }}
            />
            <Legend wrapperStyle={{ fontSize: 12, color: '#9ca3af' }} />
            <Bar dataKey="detected" name="Detected in Top Half" fill="#10b981" radius={[4,4,0,0]} />
            <Bar dataKey="missed" name="Missed / Underprioritized" fill="#ef4444" radius={[4,4,0,0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Side-by-side rankings */}
      <div className="grid lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="font-semibold text-white mb-3">Baseline Ranking (Report Volume ↓)</h3>
          <div className="text-xs text-gray-500 mb-3">Areas with most reports ranked first. Zero-report zones drop to the bottom!</div>
          <div className="space-y-1">
            {data.baselineRanking.slice(0, 10).map(entry => (
              <RankEntry key={entry.villageId} entry={entry} />
            ))}
          </div>
        </div>
        <div className="card border-blue-900/40">
          <h3 className="font-semibold text-white mb-3">UNHEARD Ranking (Verification Score ↓)</h3>
          <div className="text-xs text-gray-400 mb-3">Areas with highest hazard risk + reporting gaps ranked first. Silent zones surfaced!</div>
          <div className="space-y-1">
            {data.unheardRanking.slice(0, 10).map(entry => (
              <RankEntry key={entry.villageId} entry={entry} />
            ))}
          </div>
        </div>
      </div>

      <div className="text-xs text-gray-500 bg-gray-900 rounded p-3 border border-gray-800">
        <strong>Methodology:</strong> {metrics.methodologyNote}
      </div>
    </div>
  )
}

function RankEntry({ entry }: { entry: VillageRankingEntry }) {
  return (
    <div className={`flex items-center gap-2 p-2 rounded-lg ${
      entry.isSilentHighRisk ? 'bg-red-950/30 border border-red-800/40' : 'hover:bg-gray-800'
    }`}>
      <div className="w-5 text-xs font-mono text-gray-400 text-right shrink-0">{entry.rank}</div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-1.5">
          <span className="text-sm text-white truncate font-medium">{entry.villageName}</span>
          {entry.isSilentHighRisk && (
            <span className="flex items-center gap-0.5 text-xs text-red-400 bg-red-950 px-1.5 py-0.2 rounded border border-red-800">
              <AlertTriangle size={11} className="text-red-400 shrink-0" /> Silent Zone
            </span>
          )}
        </div>
        <div className="text-xs text-gray-400">
          {entry.actualReportCount} reports · Hazard {entry.hazardRiskScore}/10 · {entry.connectivityQuality}
        </div>
      </div>
      <div className="text-xs font-mono text-blue-400 shrink-0">{entry.unheardScore.toFixed(1)}</div>
    </div>
  )
}
