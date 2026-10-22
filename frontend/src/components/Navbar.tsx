import React from 'react';
import { Shield, ShieldAlert, Sun, Moon, Activity, CheckSquare, Eye, Lock, BarChart3, Sliders } from 'lucide-react';

interface NavbarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  darkMode: boolean;
  setDarkMode: (val: boolean) => void;
  killSwitchActive: boolean;
  onToggleKillSwitch: () => void;
  pendingApprovalsCount: number;
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  setActiveTab,
  darkMode,
  setDarkMode,
  killSwitchActive,
  onToggleKillSwitch,
  pendingApprovalsCount,
}) => {
  const tabs = [
    { id: 'sandbox', label: 'Live Gateway & Sandbox', icon: Activity },
    { id: 'approvals', label: 'Approvals Queue', icon: CheckSquare, badge: pendingApprovalsCount },
    { id: 'privacy', label: 'Privacy Lens (PS 2.2)', icon: Eye },
    { id: 'audit', label: 'Audit Hash Chain', icon: Lock },
    { id: 'evals', label: 'Evals Benchmark', icon: BarChart3 },
    { id: 'policy', label: 'Policy & Compliance', icon: Sliders },
  ];

  return (
    <header className="border-b border-slate-200 dark:border-slate-800 bg-white/90 dark:bg-slate-900/90 backdrop-blur sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & Title */}
          <div className="flex items-center space-x-3">
            <div className="h-10 w-10 rounded-xl bg-gradient-to-br from-emerald-500 to-teal-700 flex items-center justify-center text-white shadow-md shadow-emerald-500/20">
              <Shield className="h-6 w-6" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="font-bold text-lg tracking-tight text-slate-900 dark:text-white">AgentGuard</span>
                <span className="text-xs px-2 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-950/80 text-emerald-700 dark:text-emerald-400 font-semibold border border-emerald-200 dark:border-emerald-800">
                  PS 2.1 + 2.2
                </span>
              </div>
              <p className="text-xs text-slate-500 dark:text-slate-400">AI Tool Security Gateway & Privacy Lens</p>
            </div>
          </div>

          {/* Kill Switch & Theme toggle */}
          <div className="flex items-center space-x-4">
            {/* Kill Switch Button */}
            <button
              onClick={onToggleKillSwitch}
              className={`flex items-center space-x-2 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all shadow-sm ${
                killSwitchActive
                  ? 'bg-red-600 hover:bg-red-700 text-white animate-pulse ring-2 ring-red-400'
                  : 'bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 border border-slate-300 dark:border-slate-700'
              }`}
              title="Global Emergency Kill Switch"
            >
              <ShieldAlert className={`h-4 w-4 ${killSwitchActive ? 'text-white' : 'text-red-500'}`} />
              <span>KILL SWITCH: {killSwitchActive ? 'ACTIVE (BLOCK ALL)' : 'STANDBY'}</span>
            </button>

            {/* Dark / Light Toggle */}
            <button
              onClick={() => setDarkMode(!darkMode)}
              className="p-2 rounded-lg bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-600 dark:text-slate-300 transition-colors"
              aria-label="Toggle theme"
            >
              {darkMode ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
            </button>
          </div>
        </div>

        {/* Navigation Tabs */}
        <nav className="flex space-x-1 -mb-px overflow-x-auto py-1 scrollbar-none">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center space-x-2 px-3 py-2 text-xs font-medium rounded-lg transition-colors whitespace-nowrap ${
                  isActive
                    ? 'bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-400 font-semibold'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-100 hover:bg-slate-100 dark:hover:bg-slate-800/60'
                }`}
              >
                <Icon className="h-4 w-4" />
                <span>{tab.label}</span>
                {tab.badge !== undefined && tab.badge > 0 && (
                  <span className="ml-1.5 px-1.5 py-0.2 rounded-full text-[10px] font-bold bg-amber-500 text-white animate-bounce">
                    {tab.badge}
                  </span>
                )}
              </button>
            );
          })}
        </nav>
      </div>
    </header>
  );
};
