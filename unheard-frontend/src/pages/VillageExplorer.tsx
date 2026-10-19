import React, { useEffect, useState } from 'react'
import { api } from '../api'
import type { Village } from '../types'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'
import { FreshnessIndicator } from '../components/FreshnessIndicator'
import { Eye } from 'lucide-react'

const riskBadge = (riskLevel: string) => {
  const map: Record<string, string> = {
    CRITICAL: 'badge-critical', HIGH: 'badge-high', MEDIUM: 'badge-medium', LOW: 'badge-low'
  }
  return map[riskLevel] ?? 'badge-low'
}

export function VillageExplorer() {
  const [villages, setVillages] = useState<Village[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [hazardFilter, setHazardFilter] = useState('ALL')
  const [riskFilter, setRiskFilter] = useState('ALL')
  const [search, setSearch] = useState('')
  const [silentOnly, setSilentOnly] = useState(false)

  useEffect(() => {
    const ht = hazardFilter === 'ALL' ? undefined : hazardFilter
    api.getZones(ht)
      .then(setVillages)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [hazardFilter])

  const filtered = villages.filter(v => {
    if (search && !v.name.toLowerCase().includes(search.toLowerCase()) && !v.district?.toLowerCase().includes(search.toLowerCase())) return false
    if (riskFilter !== 'ALL' && v.riskLevel !== riskFilter) return false
    if (silentOnly && v.actualReportCount !== 0) return false
    return true
  })

  if (loading) return <LoadingSpinner label="Loading villages..." />
  if (error) return <ErrorBanner error={error} />

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-2xl font-bold text-white">Village & Silent Zone Explorer</h1>
        <p className="text-sm text-gray-400 mt-1">
          Browse monitored areas. Silent zones (zero reports + high hazard score) are prioritized by UNHEARD.
        </p>
      </div>
      <SimulationBanner />

      {/* Filters */}
      <div className="flex flex-wrap gap-3">
        <input
          type="text" placeholder="Search village or district..."
          value={search} onChange={e => setSearch(e.target.value)}
          className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500"
        />
        <select value={hazardFilter} onChange={e => setHazardFilter(e.target.value)}
          className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-200 focus:outline-none focus:border-blue-500">
          <option value="ALL">All Hazards</option>
          <option value="FLOOD">Flood</option>
          <option value="EARTHQUAKE">Earthquake</option>
          <option value="CYCLONE">Cyclone</option>
        </select>
        <select value={riskFilter} onChange={e => setRiskFilter(e.target.value)}
          className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-200 focus:outline-none focus:border-blue-500">
          <option value="ALL">All Risk Levels</option>
          <option value="CRITICAL">Critical</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
        </select>
        <label className="flex items-center gap-2 text-sm text-gray-300 cursor-pointer">
          <input type="checkbox" checked={silentOnly} onChange={e => setSilentOnly(e.target.checked)}
            className="rounded" />
          Silent zones only (0 reports)
        </label>
      </div>

      <div className="text-sm text-gray-500">{filtered.length} of {villages.length} villages shown</div>

      <div className="grid gap-3">
        {filtered.map(v => (
          <div key={v.id} className={`card hover:border-gray-700 transition-colors ${
            v.actualReportCount === 0 && v.hazardRiskScore >= 6 ? 'border-red-800/80 bg-red-950/20' : ''
          }`}>
            <div className="flex items-start justify-between gap-3">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <span className="font-semibold text-white">{v.name}</span>
                  <span className={riskBadge(v.riskLevel)}>{v.riskLevel}</span>
                  {v.actualReportCount === 0 && (
                    <span className="flex items-center gap-1 text-xs text-red-400 bg-red-900/40 px-2 py-0.5 rounded-full border border-red-700/50">
                      <Eye size={10} /> SILENT ZONE
                    </span>
                  )}
                </div>
                <div className="text-xs text-gray-400 mt-1">{v.district} · {v.hazardType}</div>
              </div>
              <div className="text-right shrink-0">
                <div className="text-lg font-bold text-white">{v.hazardRiskScore.toFixed(1)}<span className="text-xs text-gray-500">/10</span></div>
                <div className="text-xs text-gray-500">Hazard Risk</div>
              </div>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mt-3 text-xs">
              <div>
                <div className="text-gray-500">Population</div>
                <div className="text-gray-200 font-medium">{v.population.toLocaleString()}</div>
              </div>
              <div>
                <div className="text-gray-500">Vulnerable</div>
                <div className="text-gray-200 font-medium">{v.vulnerablePopulation.toLocaleString()}</div>
              </div>
              <div>
                <div className="text-gray-500">Reports</div>
                <div className={`font-medium ${
                  v.actualReportCount === 0 ? 'text-red-400' : 'text-gray-200'
                }`}>
                  {v.actualReportCount} / {v.expectedReportVolume} expected
                </div>
              </div>
              <div>
                <div className="text-gray-500">Connectivity</div>
                <div className={`font-medium ${
                  v.connectivityQuality === 'NO_SIGNAL' ? 'text-red-400'
                  : v.connectivityQuality === 'LOW' ? 'text-orange-400' : 'text-gray-200'
                }`}>{v.connectivityQuality}</div>
              </div>
            </div>

            <div className="mt-2 flex items-center gap-3">
              <FreshnessIndicator freshness={v.lastFieldVerification
                ? (Date.now() - new Date(v.lastFieldVerification).getTime() < 6 * 3600000 ? 'GREEN'
                   : Date.now() - new Date(v.lastFieldVerification).getTime() < 24 * 3600000 ? 'YELLOW' : 'RED')
                : 'RED'} />
              <span className="text-xs text-gray-600 truncate">{v.nearbyResources}</span>
            </div>
          </div>
        ))}
        {filtered.length === 0 && (
          <div className="text-center py-12 text-gray-500">No villages match the current filters.</div>
        )}
      </div>
    </div>
  )
}
