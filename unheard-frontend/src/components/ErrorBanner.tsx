import React from 'react'
import { AlertCircle } from 'lucide-react'

export function ErrorBanner({ error }: { error: string }) {
  return (
    <div className="bg-emergency-900/40 border border-emergency-700 rounded-xl p-4 flex items-start gap-3">
      <AlertCircle className="text-emergency-400 shrink-0 mt-0.5" size={18} />
      <div>
        <div className="font-medium text-emergency-300">Connection Error</div>
        <div className="text-sm text-emergency-400 mt-1">{error}</div>
        <div className="text-xs text-gray-500 mt-2">
          Ensure the Spring Boot backend is running on port 8080.
          Run: <code className="text-gray-300">mvn spring-boot:run</code> in the unheard-backend directory.
        </div>
      </div>
    </div>
  )
}
