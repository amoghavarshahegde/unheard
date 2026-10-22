import React, { useState } from 'react';
import { api, ActionRequest, GatewayVerdictResponse, Finding } from '../services/api';
import { Eye, ShieldCheck, Tag, ArrowRight, Play } from 'lucide-react';

export const PrivacyLensView: React.FC = () => {
  const presets = [
    {
      title: 'Helpline Cue vs Customer Phone',
      to: 'partner@external.org',
      text: 'For 24/7 technical support, please call our toll-free helpline: +91 9876543210. For customer Alice, reach her personal number 9871234567.',
    },
    {
      title: 'PAN & Aadhaar Identification KYC',
      to: 'consultant@external-audit.com',
      text: 'Verified client tax profile with PAN ABCDE1234F and Aadhaar number 1234 5678 9012 submitted for regulatory filing.',
    },
    {
      title: 'Executive Salary Exfiltration',
      to: 'headhunter@recruiting-firm.com',
      text: 'Engineering director candidate package includes CTC of 45 LPA with 20% annual performance incentive.',
    },
    {
      title: 'Internal Route (Zero Redaction)',
      to: 'hr@company.com',
      text: 'Internal onboarding: candidate phone is +91 9876543210 with offered salary Rs 18,00,000.',
    },
  ];

  const [to, setTo] = useState(presets[0].to);
  const [text, setText] = useState(presets[0].text);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<GatewayVerdictResponse | null>(null);

  const handleTest = async (overrideTo?: string, overrideText?: string) => {
    const targetTo = overrideTo !== undefined ? overrideTo : to;
    const targetText = overrideText !== undefined ? overrideText : text;

    setLoading(true);
    try {
      const req: ActionRequest = {
        agent: 'DocBot',
        tool: 'send_email',
        args: {
          to: targetTo,
          text: targetText,
        },
      };
      const res = await api.evaluateAction(req);
      setResult(res);
    } catch (err: any) {
      alert('Error testing privacy lens: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const getBadgeForFinding = (finding: Finding) => {
    if (!finding.sensitive) {
      return (
        <span className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-100 dark:bg-blue-950 text-blue-800 dark:text-blue-300 border border-blue-200 dark:border-blue-800">
          <Tag className="h-3 w-3" />
          <span>PUBLIC: {finding.reason}</span>
        </span>
      );
    }
    return (
      <span className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-100 dark:bg-rose-950 text-rose-800 dark:text-rose-300 border border-rose-200 dark:border-rose-800">
        <Tag className="h-3 w-3" />
        <span>SENSITIVE: {finding.type} ({finding.reason})</span>
      </span>
    );
  };

  return (
    <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm space-y-6">
      {/* Header */}
      <div className="pb-4 border-b border-slate-200 dark:border-slate-800">
        <div className="flex items-center space-x-2">
          <Eye className="h-5 w-5 text-indigo-500" />
          <h2 className="text-base font-bold text-slate-900 dark:text-white">
            Context-Aware Privacy Lens (PS 2.2 Module)
          </h2>
        </div>
        <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
          Detects Phone numbers, PAN cards, Aadhaar (12 digits), and Salary/CTC mentions.
          Uses context window heuristics: phone numbers within 45 characters of public cues
          (<code className="text-indigo-600 dark:text-indigo-400">helpline, toll-free, support, reception, customer care, office</code>)
          are classified as <strong>PUBLIC</strong> and left intact. Sensitive values on external egress are replaced with <code className="text-rose-600 dark:text-rose-400">[REDACTED:TYPE]</code>.
        </p>
      </div>

      {/* Preset interactive pills */}
      <div>
        <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-2">
          Quick-Load Hackathon Test Cases:
        </label>
        <div className="flex flex-wrap gap-2">
          {presets.map((preset, idx) => (
            <button
              key={idx}
              onClick={() => {
                setTo(preset.to);
                setText(preset.text);
                handleTest(preset.to, preset.text);
              }}
              className="text-xs px-3 py-1.5 rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 hover:border-indigo-400 dark:hover:border-indigo-500 text-slate-700 dark:text-slate-300 transition"
            >
              {preset.title}
            </button>
          ))}
        </div>
      </div>

      {/* Editor & Run */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
            Recipient Email
          </label>
          <input
            type="text"
            value={to}
            onChange={(e) => setTo(e.target.value)}
            className="w-full text-xs rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 p-2.5 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500"
            placeholder="partner@external.org or team@company.com"
          />
        </div>
        <div className="md:col-span-2">
          <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
            Payload Text
          </label>
          <div className="flex space-x-2">
            <textarea
              rows={2}
              value={text}
              onChange={(e) => setText(e.target.value)}
              className="w-full text-xs font-mono rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 p-2.5 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500"
            />
            <button
              onClick={() => handleTest()}
              disabled={loading}
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-xs font-semibold flex items-center space-x-1.5 shrink-0 shadow-sm disabled:opacity-50"
            >
              <Play className="h-3.5 w-3.5" />
              <span>{loading ? 'Inspecting...' : 'Analyze Lens'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Side-by-side comparison inspection */}
      {result && (
        <div className="space-y-4 pt-4 border-t border-slate-200 dark:border-slate-800">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Original Text */}
            <div className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/50">
              <span className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-2">
                1. Original Input Payload
              </span>
              <div className="p-3 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-700 text-xs font-mono text-slate-800 dark:text-slate-200 break-words">
                {text}
              </div>
            </div>

            {/* Redacted / Safe Output */}
            <div className="p-4 rounded-xl border border-emerald-200 dark:border-emerald-900/60 bg-emerald-50/40 dark:bg-emerald-950/20">
              <span className="text-xs font-bold text-emerald-800 dark:text-emerald-300 block mb-2 flex items-center justify-between">
                <span>2. Gateway Redacted Safe Output</span>
                <span className="text-[11px] font-normal">
                  {to.endsWith('company.com') ? '(Internal Route: Preserved)' : '(External Egress: Redacted)'}
                </span>
              </span>
              <div className="p-3 bg-white dark:bg-slate-900 rounded-lg border border-emerald-200 dark:border-emerald-800 text-xs font-mono text-slate-800 dark:text-slate-200 break-words">
                {result.safeOutput.redactedText || '(Empty or Withheld)'}
              </div>
            </div>
          </div>

          {/* Finding cues breakdown table */}
          <div>
            <h3 className="text-xs font-bold text-slate-700 dark:text-slate-300 mb-2">
              Privacy Detections & Heuristic Reasoning ({result.findings.length})
            </h3>
            {result.findings.length === 0 ? (
              <div className="text-xs text-slate-500 italic">No PII or privacy cues detected.</div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
                {result.findings.map((f, i) => (
                  <div
                    key={i}
                    className="p-3 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 flex flex-col justify-between"
                  >
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <span className="font-mono text-xs font-bold text-slate-900 dark:text-white">{f.value}</span>
                        {getBadgeForFinding(f)}
                      </div>
                      <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-1">{f.reason}</p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
