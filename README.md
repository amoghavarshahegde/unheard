# UNHEARD: Explainable Disaster-Response Decision-Support Platform

> **"Finding the people disaster systems never hear."**

UNHEARD is a full-stack, human-in-the-loop decision-support platform designed for disaster coordinators during floods, earthquakes, and cyclones. It actively counteracts reporting bias — the dangerous tendency of emergency systems to prioritize densely connected areas that generate the most calls, while remote, low-connectivity villages face catastrophic silence.

**Core Axiom:** *Zero reports does never mean zero danger.*

---

## ⚠️ Prototype & Safety Disclaimers

1. **Simulated Seed Data**: All villages, populations, hazard ratings, and incident reports are fictional seed data generated for hackathon demonstration.
2. **Prototype Weights**: The scoring weights (0.35/0.30/0.35 for verification; 0.50/0.30/0.20 for response) are prototype defaults, not operationally validated standards.
3. **Mandatory Human-in-the-Loop**: The platform does **not** automatically dispatch emergency teams or contact live 911/emergency services. Every recommendation must be reviewed, approved, rejected, or overridden by a human coordinator.
4. **Distance Estimates**: All resource distances are straight-line Haversine calculations in kilometers, labeled as estimates and not travel-time predictions.

---

## 🏛️ System Architecture

UNHEARD is built as a high-performance modular monolith with clean separation between persistence, REST endpoints, and scoring services:

```
anti ironman/
├── unheard-backend/                     # Spring Boot 3.4.2 REST API
│   ├── src/main/java/com/unheard/
│   │   ├── config/                      # CORS, OpenAPI Swagger, DataSeeder
│   │   ├── controller/                  # 8 REST Controllers
│   │   ├── domain/                      # 6 JPA Entities (MySQL 8)
│   │   ├── dto/                         # Request / Response DTOs
│   │   ├── exception/                   # GlobalExceptionHandler, ErrorResponse
│   │   ├── repository/                  # Spring Data JPA Repositories
│   │   └── service/                     # Business Services
│   │       └── engine/                  # Isolated Scoring Engine Module
│   │           ├── VerificationScorer.java
│   │           ├── ResponseScorer.java
│   │           ├── BaselineComparator.java
│   │           ├── ScoreComponents.java
│   │           └── ScoringWeights.java
│   └── src/test/java/                   # JUnit 5 + Mockito Unit Test Suite
│
└── unheard-frontend/                    # React 18 + Vite + Tailwind CSS
    ├── src/
    │   ├── api/                         # Axios client with connection error banners
    │   ├── components/                  # ScoreBar, FreshnessIndicator, ErrorBanner, SimulationBanner
    │   ├── pages/                       # 8 Emergency Coordination Views
    │   │   ├── Dashboard.tsx            # KPIs, active runs, top verification priority
    │   │   ├── RiskMap.tsx              # React Leaflet map with color-coded markers
    │   │   ├── VillageExplorer.tsx      # Silent-zone explorer with hazard filters
    │   │   ├── Incidents.tsx            # Incident submission + duplicate detection
    │   │   ├── Recommendations.tsx      # Explainable rankings + Coordinator decision modal
    │   │   ├── Resources.tsx            # Rescue teams, boats, medical units
    │   │   ├── BaselineComparison.tsx   # Report volume vs UNHEARD fairness audit chart
    │   │   └── AuditHistory.tsx         # Immutable coordinator decision audit trail
    │   └── types/                       # TypeScript models
```

---

## 📐 Scoring Engine Mathematics & Logic

The scoring engine operates deterministically with two independent rankings:

### 1. Verification Priority Score (Finding Silent Zones)
Identifies high-risk areas suffering from reporting gaps or stale physical verifications:

$$\text{Verification Score} = 100 \times (0.35 \times \text{HazardRisk} + 0.30 \times \text{Vulnerability} + 0.35 \times \text{ReportingGap})$$

- **HazardRisk** ($0 \dots 1$): Normalized hazard score $\frac{\text{hazardRiskScore}}{10.0}$.
- **Vulnerability** ($0 \dots 1$): Ratio of vulnerable population (children, elderly, disabled) to total population $\frac{\text{vulnerablePopulation}}{\text{population}}$.
- **ReportingGap** ($0 \dots 1$): 
  $$\text{ReportingGap} = 1.0 - \min\left(\frac{\text{actualReports}}{\text{expectedReportVolume}}, 1.0\right)$$
  *Missing Baseline Policy*: If $\text{expectedReportVolume} = 0$, $\text{ReportingGap}$ is set to $1.0$ (maximum uncertainty). This flags unmonitored zones for physical verification without fabricating danger.

### 2. Response Priority Score (Triaging Confirmed Emergencies)
Prioritizes active, verified incidents according to human need and response deficit:

$$\text{Response Score} = 100 \times (0.50 \times \text{IncidentSeverity} + 0.30 \times \text{Vulnerability} + 0.20 \times \text{UnmetResponseNeed})$$

- **IncidentSeverity** ($0 \dots 1$): Normalized severity level $\frac{\text{severity} - 1}{4.0}$ for severity $1 \dots 5$.
- **Vulnerability** ($0 \dots 1$): $\frac{\text{vulnerablePopulation}}{\text{population}}$.
- **UnmetResponseNeed** ($0 \dots 1$): $0.5$ if rescue resource is already assigned; otherwise $\min\left(\frac{\text{peopleAffected}}{\text{population}}, 1.0\right)$.

### 3. Data Freshness & Confidence Indicators
- **Freshness**:
  - `GREEN`: Verified within $< 6$ hours
  - `YELLOW`: Verified $6 \dots 24$ hours ago
  - `RED`: Never verified or $> 24$ hours ago
- **Confidence**: `HIGH`, `MEDIUM`, or `LOW` based on baseline availability, connectivity quality (`NO_SIGNAL`, `LOW`, `MEDIUM`, `HIGH`), and verification freshness.

---

## 📡 REST API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/dashboard/summary` | Dashboard KPI counts, active incidents, available assets |
| `GET` | `/api/zones` | All villages (`?hazardType=FLOOD\|EARTHQUAKE\|CYCLONE`) |
| `GET` | `/api/zones/{id}` | Single village detail with risk level |
| `GET` | `/api/incidents` | Paginated incident list (`?page=0&size=20`) |
| `POST` | `/api/incidents` | Submit report (triggers duplicate detection) |
| `GET` | `/api/resources` | Rescue resources (teams, boats, medical units) |
| `GET` | `/api/resources/nearby` | Nearest available resources (`?lat=...&lon=...`) |
| `POST` | `/api/analysis/run` | Trigger UNHEARD deterministic scoring run |
| `GET` | `/api/analysis/results` | Historical analysis runs |
| `GET` | `/api/recommendations` | Latest recommendations (`?type=VERIFICATION_PRIORITY\|RESPONSE_PRIORITY`) |
| `POST` | `/api/recommendations/{id}/decision` | Coordinator action: `APPROVED`, `REJECTED`, or `OVERRIDDEN` |
| `GET` | `/api/audit` | Paginated decision audit log |
| `GET` | `/api/baseline/compare` | Report-volume baseline vs UNHEARD simulation comparison |
| `GET` | `/swagger-ui.html` | Interactive OpenAPI 3 documentation |

---

## 🚀 Step-by-Step Local Run Guide

### Prerequisites
- **Java 17, 21, or 24** (`C:\Program Files\Java\jdk-24`)
- **Maven 3.9+** (bundled at `backend/apache-maven-3.9.6/bin/mvn.cmd`)
- **MySQL 8.0** running on `localhost:3306` (service `MySQL80`)
- **Node.js 18+ & npm**

### 1. Database Setup
The backend automatically connects to MySQL. If not already created:
```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p"your_password" -e "CREATE DATABASE IF NOT EXISTS unheard_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 2. Start Spring Boot Backend
In a terminal:
```powershell
cd "c:\Users\Amoghavarsha Hegde\Downloads\anti ironman\unheard-backend"
& "..\backend\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
```
*Backend starts on `http://localhost:8080`. Seed data and initial analysis run automatically!*

### 3. Start React Frontend
In a second terminal:
```powershell
cd "c:\Users\Amoghavarsha Hegde\Downloads\anti ironman\unheard-frontend"
npm run dev
```
*Frontend starts on `http://localhost:5173`.*

---

## 🧪 Automated Test Verification

Run all unit tests:
```powershell
& "..\backend\apache-maven-3.9.6\bin\mvn.cmd" -f "unheard-backend\pom.xml" test
```
**Test Results: 18 Tests Run, 0 Failures, 0 Errors, 0 Skipped.**
Covers:
- Hazard normalization, vulnerability normalization, reporting gap bounds
- Zero-report high-risk village prioritization
- Missing baseline uncertainty handling
- Verification freshness transitions (`GREEN` $\rightarrow$ `YELLOW` $\rightarrow$ `RED`)
- Response scorer unmet-need calculation
- Independence between Verification and Response priorities
- Baseline comparison and silent-zone detection metrics

---

## 🎤 3-Minute Hackathon Demonstration Script

1. **The Hook (Dashboard)**:
   - Open `http://localhost:5173`. Point out **Silent Zones: 3** KPI.
   - Explain: *"Traditional systems direct 100% of help to Samastipur or Hajipur because they have 30+ reports. But Majhaulia Tola and Kalyanpur have 0 reports because their towers are down."*

2. **The Map (Risk Map)**:
   - Navigate to **Live Risk Map**. Show the red circles with white pulse outlines.
   - Click on **Majhaulia Tola**: Hazard 9.1/10, Population 4,500, Reports 0/0 (No baseline), NO_SIGNAL.
   - Show how nearby rescue boats and NDRF teams are visually located.

3. **The Proof (Baseline Comparison)**:
   - Navigate to **Baseline Comparison**.
   - Show the Bar Chart: The traditional Report-Volume method misses **100%** of silent high-risk zones (ranking them in the bottom half).
   - UNHEARD surfaces **100%** of them in the top verification priorities.

4. **Human Accountability (Recommendations & Decisions)**:
   - Navigate to **Recommendations**.
   - Expand **Majhaulia Tola**: Read the human-readable explanation and transparent mathematical factor breakdown.
   - Click **Decide**: Approve the recommendation, assign **Boat-02**, record note: *"Dispatching drone survey and boat team for physical confirmation."*
   - Navigate to **Audit History**: Show the immutable audit trail recording the coordinator, original score, and assigned asset.
