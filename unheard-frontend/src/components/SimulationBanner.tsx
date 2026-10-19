import React from 'react'
import { Info } from 'lucide-react'

export function SimulationBanner() {
  return (
    <div className="bg-blue-900/20 border border-blue-700/40 rounded-lg px-4 py-2 flex items-center gap-2 text-xs text-blue-300">
      <Info size={14} className="shrink-0" />
      <span>
        <strong>SIMULATED DATA ONLY.</strong> All villages, incidents, and resources are fictional seed data
        created for this hackathon demonstration. This system does not represent real emergencies, real places,
        or real people. Never use for actual emergency response.
      </span>
    </div>
  )
}
