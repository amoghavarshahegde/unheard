import React, { useEffect, useState } from 'react'
import { api } from '../api'
import type { CoordinatorDecision, PagedResponse } from '../types'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'
import { CheckCircle, XCircle, Edit2 } from 'lucide-react'

const decisionIcon = (d: string) => {
  if (d === 'APPROVED') return <CheckCircle size={16} className="text-emerald-400" />
  if (d === 'REJECTED') return <XCircle size={16} className="text-gray-500" />
  return <Edit2 size={16} className="text-yellow-400" />
}

export function AuditHistory() {
  const [data, setData] = useState<PagedResponse<CoordinatorDecision> | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [page, setPage] = useState(0)

  const load = (p = 0) => {
    setLoading(true)
    api.getAudit(p, 20)
      .then(d => { setData(d); setPage(p) })
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  if (loading) return <LoadingSpinner label="Loading audit history..." />
  if (error) return <ErrorBanner error={error} />

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-2xl font-bold text-white">Coordinator Decision Audit</h1>
        <p className="text-sm text-gray-400 mt-1">
          Complete, accountable history of human coordinator actions. Original scores and ranks are preserved.
        </p>
      </div>
      <SimulationBanner />

      {data?.totalElements === 0 && (
        <div className="text-center py-12 text-gray-500">
          No decisions recorded yet. Go to Recommendations to approve, reject, or override any recommendation.
        </div>
      )}

      <div className="space-y-2">
        {data?.content.map(d => (
          <div key={d.id} className="card">
            <div className="flex items-start gap-3">
              <div className="shrink-0 mt-0.5">{decisionIcon(d.decision)}</div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <span className={`font-semibold text-sm ${
                    d.decision === 'APPROVED' ? 'text-emerald-400'
                    : d.decision === 'REJECTED' ? 'text-gray-400'
                    : 'text-yellow-400'
                  }`}>{d.decision}</span>
                  <span className="text-xs text-gray-400">Rec #{d.recommendationId}</span>
                  {d.overriddenRank && (
                    <span className="text-xs text-yellow-400 font-mono">→ Rank overridden to #{d.overriddenRank}</span>
                  )}
                  {d.assignedResourceId && (
                    <span className="text-xs text-blue-400">Resource #{d.assignedResourceId} assigned</span>
                  )}
                </div>
                {d.reason && <p className="text-sm text-gray-300 mt-1 font-sans">{d.reason}</p>}
                <div className="text-xs text-gray-500 mt-1">
                  By <span className="text-gray-300">{d.decidedBy}</span> · {new Date(d.decidedAt).toLocaleString()}
                  {d.originalScore != null && (
                    <span> · Original engine score: {d.originalScore} (Rank #{d.originalRank})</span>
                  )}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {data && data.totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-4">
          <button disabled={page === 0} onClick={() => load(page - 1)} className="btn-ghost text-sm disabled:opacity-40">
            Previous
          </button>
          <span className="text-sm text-gray-400 self-center">Page {page + 1} of {data.totalPages}</span>
          <button disabled={data.last} onClick={() => load(page + 1)} className="btn-ghost text-sm disabled:opacity-40">
            Next
          </button>
        </div>
      )}
    </div>
  )
}
