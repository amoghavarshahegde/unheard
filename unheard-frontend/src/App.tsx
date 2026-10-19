import React from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Layout } from './components/Layout'
import { Dashboard } from './pages/Dashboard'
import { RiskMap } from './pages/RiskMap'
import { VillageExplorer } from './pages/VillageExplorer'
import { Incidents } from './pages/Incidents'
import { Recommendations } from './pages/Recommendations'
import { Resources } from './pages/Resources'
import { BaselineComparisonPage } from './pages/BaselineComparison'
import { AuditHistory } from './pages/AuditHistory'
import { FloodReplay } from './pages/FloodReplay'

export default function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/map" element={<RiskMap />} />
          <Route path="/replay" element={<FloodReplay />} />
          <Route path="/villages" element={<VillageExplorer />} />
          <Route path="/incidents" element={<Incidents />} />
          <Route path="/recommendations" element={<Recommendations />} />
          <Route path="/resources" element={<Resources />} />
          <Route path="/baseline" element={<BaselineComparisonPage />} />
          <Route path="/audit" element={<AuditHistory />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  )
}
