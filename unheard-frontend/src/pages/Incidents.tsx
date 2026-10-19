import React, { useEffect, useState } from 'react'
import { api } from '../api'
import type { Village, IncidentReport, PagedResponse } from '../types'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'
import { Copy } from 'lucide-react'

const severityLabel = ['', 'Minor', 'Moderate', 'Serious', 'Critical', 'Catastrophic']
const severityColor = ['', 'text-emerald-400', 'text-yellow-400', 'text-orange-400', 'text-red-400', 'text-red-500']

export function Incidents() {
  const [data, setData] = useState<PagedResponse<IncidentReport> | null>(null)
  const [villages, setVillages] = useState<Village[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [submitMsg, setSubmitMsg] = useState<string | null>(null)
  const [form, setForm] = useState({
    villageId: 0, incidentType: '', severity: 3,
    description: '', peopleAffected: '', sourceType: 'COMMUNITY_MEMBER'
  })

  const load = async () => {
    setLoading(true); setError(null)
    try {
      const [d, v] = await Promise.all([api.getIncidents(), api.getZones()])
      setData(d); setVillages(v)
    } catch (e: any) { setError(e.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!form.villageId || !form.incidentType) {
      setSubmitMsg('Village and incident type are required.'); return
    }
    setSubmitting(true); setSubmitMsg(null)
    try {
      const res = await api.submitIncident({
        villageId: form.villageId,
        incidentType: form.incidentType,
        severity: form.severity,
        description: form.description,
        peopleAffected: form.peopleAffected ? parseInt(form.peopleAffected) : undefined,
        sourceType: form.sourceType
      })
      setSubmitMsg(res.flaggedDuplicate
        ? `Report submitted and flagged as potential duplicate of report #${res.originalReportId}. Kept in system for verification.`
        : `Report #${res.id} submitted successfully.`)
      setShowForm(false)
      setForm({ villageId: 0, incidentType: '', severity: 3, description: '', peopleAffected: '', sourceType: 'COMMUNITY_MEMBER' })
      await load()
    } catch (e: any) { setSubmitMsg(`Error: ${e.message}`) }
    finally { setSubmitting(false) }
  }

  if (loading) return <LoadingSpinner label="Loading incidents..." />
  if (error) return <ErrorBanner error={error} />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Incident Reports</h1>
          <p className="text-sm text-gray-400 mt-1">{data?.totalElements} total disaster reports</p>
        </div>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary text-sm">
          {showForm ? 'Cancel' : '+ Submit Incident Report'}
        </button>
      </div>

      <SimulationBanner />

      {submitMsg && (
        <div className={`rounded-lg px-4 py-2 text-sm border ${
          submitMsg.startsWith('Error') ? 'bg-red-950/40 border-red-700 text-red-300'
          : 'bg-blue-950/40 border-blue-700 text-blue-300'
        }`}>{submitMsg}</div>
      )}

      {showForm && (
        <form onSubmit={handleSubmit} className="card space-y-4">
          <h2 className="font-semibold text-white">New Incident Report</h2>
          <div className="grid sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs text-gray-400 mb-1">Village *</label>
              <select value={form.villageId} onChange={e => setForm(f => ({ ...f, villageId: +e.target.value }))}
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-200" required>
                <option value={0}>Select village...</option>
                {villages.map(v => <option key={v.id} value={v.id}>{v.name}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-xs text-gray-400 mb-1">Incident Type *</label>
              <select value={form.incidentType} onChange={e => setForm(f => ({ ...f, incidentType: e.target.value }))}
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-200" required>
                <option value="">Select type...</option>
                {['FLOOD_INUNDATION','INFRASTRUCTURE_DAMAGE','DISPLACEMENT','STRONG_WINDS','MEDICAL_EMERGENCY','SUPPLY_SHORTAGE'].map(t =>
                  <option key={t} value={t}>{t.replace(/_/g, ' ')}</option>
                )}
              </select>
            </div>
            <div>
              <label className="block text-xs text-gray-400 mb-1">Severity (1–5) — {severityLabel[form.severity]}</label>
              <input type="range" min={1} max={5} value={form.severity}
                onChange={e => setForm(f => ({ ...f, severity: +e.target.value }))}
                className="w-full" />
            </div>
            <div>
              <label className="block text-xs text-gray-400 mb-1">People Affected (optional)</label>
              <input type="number" value={form.peopleAffected}
                onChange={e => setForm(f => ({ ...f, peopleAffected: e.target.value }))}
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-200" />
            </div>
            <div>
              <label className="block text-xs text-gray-400 mb-1">Source Type</label>
              <select value={form.sourceType} onChange={e => setForm(f => ({ ...f, sourceType: e.target.value }))}
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-200">
                {['COMMUNITY_MEMBER','FIELD_OFFICER','SENSOR','SOCIAL_MEDIA','SATELLITE'].map(s =>
                  <option key={s} value={s}>{s.replace(/_/g, ' ')}</option>
                )}
              </select>
            </div>
            <div className="sm:col-span-2">
              <label className="block text-xs text-gray-400 mb-1">Description (optional)</label>
              <textarea value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
                rows={3} maxLength={2000}
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-200" />
            </div>
          </div>
          <div className="flex gap-2">
            <button type="submit" disabled={submitting} className="btn-primary text-sm">
              {submitting ? 'Submitting...' : 'Submit Report'}
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-ghost text-sm">Cancel</button>
          </div>
        </form>
      )}

      <div className="space-y-2">
        {data?.content.map(inc => (
          <div key={inc.id} className={`card hover:border-gray-700 ${
            inc.flaggedDuplicate ? 'border-yellow-800/50 bg-yellow-950/10' : ''
          }`}>
            <div className="flex items-start justify-between gap-3">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <span className="font-medium text-white">{inc.incidentType.replace(/_/g, ' ')}</span>
                  <span className={`text-xs font-bold ${severityColor[inc.severity]}`}>
                    SEV {inc.severity} — {severityLabel[inc.severity]}
                  </span>
                  {inc.flaggedDuplicate && (
                    <span className="flex items-center gap-1 text-xs text-yellow-400 bg-yellow-900/30 px-2 py-0.5 rounded-full border border-yellow-700/40">
                      <Copy size={10} /> Duplicate Suspected (Kept)
                    </span>
                  )}
                  <span className={`text-xs px-2 py-0.5 rounded-full ${
                    inc.verificationStatus === 'VERIFIED'
                      ? 'bg-emerald-900 text-emerald-300 border border-emerald-700'
                      : 'bg-gray-800 text-gray-400 border border-gray-700'
                  }`}>{inc.verificationStatus}</span>
                </div>
                <div className="text-xs text-gray-400 mt-1">
                  <strong>{inc.villageName}</strong> · {inc.sourceType} · {new Date(inc.reportTimestamp).toLocaleString()}
                </div>
                {inc.description && <p className="text-xs text-gray-500 mt-1 line-clamp-2">{inc.description}</p>}
              </div>
              <div className="text-right shrink-0">
                {inc.peopleAffected != null && (
                  <div className="text-sm font-bold text-orange-400">{inc.peopleAffected.toLocaleString()}</div>
                )}
                {inc.peopleAffected != null && <div className="text-xs text-gray-500">affected</div>}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
