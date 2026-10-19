import React, { useState } from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import {
  LayoutDashboard, Map, Globe, FileText, Target,
  Truck, BarChart2, History, AlertTriangle, Menu, X, Radio
} from 'lucide-react'
import clsx from 'clsx'

const navItems = [
  { to: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/map', icon: Map, label: 'Risk Map' },
  { to: '/replay', icon: Radio, label: 'Flood Replay & What-If' },
  { to: '/villages', icon: Globe, label: 'Villages' },
  { to: '/incidents', icon: FileText, label: 'Incidents' },
  { to: '/recommendations', icon: Target, label: 'Recommendations' },
  { to: '/resources', icon: Truck, label: 'Resources' },
  { to: '/baseline', icon: BarChart2, label: 'Baseline' },
  { to: '/audit', icon: History, label: 'Audit' },
]

interface LayoutProps { children: React.ReactNode }

export function Layout({ children }: LayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false)

  return (
    <div className="flex h-screen bg-gray-950 overflow-hidden">
      {/* Sidebar */}
      <aside className={clsx(
        'fixed inset-y-0 left-0 z-50 w-64 bg-gray-900 border-r border-gray-800 flex flex-col transition-transform duration-300',
        sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
      )}>
        {/* Logo */}
        <div className="p-4 border-b border-gray-800">
          <div className="flex items-center gap-2">
            <AlertTriangle className="text-emergency-500" size={24} />
            <div>
              <div className="font-bold text-lg tracking-wider text-white">UNHEARD</div>
              <div className="text-xs text-gray-400">Disaster Response DSS</div>
            </div>
          </div>
          <div className="mt-2 text-xs text-yellow-500/80 bg-yellow-900/20 border border-yellow-700/30 rounded px-2 py-1">
            ⚠️ SIMULATED DATA — Not a real emergency system
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 p-3 space-y-0.5 overflow-y-auto">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              onClick={() => setSidebarOpen(false)}
              className={({ isActive }) => clsx(
                'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                isActive
                  ? 'bg-ops-700 text-white'
                  : 'text-gray-400 hover:text-gray-100 hover:bg-gray-800'
              )}
            >
              <Icon size={18} />
              {label}
            </NavLink>
          ))}
        </nav>

        {/* Footer */}
        <div className="p-3 border-t border-gray-800 text-xs text-gray-600">
          UNHEARD MVP v1.0 · Hackathon Demo
        </div>
      </aside>

      {/* Mobile overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/60 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Main */}
      <div className="flex-1 flex flex-col lg:ml-64 min-w-0">
        {/* Top bar */}
        <header className="h-14 bg-gray-900 border-b border-gray-800 flex items-center px-4 gap-4 shrink-0">
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="lg:hidden text-gray-400 hover:text-white"
          >
            {sidebarOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
          <div className="flex-1 text-sm text-gray-400">
            Finding the people disaster systems never hear.
          </div>
          <div className="text-xs font-mono text-emerald-500 bg-emerald-900/30 px-2 py-1 rounded">
            ● LIVE
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-auto p-4 lg:p-6">
          {children}
        </main>
      </div>
    </div>
  )
}
