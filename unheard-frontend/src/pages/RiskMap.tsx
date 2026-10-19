import React, { useEffect, useState } from 'react'
import { MapContainer, TileLayer, CircleMarker, Popup } from 'react-leaflet'
import { api } from '../api'
import type { Village, RescueResource } from '../types'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'

const riskColor = (score: number) => {
  if (score >= 8.5) return '#ef4444'
  if (score >= 7.0) return '#f97316'
  if (score >= 5.0) return '#eab308'
  return '#10b981'
}

const resourceColor = (type: string) => {
  if (type === 'RESCUE_TEAM') return '#3b82f6'
  if (type === 'BOAT') return '#06b6d4'
  if (type === 'MEDICAL_UNIT') return '#8b5cf6'
  return '#6b7280'
}

export function RiskMap() {
  const [villages, setVillages] = useState<Village[]>([])
  const [resources, setResources] = useState<RescueResource[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showResources, setShowResources] = useState(true)
  const [filter, setFilter] = useState<string>('ALL')

  useEffect(() => {
    Promise.all([api.getZones(), api.getResources()])
      .then(([v, r]) => { setVillages(v); setResources(r) })
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner label="Loading map data..." />
  if (error) return <ErrorBanner error={error} />

  const filtered = filter === 'ALL' ? villages : villages.filter(v => v.hazardType === filter)

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold text-white">Live Risk Map</h1>
          <p className="text-sm text-gray-400 mt-1">Color-coded village risk levels with emergency resource positions</p>
        </div>
        <div className="flex gap-2 flex-wrap">
          {['ALL', 'FLOOD', 'EARTHQUAKE', 'CYCLONE'].map(f => (
            <button key={f} onClick={() => setFilter(f)}
              className={`text-xs px-3 py-1.5 rounded-lg font-medium transition-colors ${
                filter === f ? 'bg-blue-600 text-white' : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
              }`}>
              {f}
            </button>
          ))}
          <button onClick={() => setShowResources(!showResources)}
            className={`text-xs px-3 py-1.5 rounded-lg font-medium transition-colors ${
              showResources ? 'bg-indigo-600 text-white' : 'bg-gray-800 text-gray-400'
            }`}>
            {showResources ? 'Hide' : 'Show'} Resources
          </button>
        </div>
      </div>

      <SimulationBanner />

      {/* Legend */}
      <div className="flex flex-wrap gap-4 text-xs text-gray-400">
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-red-500 inline-block" /> Critical (&ge;8.5)</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-orange-500 inline-block" /> High (7–8.5)</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-yellow-500 inline-block" /> Medium (5–7)</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-emerald-500 inline-block" /> Low (&lt;5)</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-blue-500 inline-block" /> Rescue Team</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-cyan-500 inline-block" /> Boat</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-purple-500 inline-block" /> Medical</span>
      </div>

      <div className="rounded-xl overflow-hidden border border-gray-800" style={{ height: '60vh' }}>
        <MapContainer
          center={[25.9, 85.8]}
          zoom={7}
          style={{ height: '100%', width: '100%' }}
          className="z-0"
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          {filtered.map(v => (
            <CircleMarker
              key={v.id}
              center={[v.latitude, v.longitude]}
              radius={v.actualReportCount === 0 ? 12 : 8}
              fillColor={riskColor(v.hazardRiskScore)}
              color={v.actualReportCount === 0 ? '#ffffff' : 'transparent'}
              weight={v.actualReportCount === 0 ? 2 : 0}
              fillOpacity={0.85}
            >
              <Popup>
                <div style={{ fontFamily: 'sans-serif', fontSize: '13px', minWidth: '200px' }}>
                  <strong style={{ color: riskColor(v.hazardRiskScore) }}>{v.name}</strong>
                  {v.actualReportCount === 0 && (
                    <div style={{ background: '#7f1d1d', color: '#fca5a5', padding: '2px 6px', borderRadius: '4px', marginTop: '4px', fontSize: '11px' }}>
                      ⚠ ZERO REPORTS — Silent Zone
                    </div>
                  )}
                  <div style={{ marginTop: '6px', lineHeight: '1.6' }}>
                    <div>Hazard: <strong>{v.hazardType}</strong> ({v.hazardRiskScore}/10)</div>
                    <div>Population: {v.population.toLocaleString()}</div>
                    <div>Vulnerable: {v.vulnerablePopulation.toLocaleString()}</div>
                    <div>Reports: {v.actualReportCount} / {v.expectedReportVolume} expected</div>
                    <div>Connectivity: {v.connectivityQuality}</div>
                    <div>District: {v.district}</div>
                  </div>
                  <div style={{ marginTop: '4px', color: '#6b7280', fontSize: '10px' }}>
                    ⚠ Simulated data. Not a real emergency.
                  </div>
                </div>
              </Popup>
            </CircleMarker>
          ))}
          {showResources && resources.map(r => (
            <CircleMarker
              key={`res-${r.id}`}
              center={[r.latitude, r.longitude]}
              radius={6}
              fillColor={resourceColor(r.resourceType)}
              color="white"
              weight={1.5}
              fillOpacity={r.availabilityStatus === 'AVAILABLE' ? 0.9 : 0.4}
            >
              <Popup>
                <div style={{ fontFamily: 'sans-serif', fontSize: '12px' }}>
                  <strong>{r.name}</strong><br />
                  Type: {r.resourceType}<br />
                  Status: <strong>{r.availabilityStatus}</strong><br />
                  Capacity: {r.capacity}<br />
                  {r.currentAssignment && <div>Assigned to: {r.currentAssignment}</div>}
                  <div style={{ color: '#6b7280', fontSize: '10px', marginTop: '4px' }}>Simulated</div>
                </div>
              </Popup>
            </CircleMarker>
          ))}
        </MapContainer>
      </div>
    </div>
  )
}
