import client from './client'
import type {
  DashboardSummary, Village, IncidentReport, RescueResource,
  Recommendation, CoordinatorDecision, AnalysisRun, PagedResponse,
  BaselineComparison
} from '../types'

export const api = {
  // Dashboard
  getDashboardSummary: () =>
    client.get<DashboardSummary>('/dashboard/summary').then(r => r.data),

  // Zones / Villages
  getZones: (hazardType?: string) =>
    client.get<Village[]>('/zones', { params: hazardType ? { hazardType } : {} }).then(r => r.data),
  getZone: (id: number) =>
    client.get<Village>(`/zones/${id}`).then(r => r.data),

  // Incidents
  getIncidents: (page = 0, size = 20) =>
    client.get<PagedResponse<IncidentReport>>('/incidents', { params: { page, size } }).then(r => r.data),
  submitIncident: (data: {
    villageId: number
    incidentType: string
    severity: number
    description?: string
    peopleAffected?: number
    sourceType?: string
  }) => client.post<IncidentReport>('/incidents', data).then(r => r.data),

  // Resources
  getResources: () =>
    client.get<RescueResource[]>('/resources').then(r => r.data),
  getNearbyResources: (lat: number, lon: number) =>
    client.get<RescueResource[]>('/resources/nearby', { params: { lat, lon } }).then(r => r.data),

  // Analysis
  runAnalysis: () =>
    client.post<AnalysisRun>('/analysis/run').then(r => r.data),
  getAnalysisResults: () =>
    client.get<AnalysisRun[]>('/analysis/results').then(r => r.data),

  // Recommendations
  getRecommendations: (type?: string) =>
    client.get<Recommendation[]>('/recommendations', { params: type ? { type } : {} }).then(r => r.data),
  recordDecision: (id: number, data: {
    decision: string
    reason?: string
    assignedResourceId?: number
    overriddenRank?: number
    decidedBy?: string
  }) => client.post<CoordinatorDecision>(`/recommendations/${id}/decision`, data).then(r => r.data),

  // Audit
  getAudit: (page = 0, size = 20) =>
    client.get<PagedResponse<CoordinatorDecision>>('/audit', { params: { page, size } }).then(r => r.data),

  // Baseline
  getBaselineComparison: () =>
    client.get<BaselineComparison>('/baseline/compare').then(r => r.data),
}
