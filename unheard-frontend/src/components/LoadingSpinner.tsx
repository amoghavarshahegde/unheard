import React from 'react'

export function LoadingSpinner({ label = 'Loading...' }: { label?: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 gap-3">
      <div className="w-8 h-8 border-2 border-ops-600 border-t-transparent rounded-full animate-spin" />
      <span className="text-sm text-gray-400">{label}</span>
    </div>
  )
}
