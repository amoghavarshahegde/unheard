import React, { useEffect, useState } from 'react'
import { api } from '../api'
import type { Recommendation, RescueResource } from '../types'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'
import { FreshnessIndicator } from '../components/FreshnessIndicator'
import { ScoreBar } from '../components/ScoreBar'
import { ChevronDown, ChevronUp, Edit2 } from 'lucide-react'

function DecisionModal({ rec, resources, onClose, onSave }: {
  rec: Recommendation; resources: RescueResource[];
  onClose: () => void; onSave: () => void;
}) {
  const [decision, setDecision] = useState(rec.decision?.decision ?? 'APPROVED')
  const [reason, setReason] = useState(rec.decision?.reason ?? '')
  const [resourceId, setResourceId] = useState<number | ''>(rec.decision?.assignedResourceId ?? '')
  const [overriddenRank, setOverriddenRank] = useState<number | ''>(rec.decision?.overriddenRank ?? '')
  const [saving, setSaving] = useState(false)
  const [err, setErr] = useState<string | null>(null)

  const save = async () => {
    setSaving(true); setErr(null)
    try {
      await api.recordDecision(rec.id, {
        decision, reason,
        assignedResourceId: resourceId ? +resourceId : undefined,
        overriddenRank: overriddenRank ? +overriddenRank : undefined,
        decidedBy: 'COORDINATOR'
      })
      onSave()
    } catch (e: any) { setErr(e.message) }
    finally { setSaving(false) }
  }

  return (
    <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center p-4">
      <div className="bg-gray-900 border border-gray-700 rounded-xl w-full max-w-lg p-6 space-y-4">
        <h3 className="font-bold text-white text-lg">Coordinator Decision — {rec.villageName}</h3>
        <div className="text-xs text-gray-400">Rank #{rec.rank} · Score {rec.score} · {rec.recommendationType}</div>

        <div>
          <label className="text-xs text-gray-400 block mb-1">Human Decision *</label>
          <div className="flex gap-2">
            {['APPROVED','REJECTED','OVERRIDDEN'].map(d => (
              <button key={d} onClick={() => setDecision(d)}
                className={`flex-1 text-xs py-2 rounded-lg border font-medium transition-colors ${
                  decision === d
                    ? d === 'APPROVED' ? 'bg-emerald-700 border-emerald-600 text-white'
                      : d === 'REJECTED' ? 'bg-gray-700 border-gray-600 text-white'
                      : 'bg-yellow-700 border-yellow-600 text-white'
                    : 'bg-gray-800 border-gray-700 text-gray-400'
                }`}>{d}</button>
            ))}
          </div>
        </div>

        {decision === 'OVERRIDDEN' && (
          <div>
            <label className="text-xs text-gray-400 block mb-1">Override Assigned Rank</label>
            <input type="number" value={overriddenRank} onChange={e => setOverriddenRank(e.target.value ? +e.target.value : '')}
              className="w-full bg-gray-800 border border-gray-700 rounded px-3 py-2 text-sm text-gray-200" />
          </div>
        )}

        <div>
          <label className="text-xs text-gray-400 block mb-1">Assign Emergency Resource (optional)</label>
          <select value={resourceId} onChange={e => setResourceId(e.target.value ? +e.target.value : '')}
            className="w-full bg-gray-800 border border-gray-700 rounded px-3 py-2 text-sm text-gray-200">
            <option value="">None</option>
            {resources.filter(r => r.availabilityStatus === 'AVAILABLE').map(r =>
              <option key={r.id} value={r.id}>{r.name} ({r.resourceType})</option>
            )}
          </select>
        </div>

        <div>
          <label className="text-xs text-gray-400 block mb-1">Reason / Verification Notes</label>
          <textarea value={reason} onChange={e => setReason(e.target.value)}
            rows={3} className="w-full bg-gray-800 border border-gray-700 rounded px-3 py-2 text-sm text-gray-200"
            placeholder="Document reason for audit history..." />
        </div>

        {err && <div className="text-xs text-red-400">{err}</div>}

        <div className="flex justify-end gap-2">
          <button onClick={onClose} className="btn-ghost text-sm">Cancel</button>
          <button onClick={save} disabled={saving} className="btn-primary text-sm">
            {saving ? 'Saving...' : 'Record Decision'}
          </button>
        </div>
      </div>
    </div>
  )
}

export function Recommendations() {
  const [recs, setRecs] = useState<Recommendation[]>([])
  const [resources, setResources] = useState<RescueResource[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [typeFilter, setTypeFilter] = useState<string>('VERIFICATION_PRIORITY')
  const [expanded, setExpanded] = useState<number | null>(null)
  const [decisionModal, setDecisionModal] = useState<Recommendation | null>(null)

  const load = async () => {
    setLoading(true); setError(null)
    try {
      const [r, res] = await Promise.all([api.getRecommendations(typeFilter), api.getResources()])
      setRecs(r); setResources(res)
    } catch (e: any) { setError(e.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [typeFilter])

  if (loading) return <LoadingSpinner label="Loading recommendations..." />
  if (error) return <ErrorBanner error={error} />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold text-white">Recommendations</h1>
          <p className="text-sm text-gray-400 mt-1">Explainable prioritization rankings from the UNHEARD scoring engine</p>
        </div>
        <div className="flex gap-2">
          {['VERIFICATION_PRIORITY','RESPONSE_PRIORITY'].map(t => (
            <button key={t} onClick={() => setTypeFilter(t)}
              className={`text-xs px-3 py-1.5 rounded-lg font-medium transition-colors ${
                typeFilter === t ? 'bg-blue-600 text-white' : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
              }`}>
              {t === 'VERIFICATION_PRIORITY' ? 'Verification Priority' : 'Response Priority'}
            </button>
          ))}
        </div>
      </div>

      <SimulationBanner />

      {recs.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          No recommendations found for this category. Run an analysis run from Dashboard.
        </div>
      )}

      <div className="space-y-2">
        {recs.map(rec => (
          <div key={rec.id} className={`card transition-colors ${
            rec.decision?.decision === 'APPROVED' ? 'border-emerald-800/50'
            : rec.decision?.decision === 'REJECTED' ? 'border-gray-700/30'
            : ''
          }`}>
            <div className="flex items-center gap-3">
              <div className="w-7 h-7 rounded bg-gray-800 text-xs font-mono flex items-center justify-center text-gray-300 shrink-0">
                {rec.rank}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <span className="font-semibold text-white">{rec.villageName}</span>
                  <span className={`text-xs px-1.5 py-0.5 rounded ${
                    rec.confidenceLevel === 'HIGH' ? 'bg-emerald-900 text-emerald-300'
                    : rec.confidenceLevel === 'MEDIUM' ? 'bg-yellow-900 text-yellow-300'
                    : 'bg-gray-800 text-gray-400'
                  }`}>{rec.confidenceLevel} confidence</span>
                  {rec.decision && (
                    <span className={`text-xs px-2 py-0.5 rounded font-medium ${
                      rec.decision.decision === 'APPROVED' ? 'bg-emerald-900 text-emerald-300'
                      : rec.decision.decision === 'REJECTED' ? 'bg-gray-800 text-gray-400'
                      : 'bg-yellow-900 text-yellow-300'
                    }`}>✓ {rec.decision.decision}</span>
                  )}
                </div>
                <div className="flex items-center gap-3 mt-1">
                  <FreshnessIndicator freshness={rec.freshnessIndicator} />
                  <span className="text-xs text-gray-500 truncate">{rec.keyFactors?.split('|').join(' · ')}</span>
                </div>
              </div>
              <div className="flex items-center gap-3 shrink-0">
                <ScoreBar score={rec.score} className="w-28 hidden sm:flex" />
                <button onClick={() => setDecisionModal(rec)}
                  className="text-xs bg-gray-800 hover:bg-gray-700 px-2 py-1 rounded flex items-center gap-1 text-gray-300">
                  <Edit2 size={12} /> Decide
                </button>
                <button onClick={() => setExpanded(expanded === rec.id ? null : rec.id)}
                  className="text-gray-500 hover:text-gray-300">
                  {expanded === rec.id ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
                </button>
              </div>
            </div>

            {expanded === rec.id && (
              <div className="mt-3 pt-3 border-t border-gray-800 space-y-2">
                <p className="text-sm text-gray-300 leading-relaxed">{rec.explanation}</p>
                {rec.scoreBreakdown && (() => {
                  try {
                    const bd = JSON.parse(rec.scoreBreakdown)
                    return (
                      <div className="text-xs text-gray-400 font-mono bg-gray-950 rounded p-2 overflow-x-auto">
                        <pre>{JSON.stringify(bd, null, 2)}</pre>
                      </div>
                    )
                  } catch { return null }
                })()}
                <div className="text-xs text-blue-400/80 bg-blue-900/10 border border-blue-800/30 rounded px-2 py-1">
                  {rec.dataDisclaimer}
                </div>
              </div>
            )}
          </div>
        ))}
      </div>

      {decisionModal && (
        <DecisionModal
          rec={decisionModal}
          resources={resources}
          onClose={() => setDecisionModal(null)}
          onSave={() => { setDecisionModal(null); load() }}
        />
      )}
    </div>
  )
}
