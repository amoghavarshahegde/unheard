import React, { useState, useEffect } from 'react'
import { MapContainer, TileLayer, CircleMarker, Popup, Circle } from 'react-leaflet'
import { api } from '../api'
import type { Village, RescueResource } from '../types'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'
import { Play, Pause, RotateCcw, Sliders, AlertTriangle, ShieldCheck, HelpCircle, Layers } from 'lucide-react'

// Timeline flood scenario frames: T=0h (Normal), T=6h (Rain begins), T=12h (Embankment breach), T=18h (Cell towers collapse - Ghost zones form), T=24h (Critical peak)
interface ReplayFrame {
  hour: number
  label: string
  description: string
  waterLevelRiseMeters: number
  gridPowerStatus: 'NORMAL' | 'DEGRADED' | 'BLACKOUT'
}

const REPLAY_FRAMES: ReplayFrame[] = [
  { hour: 0, label: 'T+00:00 (Onset)', description: 'River gauges cross warning mark. Early cloudburst recorded.', waterLevelRiseMeters: 0.5, gridPowerStatus: 'NORMAL' },
  { hour: 6, label: 'T+06:00 (Surge)', description: 'Precipitation 180mm. Floodwaters enter low-lying river bends.', waterLevelRiseMeters: 1.8, gridPowerStatus: 'NORMAL' },
  { hour: 12, label: 'T+12:00 (Breach)', description: 'Koshi embankment fracture at kilometer 42. Roads cut off.', waterLevelRiseMeters: 3.4, gridPowerStatus: 'DEGRADED' },
  { hour: 18, label: 'T+18:00 (Ghost Zone Onset)', description: 'Substation submerged. 14 cellular base stations go dark. Phone reports abruptly drop to ZERO.', waterLevelRiseMeters: 4.6, gridPowerStatus: 'BLACKOUT' },
  { hour: 24, label: 'T+24:00 (Peak Crisis)', description: 'Peak inundation. Traditional volume systems direct boats only to connected urban zones.', waterLevelRiseMeters: 5.2, gridPowerStatus: 'BLACKOUT' },
]

export function FloodReplay() {
  const [villages, setVillages] = useState<Village[]>([])
  const [resources, setResources] = useState<RescueResource[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // Simulation Timeline State
  const [frameIndex, setFrameIndex] = useState<number>(3) // Default to ghost zone hour T+18
  const [isPlaying, setIsPlaying] = useState<boolean>(false)

  // What-If Boat Allocation Slider State
  const [additionalBoats, setAdditionalBoats] = useState<number>(5)
  const [strategy, setStrategy] = useState<'TRADITIONAL' | 'UNHEARD'>('UNHEARD')

  useEffect(() => {
    Promise.all([api.getZones(), api.getResources()])
      .then(([v, r]) => { setVillages(v); setResources(r) })
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  // Auto-play timer
  useEffect(() => {
    if (!isPlaying) return
    const timer = setInterval(() => {
      setFrameIndex(prev => (prev + 1) % REPLAY_FRAMES.length)
    }, 2800)
    return () => clearInterval(timer)
  }, [isPlaying])

  if (loading) return <LoadingSpinner label="Loading Flood Replay Engine..." />
  if (error) return <ErrorBanner error={error} />

  const currentFrame = REPLAY_FRAMES[frameIndex]

  // Calculate dynamic village states based on replay hour
  const replayVillages = villages.map(v => {
    // If hour >= 18 and low connectivity / high hazard, reports get suppressed into Ghost Zones
    const isGhostProne = v.hazardRiskScore >= 6.5 && (v.connectivityQuality === 'NO_SIGNAL' || v.connectivityQuality === 'LOW')
    const simulatedReports = frameIndex < 3 
      ? Math.max(1, Math.round(v.actualReportCount * ((frameIndex + 1) / 3)))
      : (isGhostProne ? 0 : v.actualReportCount + Math.round(frameIndex * 2.5))
    
    const isGhostZone = frameIndex >= 3 && isGhostProne && simulatedReports === 0

    return {
      ...v,
      currentReports: simulatedReports,
      isGhostZone,
      threatIndex: Math.min(10, Math.round((v.hazardRiskScore + (currentFrame.waterLevelRiseMeters * 0.4)) * 10) / 10)
    }
  })

  // What-If Calculation:
  // Base available boats
  const baseBoats = resources.filter(r => r.resourceType === 'BOAT' && r.availabilityStatus === 'AVAILABLE').length
  const totalFleet = baseBoats + additionalBoats

  // Sort by Traditional (report volume) vs UNHEARD (vulnerability + silence penalty)
  const traditionalAllocation = [...replayVillages]
    .sort((a, b) => b.currentReports - a.currentReports)
    .slice(0, totalFleet)

  const unheardAllocation = [...replayVillages]
    .sort((a, b) => {
      const scoreA = (a.threatIndex * 0.35) + ((a.vulnerablePopulation / a.population) * 30) + (a.isGhostZone ? 45 : 10)
      const scoreB = (b.threatIndex * 0.35) + ((b.vulnerablePopulation / b.population) * 30) + (b.isGhostZone ? 45 : 10)
      return scoreB - scoreA
    })
    .slice(0, totalFleet)

  const activeAssignedVillages = strategy === 'TRADITIONAL' ? traditionalAllocation : unheardAllocation
  const assignedVillageIds = new Set(activeAssignedVillages.map(v => v.id))

  // Population saved metric
  const ghostZonesCovered = activeAssignedVillages.filter(v => v.isGhostZone).length
  const vulnerableLivesProtected = activeAssignedVillages.reduce((sum, v) => sum + v.vulnerablePopulation, 0)

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="px-2.5 py-0.5 rounded text-xs font-bold uppercase tracking-wider bg-red-900/60 text-red-300 border border-red-700">
              Interactive Simulator
            </span>
            <h1 className="text-2xl font-bold text-white">Live Flood Replay & What-If Sandbox</h1>
          </div>
          <p className="text-sm text-gray-400 mt-1">
            Simulate embankment breaches, witness cell towers blackout into <strong>Ghost Zones</strong>, and benchmark Traditional vs. UNHEARD boat deployments.
          </p>
        </div>

        {/* Playback Controls */}
        <div className="flex items-center gap-2 bg-gray-900 border border-gray-800 p-2 rounded-xl">
          <button
            onClick={() => setIsPlaying(!isPlaying)}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-blue-600 hover:bg-blue-500 text-white rounded-lg text-xs font-semibold transition-colors"
          >
            {isPlaying ? <Pause size={14} /> : <Play size={14} />}
            {isPlaying ? 'Pause Replay' : 'Play Replay'}
          </button>
          <button
            onClick={() => { setIsPlaying(false); setFrameIndex(0) }}
            className="p-1.5 text-gray-400 hover:text-white rounded-lg hover:bg-gray-800 transition-colors"
            title="Reset Timeline"
          >
            <RotateCcw size={15} />
          </button>
        </div>
      </div>

      <SimulationBanner />

      {/* Flood Timeline Progress Bar */}
      <div className="card border-blue-900/50 bg-gray-900/80 p-4">
        <div className="flex items-center justify-between mb-2">
          <span className="text-xs font-semibold text-blue-400 uppercase tracking-wider">
            Flood Event Timeline: {currentFrame.label}
          </span>
          <span className="text-xs text-gray-400 font-mono">
            Water Rise: <strong className="text-white">+{currentFrame.waterLevelRiseMeters}m</strong> | Grid Power: <strong className={currentFrame.gridPowerStatus === 'BLACKOUT' ? 'text-red-400' : 'text-emerald-400'}>{currentFrame.gridPowerStatus}</strong>
          </span>
        </div>
        
        {/* Step buttons */}
        <div className="grid grid-cols-5 gap-2 my-2">
          {REPLAY_FRAMES.map((f, i) => (
            <button
              key={f.hour}
              onClick={() => { setIsPlaying(false); setFrameIndex(i) }}
              className={`p-2 text-left rounded-lg border transition-all text-xs ${
                frameIndex === i 
                  ? 'border-blue-500 bg-blue-950/60 text-white shadow-lg shadow-blue-950/50' 
                  : 'border-gray-800 bg-gray-950 text-gray-400 hover:border-gray-700'
              }`}
            >
              <div className="font-bold flex items-center justify-between">
                <span>T+{f.hour < 10 ? '0' + f.hour : f.hour}:00</span>
                {f.hour >= 18 && <span className="text-[10px] text-red-400 font-normal">Ghosting</span>}
              </div>
              <div className="truncate text-[11px] opacity-75 mt-0.5">{f.label.split(' ')[1]}</div>
            </button>
          ))}
        </div>
        <p className="text-xs text-gray-300 mt-2 bg-gray-950/70 p-2.5 rounded border border-gray-800/80">
          <strong className="text-blue-400">Current Phase:</strong> {currentFrame.description}
        </p>
      </div>

      {/* What-If Boat Allocation Controller */}
      <div className="grid lg:grid-cols-3 gap-4">
        <div className="lg:col-span-1 card border-gray-800 bg-gray-900 p-5 space-y-5">
          <div className="flex items-center gap-2 border-b border-gray-800 pb-3">
            <Sliders className="text-cyan-400" size={18} />
            <h3 className="font-semibold text-white text-base">What-If Boat Slider</h3>
          </div>

          <div>
            <div className="flex justify-between text-sm mb-1.5">
              <span className="text-gray-300">Additional Boats to Dispatch:</span>
              <span className="font-bold text-cyan-400 font-mono text-base">+{additionalBoats} Boats</span>
            </div>
            <input
              type="range"
              min="0"
              max="15"
              step="1"
              value={additionalBoats}
              onChange={e => setAdditionalBoats(parseInt(e.target.value))}
              className="w-full accent-cyan-500 bg-gray-800 rounded-lg h-2 cursor-pointer"
            />
            <div className="flex justify-between text-[11px] text-gray-500 mt-1 font-mono">
              <span>0 (Base: {baseBoats})</span>
              <span>Total Fleet: {totalFleet} Boats</span>
              <span>+15 Boats</span>
            </div>
          </div>

          <div>
            <label className="text-xs font-semibold text-gray-400 uppercase tracking-wider block mb-2">
              Dispatch Algorithm Comparison:
            </label>
            <div className="grid grid-cols-2 gap-2">
              <button
                onClick={() => setStrategy('TRADITIONAL')}
                className={`px-3 py-2.5 rounded-lg border text-xs font-semibold text-left transition-all ${
                  strategy === 'TRADITIONAL'
                    ? 'border-orange-500 bg-orange-950/40 text-orange-200'
                    : 'border-gray-800 bg-gray-950 text-gray-400 hover:border-gray-700'
                }`}
              >
                <div className="font-bold">Traditional</div>
                <div className="text-[10px] text-gray-400 mt-0.5">Report-Volume Bias</div>
              </button>

              <button
                onClick={() => setStrategy('UNHEARD')}
                className={`px-3 py-2.5 rounded-lg border text-xs font-semibold text-left transition-all ${
                  strategy === 'UNHEARD'
                    ? 'border-emerald-500 bg-emerald-950/40 text-emerald-200'
                    : 'border-gray-800 bg-gray-950 text-gray-400 hover:border-gray-700'
                }`}
              >
                <div className="font-bold">UNHEARD</div>
                <div className="text-[10px] text-gray-400 mt-0.5">Ghost Zone Equity</div>
              </button>
            </div>
          </div>

          {/* Outcome Metric Card */}
          <div className="bg-gray-950 border border-gray-800 rounded-lg p-3 space-y-3">
            <div className="text-xs font-semibold text-gray-400 uppercase tracking-wider">
              Simulation Outcomes
            </div>
            <div className="flex justify-between items-center text-sm">
              <span className="text-gray-300">Ghost Zones Covered:</span>
              <span className={`font-bold font-mono px-2 py-0.5 rounded text-xs ${
                ghostZonesCovered > 0 ? 'bg-emerald-900/60 text-emerald-300' : 'bg-red-900/60 text-red-300'
              }`}>
                {ghostZonesCovered} Villages
              </span>
            </div>
            <div className="flex justify-between items-center text-sm">
              <span className="text-gray-300">Vulnerable Lives Covered:</span>
              <span className="font-bold text-white font-mono">{vulnerableLivesProtected.toLocaleString()}</span>
            </div>
            <div className="text-[11px] text-gray-400 pt-2 border-t border-gray-800">
              {strategy === 'TRADITIONAL' ? (
                <span className="text-orange-400">
                  ⚠️ <strong>Bias Alert:</strong> Traditional dispatch routes boats only to urban hubs reporting on mobile networks, leaving cut-off areas stranded.
                </span>
              ) : (
                <span className="text-emerald-400">
                  🛡️ <strong>Equity Active:</strong> UNHEARD penalizes reporting drop-offs and routes rescue boats to cut-off high-risk ghost zones.
                </span>
              )}
            </div>
          </div>
        </div>

        {/* Map with Ghost Zones */}
        <div className="lg:col-span-2 card border-gray-800 p-0 overflow-hidden relative" style={{ height: '520px' }}>
          <div className="absolute top-3 right-3 z-[1000] bg-gray-900/90 backdrop-blur border border-gray-800 p-2.5 rounded-lg text-xs space-y-1 text-gray-300">
            <div className="font-bold text-white mb-1">Live Map Legend</div>
            <div className="flex items-center gap-1.5"><span className="w-3 h-3 rounded-full bg-red-600 animate-ping" /> Ghost Zone (0 calls, Critical Flood)</div>
            <div className="flex items-center gap-1.5"><span className="w-3 h-3 rounded-full bg-cyan-400 border border-white" /> Boat Dispatched ({strategy})</div>
            <div className="flex items-center gap-1.5"><span className="w-3 h-3 rounded-full bg-blue-500" /> Monitored Village</div>
          </div>

          <MapContainer center={[25.95, 85.85]} zoom={7} style={{ height: '100%', width: '100%' }}>
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            {replayVillages.map(v => {
              const hasBoat = assignedVillageIds.has(v.id)
              return (
                <React.Fragment key={v.id}>
                  {v.isGhostZone && (
                    <Circle
                      center={[v.latitude, v.longitude]}
                      radius={18000}
                      pathOptions={{ color: '#ef4444', fillColor: '#ef4444', fillOpacity: 0.15, dashArray: '4, 8' }}
                    />
                  )}
                  <CircleMarker
                    center={[v.latitude, v.longitude]}
                    radius={v.isGhostZone ? 13 : hasBoat ? 10 : 7}
                    fillColor={v.isGhostZone ? '#ef4444' : hasBoat ? '#06b6d4' : '#3b82f6'}
                    color={hasBoat ? '#ffffff' : v.isGhostZone ? '#fca5a5' : 'transparent'}
                    weight={hasBoat ? 2.5 : 1}
                    fillOpacity={0.9}
                  >
                    <Popup>
                      <div className="text-xs p-1 min-w-[210px] text-gray-900 font-sans">
                        <strong className="text-sm font-bold text-gray-900">{v.name}</strong>
                        {v.isGhostZone && (
                          <div className="bg-red-700 text-white font-bold px-2 py-0.5 rounded mt-1 text-[11px] animate-pulse">
                            🚨 GHOST ZONE: Zero Calls, High Hazard
                          </div>
                        )}
                        {hasBoat && (
                          <div className="bg-cyan-700 text-white font-bold px-2 py-0.5 rounded mt-1 text-[11px]">
                            ⛵ Rescue Boat Dispatched ({strategy})
                          </div>
                        )}
                        <div className="mt-2 space-y-1">
                          <div>Hazard Score: <strong>{v.threatIndex}/10</strong></div>
                          <div>Actual Calls: <strong>{v.currentReports}</strong> (Expected: {v.expectedReportVolume})</div>
                          <div>Vulnerable Pop: <strong>{v.vulnerablePopulation.toLocaleString()}</strong></div>
                          <div>Signal: <strong>{v.connectivityQuality}</strong></div>
                        </div>
                      </div>
                    </Popup>
                  </CircleMarker>
                </React.Fragment>
              )
            })}
          </MapContainer>
        </div>
      </div>

      {/* Traditional vs UNHEARD Comparison Table */}
      <div className="card border-gray-800 bg-gray-900 p-5">
        <h3 className="font-bold text-white text-base mb-3 flex items-center gap-2">
          <Layers size={18} className="text-blue-400" />
          Fleet Allocation Audit: Where would {totalFleet} rescue boats go?
        </h3>
        <div className="overflow-x-auto">
          <table className="w-full text-xs text-left">
            <thead className="bg-gray-950 text-gray-400 uppercase font-mono">
              <tr>
                <th className="p-3">Rank</th>
                <th className="p-3">Traditional Dispatch (Volume-Based)</th>
                <th className="p-3">Reports</th>
                <th className="p-3">UNHEARD Dispatch (Equity-Based)</th>
                <th className="p-3">Reports</th>
                <th className="p-3">Vulnerable Pop</th>
                <th className="p-3">Equity Assessment</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-800 font-mono">
              {Array.from({ length: Math.min(totalFleet, 10) }).map((_, idx) => {
                const trad = traditionalAllocation[idx]
                const unh = unheardAllocation[idx]
                return (
                  <tr key={idx} className="hover:bg-gray-850">
                    <td className="p-3 font-bold text-gray-400">Boat #{idx + 1}</td>
                    <td className="p-3">
                      <span className="text-white font-sans font-semibold">{trad?.name || '—'}</span>
                      {trad?.isGhostZone && <span className="ml-1 text-[10px] text-red-400"> (Ghost)</span>}
                    </td>
                    <td className="p-3 text-orange-400 font-bold">{trad?.currentReports} calls</td>
                    <td className="p-3">
                      <span className="text-white font-sans font-semibold">{unh?.name || '—'}</span>
                      {unh?.isGhostZone && (
                        <span className="ml-1.5 px-1.5 py-0.5 rounded text-[10px] bg-red-900/60 text-red-300 border border-red-700">
                          GHOST ZONE
                        </span>
                      )}
                    </td>
                    <td className="p-3 text-gray-300">{unh?.currentReports} calls</td>
                    <td className="p-3 text-cyan-400 font-bold">{unh?.vulnerablePopulation.toLocaleString()}</td>
                    <td className="p-3 font-sans">
                      {unh?.isGhostZone ? (
                        <span className="text-emerald-400 font-medium flex items-center gap-1">
                          <ShieldCheck size={13} /> Saved from digital silence
                        </span>
                      ) : (
                        <span className="text-gray-400">Verified critical hazard</span>
                      )}
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
