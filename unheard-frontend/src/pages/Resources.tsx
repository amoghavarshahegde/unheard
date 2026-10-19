import React, { useEffect, useState } from 'react'
import { api } from '../api'
import type { RescueResource } from '../types'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { ErrorBanner } from '../components/ErrorBanner'
import { SimulationBanner } from '../components/SimulationBanner'
import { Truck, Anchor, Cross, Package } from 'lucide-react'

const typeIcon: Record<string, React.ElementType> = {
  RESCUE_TEAM: Truck, BOAT: Anchor, MEDICAL_UNIT: Cross, SUPPLY_TRUCK: Package
}

const statusColor: Record<string, string> = {
  AVAILABLE: 'text-emerald-400 bg-emerald-900/30 border-emerald-700/50',
  DEPLOYED: 'text-orange-400 bg-orange-900/30 border-orange-700/50',
  MAINTENANCE: 'text-yellow-400 bg-yellow-900/30 border-yellow-700/50',
  UNAVAILABLE: 'text-gray-500 bg-gray-800/30 border-gray-700/50',
}

export function Resources() {
  const [resources, setResources] = useState<RescueResource[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [typeFilter, setTypeFilter] = useState('ALL')
  const [statusFilter, setStatusFilter] = useState('ALL')

  useEffect(() => {
    api.getResources()
      .then(setResources)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner label="Loading resources..." />
  if (error) return <ErrorBanner error={error} />

  const types = ['ALL', ...Array.from(new Set(resources.map(r => r.resourceType)))]
  const filtered = resources.filter(r => {
    if (typeFilter !== 'ALL' && r.resourceType !== typeFilter) return false
    if (statusFilter !== 'ALL' && r.availabilityStatus !== statusFilter) return false
    return true
  })

  const available = resources.filter(r => r.availabilityStatus === 'AVAILABLE').length

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Resource Management</h1>
          <p className="text-sm text-gray-400 mt-1">{available} of {resources.length} rescue resources available</p>
        </div>
      </div>

      <SimulationBanner />

      <div className="text-xs text-gray-400 bg-gray-850 border border-gray-800 rounded px-3 py-2">
        ⚠ Distance estimates are straight-line (Haversine) calculations in km. They are NOT travel-time predictions.
      </div>

      <div className="flex flex-wrap gap-2">
        {types.map(t => (
          <button key={t} onClick={() => setTypeFilter(t)}
            className={`text-xs px-3 py-1.5 rounded-lg font-medium transition-colors ${
              typeFilter === t ? 'bg-blue-600 text-white' : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
            }`}>{t.replace(/_/g, ' ')}</button>
        ))}
        <div className="w-px bg-gray-700" />
        {['ALL','AVAILABLE','DEPLOYED'].map(s => (
          <button key={s} onClick={() => setStatusFilter(s)}
            className={`text-xs px-3 py-1.5 rounded-lg font-medium transition-colors ${
              statusFilter === s ? 'bg-blue-600 text-white' : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
            }`}>{s}</button>
        ))}
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
        {filtered.map(r => {
          const Icon = typeIcon[r.resourceType] ?? Truck
          return (
            <div key={r.id} className={`card flex flex-col gap-2 ${
              r.availabilityStatus !== 'AVAILABLE' ? 'opacity-65' : ''
            }`}>
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-2">
                  <div className="p-1.5 bg-gray-800 rounded-lg">
                    <Icon size={16} className="text-blue-400" />
                  </div>
                  <div>
                    <div className="font-medium text-sm text-white">{r.name}</div>
                    <div className="text-xs text-gray-500">{r.resourceType.replace(/_/g, ' ')}</div>
                  </div>
                </div>
                <span className={`text-xs px-2 py-0.5 rounded border font-medium ${statusColor[r.availabilityStatus] ?? ''}`}>
                  {r.availabilityStatus}
                </span>
              </div>
              <div className="grid grid-cols-2 gap-1 text-xs">
                <div><span className="text-gray-500">Capacity: </span><span className="text-gray-300">{r.capacity}</span></div>
                {r.currentAssignment && (
                  <div className="col-span-2">
                    <span className="text-gray-500">Assigned: </span>
                    <span className="text-orange-400">{r.currentAssignment}</span>
                  </div>
                )}
              </div>
              {r.notes && <p className="text-xs text-gray-500">{r.notes}</p>}
            </div>
          )
        })}
      </div>
    </div>
  )
}
