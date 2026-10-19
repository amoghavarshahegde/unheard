export interface Village {
  id: number
  name: string
  latitude: number
  longitude: number
  population: number
  vulnerablePopulation: number
  hazardType: string
  hazardRiskScore: number
  expectedReportVolume: number
  actualReportCount: number
  connectivityQuality: string
  lastFieldVerification: string | null
  nearbyResources: string
  district: string
  simulated: boolean
  riskLevel: string
  verificationScore?: number
  responseScore?: number
  freshnessIndicator?: string
  dataDisclaimer: string
}

export interface IncidentReport {
  id: number
  villageId: number
  villageName: string
  incidentType: string
  severity: number
  description: string
  peopleAffected: number | null
  reportTimestamp: string
  sourceType: string
  verificationStatus: string
  flaggedDuplicate: boolean
  originalReportId: number | null
  dataDisclaimer: string
}

export interface RescueResource {
  id: number
  resourceType: string
  name: string
  latitude: number
  longitude: number
  availabilityStatus: string
  capacity: number
  currentAssignment: string | null
  notes: string
  simulated: boolean
  distanceKmEstimate: number | null
  distanceDisclaimer: string
}

export interface Recommendation {
  id: number
  analysisRunId: number
  villageId: number
  villageName: string
  recommendationType: string
  score: number
  rank: number
  keyFactors: string
  explanation: string
  freshnessIndicator: string
  confidenceLevel: string
  basedOnSimulatedData: boolean
  scoreBreakdown: string
  dataDisclaimer: string
  decision?: CoordinatorDecision
}

export interface CoordinatorDecision {
  id: number
  recommendationId: number
  decision: string
  reason: string
  assignedResourceId: number | null
  overriddenRank: number | null
  decidedAt: string
  decidedBy: string
  originalScore: number
  originalRank: number
}

export interface DashboardSummary {
  totalVillages: number
  highRiskVillages: number
  silentZoneVillages: number
  activeIncidents: number
  availableRescueTeams: number
  availableBoats: number
  totalResources: number
  lastAnalysisRun: string | null
  lastAnalysisRunId: number | null
  totalRecommendations: number
  pendingDecisions: number
  dataDisclaimer: string
}

export interface AnalysisRun {
  id: number
  runAt: string
  triggerSource: string
  villagesAnalyzed: number
  highRiskCount: number
  silentZoneCount: number
  totalRecommendations: number
  notes: string
}

export interface PagedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface BaselineComparison {
  baselineRanking: VillageRankingEntry[]
  unheardRanking: VillageRankingEntry[]
  metrics: ComparisonMetrics
  disclaimer: string
}

export interface VillageRankingEntry {
  rank: number
  villageId: number
  villageName: string
  actualReportCount: number
  unheardScore: number
  hazardType: string
  hazardRiskScore: number
  population: number
  vulnerablePopulation: number
  connectivityQuality: string
  isSilentHighRisk: boolean
  explanation: string
}

export interface ComparisonMetrics {
  totalVillages: number
  highRiskUnderreportedCount: number
  detectedByUnheard: number
  detectedByBaseline: number
  missedByBaseline: number
  missedByUnheard: number
  unheardDetectionRate: number
  baselineDetectionRate: number
  methodologyNote: string
}
