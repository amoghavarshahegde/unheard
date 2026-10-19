import React from 'react'
import clsx from 'clsx'

interface ScoreBarProps {
  score: number
  max?: number
  className?: string
}

export function ScoreBar({ score, max = 100, className }: ScoreBarProps) {
  const pct = Math.min((score / max) * 100, 100)
  const color = pct >= 75 ? 'bg-emergency-500' : pct >= 50 ? 'bg-orange-500' : pct >= 30 ? 'bg-yellow-500' : 'bg-emerald-500'
  return (
    <div className={clsx('flex items-center gap-2', className)}>
      <div className="flex-1 bg-gray-800 rounded-full h-1.5 overflow-hidden">
        <div className={clsx('h-full rounded-full transition-all', color)} style={{ width: `${pct}%` }} />
      </div>
      <span className="text-xs font-mono text-gray-300 w-10 text-right">{score.toFixed(1)}</span>
    </div>
  )
}
