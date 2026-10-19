import React from 'react'
import clsx from 'clsx'

export function FreshnessIndicator({ freshness }: { freshness: string }) {
  const map: Record<string, { dot: string; label: string }> = {
    GREEN: { dot: 'bg-emerald-500', label: 'Fresh (<6h)' },
    YELLOW: { dot: 'bg-yellow-500', label: 'Stale (6-24h)' },
    RED: { dot: 'bg-red-500 animate-pulse', label: 'Unverified (>24h)' },
  }
  const m = map[freshness] ?? { dot: 'bg-gray-500', label: 'Unknown' }
  return (
    <span className="inline-flex items-center gap-1.5">
      <span className={clsx('inline-block w-2 h-2 rounded-full', m.dot)} />
      <span className="text-xs text-gray-400">{m.label}</span>
    </span>
  )
}
