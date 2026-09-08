'use client'

import React, { useState, useEffect, useRef } from 'react'
import { 
  Play, 
  Square, 
  Settings, 
  Mic, 
  MicOff, 
  CheckCircle, 
  AlertCircle, 
  Terminal, 
  Smartphone, 
  RefreshCw, 
  Lock, 
  Unlock, 
  Wifi, 
  Battery, 
  ArrowLeft, 
  Compass, 
  CloudSun, 
  Activity, 
  FileText, 
  Check, 
  ChevronRight, 
  Info,
  Video,
  Volume2,
  Trash2,
  Menu,
  X,
  Search,
  Eye,
  Download
} from 'lucide-react'

// Simple Client Error Boundary
class ErrorBoundary extends React.Component<{ children: React.ReactNode }, { hasError: boolean; error: any }> {
  constructor(props: any) {
    super(props)
    this.state = { hasError: false, error: null }
  }
  static getDerivedStateFromError(error: any) {
    return { hasError: true, error }
  }
  componentDidCatch(error: any, errorInfo: any) {
    console.error("ErrorBoundary caught an error:", error, errorInfo)
  }
  render() {
    if (this.state.hasError) {
      return (
        <div className="p-8 bg-red-950/40 border border-red-500/50 rounded-2xl m-4 text-center">
          <AlertCircle className="size-12 text-red-400 mx-auto mb-3 animate-bounce" />
          <h2 className="text-lg font-bold text-red-200 mb-1">Something went wrong</h2>
          <p className="text-xs text-red-400/80 font-mono mb-4">{this.state.error?.toString()}</p>
          <button 
            onClick={() => window.location.reload()}
            className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white font-bold text-xs rounded-xl transition"
          >
            Reload Preview
          </button>
        </div>
      )
    }
    return this.props.children
  }
}

// System log type
interface LogEntry {
  id: string
  timestamp: string
  source: 'PLANNER' | 'DEVICE' | 'POLICY' | 'SYSTEM' | 'SUCCESS' | 'ERROR'
  message: string
}

// Simulated agent actions
interface AgentAction {
  type: string
  description: string
  details?: string
}

// UI Inspector Node Type
interface InspectorNode {
  id: string
  role: string
  text: string
  clickable: boolean
  enabled: boolean
  bounds: {
    top: string
    left: string
    width: string
    height: string
  }
}

export default function Page() {
  return (
    <ErrorBoundary>
      <MainDashboard />
    </ErrorBoundary>
  )
}

function MainDashboard() {
  // Navigation & Tabs
  const [activeSidebarTab, setActiveSidebarTab] = useState<'dashboard' | 'inspector' | 'actions' | 'logs' | 'settings'>('dashboard')
  const [activeConsoleTab, setActiveConsoleTab] = useState<'agent' | 'terminal' | 'policy'>('agent')
  const [sidebarOpen, setSidebarOpen] = useState(true)

  // Config & State
  const [command, setCommand] = useState('')
  const [isRunning, setIsRunning] = useState(false)
  const [isPaused, setIsPaused] = useState(false)
  const [isSimulation, setIsSimulation] = useState(true)
  
  // Interactive Android States
  const [accessibilityConnected, setAccessibilityConnected] = useState(true)
  const [microphoneGranted, setMicrophoneGranted] = useState(true)
  const [screenCaptureConsent, setScreenCaptureConsent] = useState(true)
  const [plannerMode, setPlannerMode] = useState<'offline' | 'gemini'>('offline')
  const [apiKey, setApiKey] = useState('')
  
  // Voice recognition simulation
  const [isRecording, setIsRecording] = useState(false)
  const [recordingLevel, setRecordingLevel] = useState<number[]>([4, 4, 4, 4, 4, 4])

  // Simulator Screen State
  const [screenState, setScreenState] = useState<'home' | 'companion' | 'youtube' | 'maps' | 'weather' | 'settings' | 'custom'>('companion')
  const [previousScreenState, setPreviousScreenState] = useState<'home' | 'companion' | 'youtube' | 'maps' | 'weather' | 'settings' | 'custom'>('home')
  const [wifiConnected, setWifiConnected] = useState(true)
  
  // Pointer & Ripple effects
  const [pointerPos, setPointerPos] = useState({ x: 50, y: 50 })
  const [pointerVisible, setPointerVisible] = useState(false)
  const [ripplePos, setRipplePos] = useState({ x: 0, y: 0 })
  const [rippleVisible, setRippleVisible] = useState(false)
  const [currentStepText, setCurrentStepText] = useState('')
  const [typedInput, setTypedInput] = useState('')
  const [searchQuery, setSearchQuery] = useState('')

  // UI Inspector Selected Node
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null)

  // Execution steps timeline
  const [executionTimeline, setExecutionTimeline] = useState<Array<{
    label: string
    status: 'PENDING' | 'PLANNING' | 'VALIDATING' | 'EXECUTING' | 'SUCCESS' | 'FAILED' | 'BLOCKED'
    message: string
  }>>([
    { label: 'User Request', status: 'SUCCESS', message: 'Ready for dynamic command input.' },
    { label: 'Intent Parsed', status: 'PENDING', message: 'Awaiting task parser activation.' },
    { label: 'Plan Generated', status: 'PENDING', message: 'Awaiting sequence construction.' },
    { label: 'Action Validation', status: 'PENDING', message: 'ActionPolicy validation pending.' },
    { label: 'Action Execution', status: 'PENDING', message: 'Awaiting executor stream.' },
    { label: 'Result', status: 'PENDING', message: 'Ready.' }
  ])

  // Confirmation Request Drawer
  const [pendingConfirmation, setPendingConfirmation] = useState<{
    action: AgentAction
    resolve: (confirmed: boolean) => void
  } | null>(null)

  // System Logs (Session persistence in client state)
  const [logs, setLogs] = useState<LogEntry[]>([
    { id: '1', timestamp: getTimestamp(), source: 'SYSTEM', message: 'Workspace initialized successfully.' },
    { id: '2', timestamp: getTimestamp(), source: 'SYSTEM', message: 'Android Agent Runtime loaded. Target SDK: 35.' },
    { id: '3', timestamp: getTimestamp(), source: 'DEVICE', message: 'Accessibility service connected and listening.' },
    { id: '4', timestamp: getTimestamp(), source: 'SYSTEM', message: 'Demo fallback planner active. Standing by.' }
  ])

  // Refs for auto-scroll
  const logEndRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    logEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [logs])

  // Microphone level indicator animation
  useEffect(() => {
    let interval: any
    if (isRecording) {
      interval = setInterval(() => {
        setRecordingLevel(Array.from({ length: 6 }, () => Math.floor(Math.random() * 24) + 4))
      }, 100)
    } else {
      setRecordingLevel([4, 4, 4, 4, 4, 4])
    }
    return () => clearInterval(interval)
  }, [isRecording])

  // Time stamp helper
  function getTimestamp() {
    const d = new Date()
    return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}:${d.getSeconds().toString().padStart(2, '0')}.${d.getMilliseconds().toString().padStart(3, '0')}`
  }

  // Add system log helper
  const addLog = (source: LogEntry['source'], message: string) => {
    setLogs(prev => [
      ...prev,
      {
        id: Math.random().toString(),
        timestamp: getTimestamp(),
        source,
        message
      }
    ])
  }

  // Clear log screen
  const clearLogs = () => {
    setLogs([{ id: '1', timestamp: getTimestamp(), source: 'SYSTEM', message: 'Logs cleared. Standby.' }])
  }

  // Export Logs to local JSON file
  const exportLogs = () => {
    try {
      const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(logs, null, 2))
      const downloadAnchor = document.createElement('a')
      downloadAnchor.setAttribute("href", dataStr)
      downloadAnchor.setAttribute("download", `android_agent_logs_${Date.now()}.json`)
      document.body.appendChild(downloadAnchor)
      downloadAnchor.click()
      downloadAnchor.remove()
      addLog('SYSTEM', 'Exported session log file successfully.')
    } catch (e) {
      console.error(e)
    }
  }

  // Trigger click ripple on screen
  const triggerRipple = (x: number, y: number) => {
    setRipplePos({ x, y })
    setRippleVisible(true)
    setTimeout(() => setRippleVisible(false), 600)
  }

  // Helper to change screen and record previous
  const transitionToScreen = (newScreen: typeof screenState) => {
    setPreviousScreenState(screenState)
    setScreenState(newScreen)
  }

  // Animate pointer move and click helper
  const animatePointerAndClick = async (x: number, y: number, text: string, delayMs = 1200) => {
    setPointerVisible(true)
    setPointerPos({ x, y })
    setCurrentStepText(text)
    await sleep(delayMs)
    triggerRipple(x, y)
    await sleep(300)
  }

  const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

  // Main task orchestrator & planner simulation
  const executeAgentTask = async (commandText: string) => {
    if (isRunning) return
    setIsRunning(true)
    setIsPaused(false)
    setSearchQuery('')
    setTypedInput('')
    setSelectedNodeId(null)
    
    // Reset Timeline status
    setExecutionTimeline([
      { label: 'User Request', status: 'SUCCESS', message: `Command queued: "${commandText}"` },
      { label: 'Intent Parsed', status: 'PLANNING', message: 'Analyzing voice or typed intent...' },
      { label: 'Plan Generated', status: 'PENDING', message: 'Pending construct...' },
      { label: 'Action Validation', status: 'PENDING', message: 'Security Policy pending...' },
      { label: 'Action Execution', status: 'PENDING', message: 'Pending execution...' },
      { label: 'Result', status: 'PENDING', message: 'Pending task result.' }
    ])

    addLog('SYSTEM', `Task Parser: Processing user command "${commandText}"`)
    await sleep(600)

    setExecutionTimeline(prev => [
      prev[0],
      { label: 'Intent Parsed', status: 'SUCCESS', message: 'Command mapped to specific system tasks.' },
      { label: 'Plan Generated', status: 'PLANNING', message: 'Synthesizing action steps for device controller...' },
      prev[3], prev[4], prev[5]
    ])
    addLog('PLANNER', 'Creating device snapshots and reading Accessibility Node Hierarchy...')
    await sleep(800)

    // Security Gate check
    setExecutionTimeline(prev => [
      prev[0], prev[1],
      { label: 'Plan Generated', status: 'SUCCESS', message: 'Constructed 5-step interactive workflow.' },
      { label: 'Action Validation', status: 'PLANNING', message: 'Running security bounds checks...' },
      prev[4], prev[5]
    ])
    addLog('POLICY', 'Policy Guard checking generated package and gesture ranges...')
    await sleep(600)

    const normalized = commandText.toLowerCase()
    const isSensitive = normalized.includes('delete') || normalized.includes('private') || normalized.includes('clearance') || normalized.includes('warning')

    if (isSensitive) {
      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2],
        { label: 'Action Validation', status: 'BLOCKED', message: 'CONFIRMATION_REQUIRED state detected.' },
        { label: 'Action Execution', status: 'PENDING', message: 'Execution halted.' },
        prev[5]
      ])
      addLog('POLICY', 'Warning: High-risk system state modification detected.')
      
      const confirmed = await requestUserClearance({
        type: 'Toggle Security Directory & Access Controls',
        description: 'Toggling administrative permissions or accessing secure internal preferences.',
        details: commandText
      })

      if (!confirmed) {
        addLog('POLICY', 'Clearance denied by device owner. Aborting task plan immediately.')
        addLog('ERROR', 'Planner task aborted.')
        setExecutionTimeline(prev => [
          prev[0], prev[1], prev[2],
          { label: 'Action Validation', status: 'BLOCKED', message: 'Validation failed: Permission Denied.' },
          { label: 'Action Execution', status: 'FAILED', message: 'Task terminated cleanly.' },
          { label: 'Result', status: 'FAILED', message: 'Aborted: Security guard blocked action.' }
        ])
        setIsRunning(false)
        return
      }
      
      addLog('POLICY', 'Clearance verified. Launching action sequence.')
    }

    setExecutionTimeline(prev => [
      prev[0], prev[1], prev[2],
      { label: 'Action Validation', status: 'SUCCESS', message: 'Validation complete. Safe state authorized.' },
      { label: 'Action Execution', status: 'PLANNING', message: 'Connecting Accessibility Service pipe...' },
      prev[5]
    ])
    await sleep(400)

    // Run action flows based on intent parsed
    if (normalized.includes('youtube') || normalized.includes('play') || normalized.includes('music') || normalized.includes('lo-fi')) {
      transitionToScreen('home')
      addLog('DEVICE', 'Current Screen State: com.android.launcher3 (Home launcher)')
      await sleep(1000)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 1/4: Locating and opening YouTube icon' },
        prev[5]
      ])
      await animatePointerAndClick(70, 39, 'Locating YouTube icon', 1200)
      transitionToScreen('youtube')
      setPointerVisible(false)
      addLog('DEVICE', 'Opened package: com.google.android.youtube')
      await sleep(1500)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 2/4: Clicking search button' },
        prev[5]
      ])
      await animatePointerAndClick(80, 5, 'Activating search bar', 1200)
      addLog('PLANNER', 'Accessibility typing: "lo-fi study stream"')
      
      // Simulating typing
      const textToType = 'lo-fi study stream'
      let partial = ''
      for (let i = 0; i < textToType.length; i++) {
        partial += textToType[i]
        setTypedInput(partial)
        await sleep(60)
      }
      await sleep(600)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 3/4: Executing query search' },
        prev[5]
      ])
      await animatePointerAndClick(85, 93, 'Submitting query search', 1000)
      setSearchQuery('lo-fi study stream')
      setTypedInput('')
      addLog('DEVICE', 'View update: Rendered live lo-fi stream list')
      await sleep(1500)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 4/4: Selecting video item' },
        prev[5]
      ])
      await animatePointerAndClick(50, 42, 'Playing active lo-fi video stream', 1200)
      setPointerVisible(false)
      addLog('DEVICE', 'Streaming status: Currently playing "Lofi Girl 24/7 Deep Focus Music"')
      
      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'SUCCESS', message: 'Task complete.' },
        { label: 'Result', status: 'SUCCESS', message: 'Lo-fi stream is currently active on device!' }
      ])
      addLog('SUCCESS', 'Execution flow complete. Goal achieved successfully.')

    } else if (normalized.includes('coffee') || normalized.includes('map') || normalized.includes('navigate')) {
      transitionToScreen('home')
      addLog('DEVICE', 'Current Screen State: com.android.launcher3 (Home launcher)')
      await sleep(1000)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 1/4: Locating and opening Google Maps icon' },
        prev[5]
      ])
      await animatePointerAndClick(30, 39, 'Opening Google Maps app', 1200)
      transitionToScreen('maps')
      setPointerVisible(false)
      addLog('DEVICE', 'Opened package: com.google.android.apps.maps')
      await sleep(1500)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 2/4: Activating search box' },
        prev[5]
      ])
      await animatePointerAndClick(50, 5, 'Activating Search bar', 1200)
      addLog('PLANNER', 'Accessibility typing: "local coffee shop"')
      
      const textToType = 'local coffee shop'
      let partial = ''
      for (let i = 0; i < textToType.length; i++) {
        partial += textToType[i]
        setTypedInput(partial)
        await sleep(60)
      }
      await sleep(600)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 3/4: Resolving drop pins' },
        prev[5]
      ])
      await animatePointerAndClick(90, 5, 'Submitting query search', 1000)
      setSearchQuery('local coffee shop')
      setTypedInput('')
      addLog('DEVICE', 'View update: Retrieved drop pins matching "local coffee shop"')
      await sleep(1500)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 4/4: Activating navigation' },
        prev[5]
      ])
      await animatePointerAndClick(80, 88, 'Starting route navigation', 1200)
      setPointerVisible(false)
      addLog('DEVICE', 'Route active: Navigation starting to "Brew & Brew Cafe"')

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'SUCCESS', message: 'Task complete.' },
        { label: 'Result', status: 'SUCCESS', message: 'Coffee shop route loaded. Navigation live!' }
      ])
      addLog('SUCCESS', 'Execution flow complete. Navigation starting.')

    } else if (normalized.includes('weather') || normalized.includes('rain') || normalized.includes('forecast')) {
      transitionToScreen('home')
      addLog('DEVICE', 'Current Screen State: com.android.launcher3 (Home launcher)')
      await sleep(1000)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 1/3: Opening Weather Card' },
        prev[5]
      ])
      await animatePointerAndClick(70, 55, 'Opening Weather app', 1200)
      transitionToScreen('weather')
      setPointerVisible(false)
      addLog('DEVICE', 'Opened package: com.example.android.weather')
      await sleep(1500)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 2/3: Searching city' },
        prev[5]
      ])
      await animatePointerAndClick(50, 5, 'Activating Location search', 1000)
      addLog('PLANNER', 'Accessibility typing: "San Francisco"')
      
      const textToType = 'San Francisco'
      let partial = ''
      for (let i = 0; i < textToType.length; i++) {
        partial += textToType[i]
        setTypedInput(partial)
        await sleep(60)
      }
      await sleep(600)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 3/3: Querying weather channel databases' },
        prev[5]
      ])
      await animatePointerAndClick(90, 5, 'Query weather conditions', 1000)
      setSearchQuery('San Francisco')
      setTypedInput('')
      addLog('DEVICE', 'Retrieved San Francisco Forecast: 68°F, Sunny')

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'SUCCESS', message: 'Task complete.' },
        { label: 'Result', status: 'SUCCESS', message: 'SF Weather forecast loaded successfully.' }
      ])
      addLog('SUCCESS', 'Forecast compiled.')

    } else if (normalized.includes('settings') || normalized.includes('system') || normalized.includes('wi-fi')) {
      transitionToScreen('home')
      addLog('DEVICE', 'Current Screen State: com.android.launcher3 (Home launcher)')
      await sleep(1000)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Task 1/2: Opening System Settings' },
        prev[5]
      ])
      await animatePointerAndClick(30, 55, 'Clicking settings launcher icon', 1200)
      transitionToScreen('settings')
      setPointerVisible(false)
      addLog('DEVICE', 'Opened package: com.android.settings')
      await sleep(1500)

      if (normalized.includes('wi-fi')) {
        setExecutionTimeline(prev => [
          prev[0], prev[1], prev[2], prev[3],
          { label: 'Action Execution', status: 'EXECUTING', message: 'Task 2/2: Toggling Wi-Fi connection' },
          prev[5]
        ])
        await animatePointerAndClick(50, 18, 'Clicking Wi-Fi Settings row', 1200)
        setWifiConnected(!wifiConnected)
        addLog('DEVICE', `Wi-Fi state modified to: ${!wifiConnected ? 'Connected' : 'Disconnected'}`)
        setPointerVisible(false)
      }

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'SUCCESS', message: 'Task complete.' },
        { label: 'Result', status: 'SUCCESS', message: 'System Settings opened and configured successfully.' }
      ])
      addLog('SUCCESS', 'Settings configured.')

    } else if (normalized.includes('scroll') || normalized.includes('down')) {
      addLog('PLANNER', 'Evaluating scroll range bounds...')
      await sleep(600)
      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Emulating scroll gesture scrollDown()' },
        prev[5]
      ])
      addLog('DEVICE', 'Dispatched Gesture: [Scroll Down] executed smoothly.')
      await sleep(1200)
      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'SUCCESS', message: 'Scroll complete.' },
        { label: 'Result', status: 'SUCCESS', message: 'Device viewport scrolled down.' }
      ])
      addLog('SUCCESS', 'Action success.')

    } else if (normalized.includes('back')) {
      addLog('PLANNER', 'Dispatching system back key event...')
      await sleep(600)
      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Emulating back gesture performGlobalAction(1)' },
        prev[5]
      ])
      transitionToScreen(previousScreenState)
      addLog('DEVICE', `System navigated back. Current Screen State: ${previousScreenState}`)
      await sleep(1000)
      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'SUCCESS', message: 'Back navigation complete.' },
        { label: 'Result', status: 'SUCCESS', message: 'Returned to previous state screen.' }
      ])
      addLog('SUCCESS', 'Back complete.')

    } else {
      // Generic App Flow
      transitionToScreen('home')
      addLog('DEVICE', 'Current Screen State: com.android.launcher3 (Home launcher)')
      await sleep(1000)

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'EXECUTING', message: 'Resolving dynamic query package...' },
        prev[5]
      ])
      await animatePointerAndClick(30, 55, 'Launching custom action app', 1200)
      transitionToScreen('custom')
      setPointerVisible(false)
      addLog('DEVICE', 'Opened package: com.android.assistant.custom')

      setExecutionTimeline(prev => [
        prev[0], prev[1], prev[2], prev[3],
        { label: 'Action Execution', status: 'SUCCESS', message: 'Task complete.' },
        { label: 'Result', status: 'SUCCESS', message: `Custom execution completed for "${commandText}".` }
      ])
      addLog('SUCCESS', 'Goal achieved.')
    }

    setPointerVisible(false)
    setIsRunning(false)
  }

  // Request Security confirmation overlay
  const requestUserClearance = (action: AgentAction): Promise<boolean> => {
    return new Promise(resolve => {
      setPendingConfirmation({
        action,
        resolve: (val) => {
          setPendingConfirmation(null)
          resolve(val)
        }
      })
    })
  }

  // Speak Voice input sequence simulator
  const toggleVoiceRecording = () => {
    if (!microphoneGranted) {
      addLog('ERROR', 'Voice permissions missing. Access denied.')
      return
    }

    if (isRecording) {
      setIsRecording(false)
      const presets = [
        'Open YouTube lo-fi stream',
        'Find coffee shop nearby',
        'SF weather forecast',
        'Open settings to toggle Wi-Fi'
      ]
      const chosen = presets[Math.floor(Math.random() * presets.length)]
      setCommand(chosen)
      addLog('SYSTEM', `Voice recognized input: "${chosen}"`)
    } else {
      setIsRecording(true)
      addLog('DEVICE', 'Simulating voice transcription recording...')
    }
  }

  const triggerTaskStart = () => {
    if (!command.trim()) return
    executeAgentTask(command)
  }

  const handleStop = () => {
    setIsRunning(false)
    setIsPaused(false)
    setPointerVisible(false)
    transitionToScreen('companion')
    addLog('SYSTEM', 'Emergency process termination requested. Clearing execution register state.')
  }

  // Build Accessibility Tree based on active screenState
  const getAccessibilityTree = (): InspectorNode[] => {
    switch (screenState) {
      case 'companion':
        return [
          { id: 'node_toolbar', role: 'Toolbar', text: 'AI Android Agent Workspace', clickable: false, enabled: true, bounds: { top: '8%', left: '5%', width: '90%', height: '8%' } },
          { id: 'node_status_card', role: 'CardView', text: `Agent Status: ${isRunning ? 'ACTIVE' : 'READY'}`, clickable: false, enabled: true, bounds: { top: '18%', left: '5%', width: '90%', height: '24%' } },
          { id: 'node_accessibility_toggle', role: 'Button', text: `Accessibility: ${accessibilityConnected ? 'CONNECTED' : 'REQUIRED'}`, clickable: true, enabled: true, bounds: { top: '45%', left: '5%', width: '90%', height: '12%' } },
          { id: 'node_log_container', role: 'ListView', text: 'Companion Activity Logs', clickable: false, enabled: true, bounds: { top: '60%', left: '5%', width: '90%', height: '24%' } }
        ]
      case 'home':
        return [
          { id: 'node_clock', role: 'TextView', text: 'Monday 05:30 PM', clickable: false, enabled: true, bounds: { top: '10%', left: '10%', width: '80%', height: '15%' } },
          { id: 'node_youtube_icon', role: 'ImageView', text: 'YouTube', clickable: true, enabled: true, bounds: { top: '35%', left: '50%', width: '25%', height: '15%' } },
          { id: 'node_maps_icon', role: 'ImageView', text: 'Maps', clickable: true, enabled: true, bounds: { top: '35%', left: '25%', width: '25%', height: '15%' } },
          { id: 'node_weather_icon', role: 'ImageView', text: 'Weather', clickable: true, enabled: true, bounds: { top: '52%', left: '50%', width: '25%', height: '15%' } },
          { id: 'node_companion_icon', role: 'ImageView', text: 'Agent App Launcher', clickable: true, enabled: true, bounds: { top: '52%', left: '25%', width: '25%', height: '15%' } }
        ]
      case 'youtube':
        return [
          { id: 'node_yt_toolbar', role: 'Toolbar', text: 'YouTube Navigation Header', clickable: false, enabled: true, bounds: { top: '2%', left: '5%', width: '90%', height: '8%' } },
          { id: 'node_yt_search', role: 'EditText', text: searchQuery || 'Search YouTube Video', clickable: true, enabled: true, bounds: { top: '2%', left: '55%', width: '40%', height: '8%' } },
          { id: 'node_yt_video_card', role: 'CardView', text: searchQuery ? 'Lofi Study Stream Live Broadcast' : 'Awaiting Video Search Query', clickable: true, enabled: true, bounds: { top: '15%', left: '5%', width: '90%', height: '45%' } }
        ]
      case 'maps':
        return [
          { id: 'node_maps_search', role: 'EditText', text: searchQuery || 'Search in Maps', clickable: true, enabled: true, bounds: { top: '2%', left: '5%', width: '90%', height: '8%' } },
          { id: 'node_maps_pin', role: 'ImageView', text: searchQuery ? 'Drop Pin: Brew & Brew Cafe' : 'Map Viewport Grid', clickable: false, enabled: true, bounds: { top: '40%', left: '40%', width: '20%', height: '20%' } },
          { id: 'node_maps_directions', role: 'CardView', text: searchQuery ? 'Start Route: Brew Cafe' : 'Location metadata info', clickable: true, enabled: true, bounds: { top: '75%', left: '5%', width: '90%', height: '20%' } }
        ]
      case 'weather':
        return [
          { id: 'node_weather_search', role: 'EditText', text: searchQuery || 'Enter location', clickable: true, enabled: true, bounds: { top: '2%', left: '5%', width: '90%', height: '8%' } },
          { id: 'node_weather_stats', role: 'TextView', text: searchQuery ? 'San Francisco: 68°F Sunny' : 'No Location Loaded', clickable: false, enabled: true, bounds: { top: '20%', left: '10%', width: '80%', height: '35%' } }
        ]
      case 'settings':
        return [
          { id: 'node_settings_toolbar', role: 'Toolbar', text: 'Android System Settings', clickable: false, enabled: true, bounds: { top: '2%', left: '5%', width: '90%', height: '8%' } },
          { id: 'node_settings_wifi', role: 'TextView', text: `Wi-Fi state: ${wifiConnected ? 'Connected' : 'Disconnected'}`, clickable: true, enabled: true, bounds: { top: '14%', left: '5%', width: '90%', height: '10%' } },
          { id: 'node_settings_bt', role: 'TextView', text: 'Bluetooth: Disconnected', clickable: true, enabled: true, bounds: { top: '26%', left: '5%', width: '90%', height: '10%' } },
          { id: 'node_settings_display', role: 'TextView', text: 'Display Brightness: 80%', clickable: true, enabled: true, bounds: { top: '38%', left: '5%', width: '90%', height: '10%' } },
          { id: 'node_settings_about', role: 'TextView', text: 'Model: Simulator API 35', clickable: false, enabled: true, bounds: { top: '50%', left: '5%', width: '90%', height: '10%' } }
        ]
      case 'custom':
        return [
          { id: 'node_custom_header', role: 'ImageView', text: 'Custom automation icon header', clickable: false, enabled: true, bounds: { top: '15%', left: '30%', width: '40%', height: '20%' } },
          { id: 'node_custom_console', role: 'TextView', text: 'Dynamic Agent Execution Complete', clickable: false, enabled: true, bounds: { top: '45%', left: '5%', width: '90%', height: '30%' } }
        ]
    }
  }

  const inspectorNodes = getAccessibilityTree()
  const selectedNode = inspectorNodes.find(node => node.id === selectedNodeId)

  return (
    <div className="min-h-screen bg-[#111116] text-[#E3E1E6] font-sans overflow-x-hidden antialiased flex flex-col">
      
      {/* 1. Header component */}
      <header className="border-b border-[#2B2930] bg-[#17161D] px-6 py-4 flex flex-wrap items-center justify-between shadow-md gap-4">
        <div className="flex items-center gap-3">
          <button 
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="p-1.5 rounded-lg hover:bg-[#2B2930] transition text-[#CAC4D0] md:hidden"
            title="Toggle Menu"
          >
            {sidebarOpen ? <X className="size-5" /> : <Menu className="size-5" />}
          </button>
          
          <div className="bg-[#6750A4] p-2 rounded-xl text-white shadow-inner flex items-center justify-center">
            <Smartphone className="size-6" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-lg font-bold tracking-tight text-[#E6E1E5]">AI Android Agent</h1>
              <span className="text-[10px] bg-[#E8DEF8]/10 text-[#D0BCFF] border border-[#D0BCFF]/30 font-semibold px-2 py-0.5 rounded">
                Control Center
              </span>
            </div>
            <p className="text-xs text-[#938F99] font-medium flex items-center gap-1.5">
              <Activity className="size-3 text-[#4F378B]" /> Active Web Workspace Simulator
            </p>
          </div>
        </div>

        {/* System parameters display */}
        <div className="flex items-center gap-3 text-xs">
          <div className="hidden sm:flex items-center gap-4 bg-[#1D1B20] border border-[#2B2930] px-4 py-2 rounded-xl text-[11px] text-[#CAC4D0]">
            <div>Status: <span className="text-green-400 font-bold">● Preview Online</span></div>
            <div className="w-px h-3 bg-zinc-800"></div>
            <div>Connection: <span className="font-semibold">Web Simulator</span></div>
            <div className="w-px h-3 bg-zinc-800"></div>
            <div>Agent: <span className="text-[#D0BCFF] font-semibold">Ready</span></div>
          </div>

          <button 
            onClick={() => {
              setAccessibilityConnected(true)
              setMicrophoneGranted(true)
              setScreenCaptureConsent(true)
              setWifiConnected(true)
              setCommand('')
              transitionToScreen('companion')
              addLog('SYSTEM', 'Simulator environments restored completely.')
            }}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-[#49454F] text-[#CAC4D0] hover:bg-[#2B2930] text-xs font-semibold transition"
          >
            <RefreshCw className="size-3.5" /> Restore State
          </button>
        </div>
      </header>

      {/* Main Body with Sidebar Layout */}
      <div className="flex-1 flex flex-row">
        
        {/* 2. Left Sidebar panel */}
        <aside className={`${
          sidebarOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0 md:w-16'
        } fixed md:static inset-y-0 left-0 z-40 w-64 md:w-60 bg-[#17161D] border-r border-[#2B2930] transition-all duration-300 flex flex-col justify-between shrink-0`}>
          
          <div className="p-4 flex flex-col gap-1">
            <span className="text-[10px] font-bold text-zinc-500 uppercase tracking-wider px-3 mb-2 block">Agent Navigation</span>
            
            {[
              { id: 'dashboard', label: 'Dashboard Control', tab: 'dashboard' },
              { id: 'inspector', label: 'UI Inspector', tab: 'inspector' },
              { id: 'actions', label: 'Action Policy', tab: 'actions' },
              { id: 'logs', label: 'System Logs', tab: 'logs' },
              { id: 'settings', label: 'Agent Settings', tab: 'settings' }
            ].map(item => (
              <button
                key={item.id}
                onClick={() => {
                  setActiveSidebarTab(item.tab as any)
                  if (item.tab === 'inspector') transitionToScreen(screenState === 'companion' ? 'home' : screenState)
                }}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-semibold text-left transition-all ${
                  activeSidebarTab === item.tab 
                    ? 'bg-[#381E72] text-[#D0BCFF] shadow-inner' 
                    : 'text-[#CAC4D0] hover:bg-[#2B2930] hover:text-white'
                }`}
              >
                <Smartphone className="size-4 shrink-0" />
                <span className="truncate">{item.label}</span>
              </button>
            ))}

            <div className="mt-6 border-t border-[#2B2930] pt-4 px-3">
              <span className="text-[10px] font-bold text-[#D0BCFF] uppercase tracking-wider block mb-2">Simulated Screens</span>
              <div className="grid grid-cols-2 gap-1.5">
                {[
                  { label: 'Agent App', state: 'companion' },
                  { label: 'Launcher', state: 'home' },
                  { label: 'YouTube', state: 'youtube' },
                  { label: 'Maps', state: 'maps' },
                  { label: 'Weather', state: 'weather' },
                  { label: 'Settings', state: 'settings' }
                ].map(item => (
                  <button
                    key={item.state}
                    onClick={() => {
                      transitionToScreen(item.state as any)
                      addLog('DEVICE', `Manually navigated simulator to: ${item.label}`)
                    }}
                    className={`px-2 py-1.5 rounded-lg border text-[10px] font-bold tracking-tight text-center transition ${
                      screenState === item.state 
                        ? 'bg-[#4F378B]/20 text-[#D0BCFF] border-[#D0BCFF]/30' 
                        : 'border-[#49454F] text-[#CAC4D0] hover:bg-[#2B2930]'
                    }`}
                  >
                    {item.label}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Quick Warning Footer */}
          <div className="p-4 bg-[#111116] border-t border-[#2B2930] m-3 rounded-xl">
            <div className="flex gap-2 items-start text-[10px] text-[#938F99]">
              <Info className="size-3.5 text-[#D0BCFF] shrink-0 mt-0.5" />
              <p className="leading-normal">
                <b>Web Simulator Mode</b>: This environment demonstrates accessibility traversal and planning pipelines visually. It does not access real connected hardware accounts.
              </p>
            </div>
          </div>
        </aside>

        {/* 3. Main Workspace and Simulator Panels */}
        <main className="flex-1 grid grid-cols-1 xl:grid-cols-12 gap-6 p-6 overflow-y-auto">
          
          {/* Central Workspace Area */}
          <section className="xl:col-span-7 flex flex-col gap-6">
            
            {activeSidebarTab === 'dashboard' && (
              <>
                {/* Agent Command Console Card */}
                <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl p-5 shadow-sm">
                  <div className="flex justify-between items-center mb-2">
                    <h2 className="text-sm font-bold text-[#E6E1E5] flex items-center gap-2">
                      <Terminal className="size-4 text-[#D0BCFF]" /> Agent Command Console
                    </h2>
                    <span className="text-[10px] bg-green-500/15 text-green-400 border border-green-500/20 font-bold px-2 py-0.5 rounded">
                      Local Interpreter Ready
                    </span>
                  </div>
                  <p className="text-xs text-[#938F99] leading-relaxed mb-4">
                    Send instructions to start the task planning loop. The agent parses the goal, scans active nodes, performs step action sequence matching, and executes simulated gestures inside the emulator frame.
                  </p>

                  {/* Search Input block */}
                  <div className="flex flex-col gap-3">
                    <div className="relative flex items-center">
                      <input 
                        type="text"
                        placeholder="Tell the agent what to do... (e.g. 'Open settings and check Wi-Fi')"
                        value={command}
                        onChange={(e) => setCommand(e.target.value)}
                        disabled={isRunning}
                        onKeyDown={(e) => e.key === 'Enter' && triggerTaskStart()}
                        className="w-full bg-[#2B2930] text-xs text-[#E6E1E5] placeholder-[#938F99] border border-[#49454F] rounded-xl pl-4 pr-12 py-3.5 focus:border-[#D0BCFF] focus:ring-1 focus:ring-[#D0BCFF] outline-none transition disabled:opacity-60"
                      />
                      
                      <button 
                        onClick={toggleVoiceRecording}
                        disabled={isRunning}
                        className={`absolute right-2.5 p-2 rounded-lg transition ${
                          isRecording 
                            ? 'bg-[#E40046] text-white animate-pulse' 
                            : 'text-[#CAC4D0] hover:bg-[#36343B]'
                        }`}
                        title="Simulate Voice Input"
                      >
                        {isRecording ? <MicOff className="size-4" /> : <Mic className="size-4" />}
                      </button>
                    </div>

                    {/* Voice wave bar */}
                    {isRecording && (
                      <div className="flex items-center gap-2.5 bg-[#211F26] border border-[#E40046]/30 p-3 rounded-xl">
                        <div className="flex gap-0.5 items-center shrink-0">
                          {recordingLevel.map((lvl, idx) => (
                            <span 
                              key={idx} 
                              style={{ height: `${lvl}px` }} 
                              className="w-1 bg-[#E40046] rounded-full transition-all duration-100"
                            />
                          ))}
                        </div>
                        <span className="text-[11px] text-[#938F99]">
                          Voice recognition listening... Click microphone button again to simulate transcription.
                        </span>
                      </div>
                    )}

                    {/* Trigger controls */}
                    <div className="flex items-center justify-between gap-3 mt-1 flex-wrap">
                      <div className="flex items-center gap-2">
                        <button 
                          onClick={() => setIsSimulation(!isSimulation)}
                          className={`text-[11px] px-2.5 py-1.5 rounded-lg border font-bold transition ${
                            isSimulation 
                              ? 'bg-[#E8DEF8]/10 text-[#D0BCFF] border-[#D0BCFF]/30' 
                              : 'border-[#49454F] text-[#CAC4D0]'
                          }`}
                        >
                          {isSimulation ? '✓ Local Simulation' : 'Physical device tether'}
                        </button>
                        <span className="text-[10px] text-zinc-500 font-medium">
                          {isSimulation ? 'Autonomous visual timeline execution' : 'Tether active'}
                        </span>
                      </div>

                      <div className="flex items-center gap-2">
                        {isRunning ? (
                          <button 
                            onClick={handleStop}
                            className="flex items-center gap-1.5 bg-[#E40046] hover:bg-[#C3003A] text-white font-bold text-xs px-4 py-2 rounded-lg transition"
                          >
                            <Square className="size-3.5 fill-current" /> EMERGENCY STOP
                          </button>
                        ) : (
                          <button 
                            onClick={triggerTaskStart}
                            disabled={!command.trim()}
                            className="flex items-center gap-1.5 bg-[#D0BCFF] hover:bg-[#D0BCFF]/90 disabled:opacity-40 text-[#381E72] font-bold text-xs px-5 py-2.5 rounded-xl transition shadow"
                          >
                            <Play className="size-3.5 fill-current animate-pulse" /> RUN AGENT
                          </button>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Suggestion Flows */}
                  <div className="mt-5 border-t border-[#2B2930] pt-4">
                    <span className="text-[10px] font-bold text-[#938F99] uppercase tracking-wider block mb-2">Automated Execution Presets</span>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                      {[
                        { title: 'Open YouTube and play lo-fi', cmd: 'Open YouTube and play a lo-fi study stream' },
                        { title: 'Find coffee shop nearby', cmd: 'Open Maps to find local coffee shops' },
                        { title: 'Weather forecast in SF', cmd: 'Check weather conditions in San Francisco' },
                        { title: 'Trigger action validation prompt', cmd: 'Toggle private directories accessibility' }
                      ].map((item, idx) => (
                        <button 
                          key={idx}
                          onClick={() => {
                            setCommand(item.cmd)
                            addLog('SYSTEM', `Loaded preset action path: "${item.cmd}"`)
                          }}
                          className="bg-[#2B2930] hover:bg-[#36343B] border border-[#49454F]/60 text-left text-xs p-2.5 rounded-xl text-[#E6E1E5] font-semibold flex items-center justify-between group transition"
                        >
                          <span className="truncate">{item.title}</span>
                          <ChevronRight className="size-3 text-[#938F99] group-hover:text-[#D0BCFF] shrink-0" />
                        </button>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Execution Timeline Map */}
                <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl p-5 shadow-sm">
                  <h3 className="text-sm font-bold text-[#E6E1E5] mb-3 flex items-center gap-2">
                    <Activity className="size-4 text-[#D0BCFF]" /> Task Planning Execution Timeline
                  </h3>
                  
                  <div className="relative pl-6 border-l border-zinc-800 flex flex-col gap-4 text-xs">
                    {executionTimeline.map((step, idx) => {
                      const statusColor = {
                        PENDING: 'bg-zinc-800 text-zinc-500 border-zinc-700',
                        PLANNING: 'bg-purple-950/40 text-purple-400 border-purple-500 animate-pulse',
                        VALIDATING: 'bg-amber-950/40 text-amber-400 border-amber-500 animate-pulse',
                        EXECUTING: 'bg-blue-950/40 text-blue-400 border-blue-500 animate-pulse',
                        SUCCESS: 'bg-green-950/40 text-green-400 border-green-500',
                        FAILED: 'bg-red-950/40 text-red-400 border-red-500',
                        BLOCKED: 'bg-red-950/40 text-red-400 border-red-500'
                      }[step.status]

                      return (
                        <div key={idx} className="relative">
                          {/* Dot indicator */}
                          <span className={`absolute -left-[31px] top-0.5 size-4 rounded-full border-2 flex items-center justify-center text-[8px] font-bold ${statusColor}`}>
                            {idx + 1}
                          </span>
                          
                          <div>
                            <span className="font-bold text-[#E6E1E5] block mb-0.5">{step.label}</span>
                            <span className="text-[11px] text-[#938F99]">{step.message}</span>
                          </div>
                        </div>
                      )
                    })}
                  </div>
                </div>
              </>
            )}

            {/* Interactive Tab: UI Inspector Panel */}
            {activeSidebarTab === 'inspector' && (
              <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl p-5 shadow-sm flex-1 flex flex-col gap-4">
                <div>
                  <h2 className="text-sm font-bold text-[#E6E1E5] flex items-center gap-2">
                    <Eye className="size-4 text-[#D0BCFF]" /> Simulated Accessibility Tree Inspector
                  </h2>
                  <p className="text-xs text-[#938F99] leading-relaxed mt-1">
                    Traversing accessibility nodes on screen state: <code className="text-amber-400 font-mono font-bold text-[10px]">{screenState}</code>. Click any row item to visually locate and highlight bounds inside the phone frame.
                  </p>
                </div>

                <div className="border border-[#2B2930] rounded-xl overflow-hidden flex-1 max-h-[400px] overflow-y-auto font-mono text-[11px]">
                  <div className="grid grid-cols-12 bg-[#17161D] text-[#D0BCFF] py-2 px-3 border-b border-[#2B2930] font-bold">
                    <div className="col-span-3">Role</div>
                    <div className="col-span-4">Text</div>
                    <div className="col-span-2">Click</div>
                    <div className="col-span-3">Bounds</div>
                  </div>

                  <div className="flex flex-col divide-y divide-[#2B2930]/30">
                    {inspectorNodes.map((node) => (
                      <div 
                        key={node.id}
                        onClick={() => setSelectedNodeId(node.id === selectedNodeId ? null : node.id)}
                        className={`grid grid-cols-12 py-2 px-3 items-center cursor-pointer transition ${
                          selectedNodeId === node.id 
                            ? 'bg-[#381E72]/40 border-l-2 border-[#D0BCFF] text-[#E6E1E5]' 
                            : 'hover:bg-[#2B2930] text-[#CAC4D0]'
                        }`}
                      >
                        <div className="col-span-3 font-semibold truncate">{node.role}</div>
                        <div className="col-span-4 truncate text-[#E6E1E5]">{node.text || '(empty)'}</div>
                        <div className="col-span-2">
                          <span className={`px-1.5 py-0.5 text-[9px] rounded font-bold ${node.clickable ? 'bg-green-500/10 text-green-400' : 'bg-zinc-800 text-zinc-500'}`}>
                            {node.clickable ? 'YES' : 'NO'}
                          </span>
                        </div>
                        <div className="col-span-3 text-zinc-500 text-[10px] truncate">{`t:${node.bounds.top} l:${node.bounds.left}`}</div>
                      </div>
                    ))}
                  </div>
                </div>

                {selectedNode && (
                  <div className="bg-[#2B2930]/60 border border-[#49454F] p-4 rounded-xl flex flex-col gap-1 text-xs">
                    <div className="flex justify-between">
                      <span className="font-bold text-[#D0BCFF]">Node Detail ID: {selectedNode.id}</span>
                      <span className="text-zinc-500 font-mono text-[10px]">Bounds: {`w:${selectedNode.bounds.width} h:${selectedNode.bounds.height}`}</span>
                    </div>
                    <p className="text-[#938F99] leading-relaxed mt-1">
                      Role category: <span className="text-[#E6E1E5] font-semibold">{selectedNode.role}</span> | Focus element text: <span className="text-white">"{selectedNode.text || 'null'}"</span>. Interactive actions are authorized on accessibility streams.
                    </p>
                  </div>
                )}
              </div>
            )}

            {/* Interactive Tab: Action Policy */}
            {activeSidebarTab === 'actions' && (
              <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl p-5 shadow-sm flex-1 flex flex-col gap-4">
                <div>
                  <h2 className="text-sm font-bold text-[#E6E1E5] flex items-center gap-2">
                    <FileText className="size-4 text-[#D0BCFF]" /> Action Policy Safety Rules
                  </h2>
                  <p className="text-xs text-[#938F99] leading-normal mt-1">
                    Verification models from <code className="text-[10px] text-amber-300">ActionPolicy.kt</code>. All actions suggested by the AI models must pass through safe threshold criteria.
                  </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  {/* Safe Card */}
                  <div className="bg-green-500/5 border border-green-500/20 p-4 rounded-xl flex flex-col gap-2">
                    <div className="flex items-center gap-2 text-green-400 font-bold text-xs uppercase tracking-wider">
                      <Check className="size-4" /> Safe Actions
                    </div>
                    <p className="text-[11px] text-[#CAC4D0] leading-relaxed">
                      Actions that query layout stats, minimize apps, open directories, perform swipes, and execute search queries do not modify account data.
                    </p>
                    <div className="bg-[#111116] p-2.5 rounded text-[10px] font-mono text-zinc-500 mt-auto leading-normal">
                      OpenApp()<br/>ClickText()<br/>ScrollDown()
                    </div>
                  </div>

                  {/* Confirmation Card */}
                  <div className="bg-amber-500/5 border border-amber-500/20 p-4 rounded-xl flex flex-col gap-2">
                    <div className="flex items-center gap-2 text-amber-400 font-bold text-xs uppercase tracking-wider">
                      <Lock className="size-4" /> Confirmation Required
                    </div>
                    <p className="text-[11px] text-[#CAC4D0] leading-relaxed">
                      Actions that toggle important settings, type credential sequences, change database keys, or send messaging streams.
                    </p>
                    <div className="bg-[#111116] p-2.5 rounded text-[10px] font-mono text-zinc-500 mt-auto leading-normal">
                      TypeText()<br/>ToggleAccessibility()<br/>AccessPrivateData()
                    </div>
                  </div>

                  {/* Blocked Card */}
                  <div className="bg-red-500/5 border border-red-500/20 p-4 rounded-xl flex flex-col gap-2">
                    <div className="flex items-center gap-2 text-red-400 font-bold text-xs uppercase tracking-wider">
                      <AlertCircle className="size-4" /> Blocked Actions
                    </div>
                    <p className="text-[11px] text-[#CAC4D0] leading-relaxed">
                      Privileged actions, access without active authorization, keystore modification, background stealth downloads, or broad deletions.
                    </p>
                    <div className="bg-[#111116] p-2.5 rounded text-[10px] font-mono text-zinc-500 mt-auto leading-normal">
                      ModifyKeystore()<br/>StealthRecordAudio()<br/>SilentRootAccess()
                    </div>
                  </div>
                </div>

                <div className="bg-[#2B2930] p-4 rounded-xl">
                  <span className="font-bold text-xs text-[#E6E1E5] block mb-1">Demonstrated Safety Execution Pattern</span>
                  <p className="text-xs text-[#938F99] leading-relaxed">
                    When the AI planner generates high-risk plans (such as typing critical credentials or wiping lists), a native Biometric Dialog modal halts execution until confirmed by the user. Try clicking the <b>"Trigger action validation prompt"</b> preset on the main dashboard to test this feature!
                  </p>
                </div>
              </div>
            )}

            {/* Interactive Tab: Logs */}
            {activeSidebarTab === 'logs' && (
              <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl p-5 shadow-sm flex-1 flex flex-col gap-4">
                <div className="flex justify-between items-center">
                  <div>
                    <h2 className="text-sm font-bold text-[#E6E1E5] flex items-center gap-2">
                      <Terminal className="size-4 text-[#D0BCFF]" /> Active Workspace Console Logs
                    </h2>
                    <p className="text-xs text-[#938F99] leading-relaxed mt-1">
                      Persistent real-time console messages from active emulator tasks.
                    </p>
                  </div>

                  <div className="flex gap-2">
                    <button 
                      onClick={exportLogs}
                      className="px-2.5 py-1.5 rounded-lg border border-[#49454F] text-[#CAC4D0] hover:bg-[#2B2930] text-[10px] font-bold flex items-center gap-1 transition"
                    >
                      <Download className="size-3" /> Export Logs
                    </button>
                    <button 
                      onClick={clearLogs}
                      className="px-2.5 py-1.5 rounded-lg border border-[#49454F] text-[#CAC4D0] hover:bg-[#2B2930] text-[10px] font-bold flex items-center gap-1 transition"
                    >
                      <Trash2 className="size-3" /> Clear
                    </button>
                  </div>
                </div>

                <div className="bg-[#0E0D12] border border-[#2B2930] rounded-xl p-4 overflow-y-auto max-h-[380px] font-mono text-xs flex flex-col gap-2 flex-1">
                  {logs.map((log) => {
                    const statusColor = {
                      PLANNER: 'text-[#D0BCFF] bg-[#D0BCFF]/10',
                      DEVICE: 'text-[#E8DEF8] bg-[#4F378B]/40',
                      POLICY: 'text-amber-400 bg-amber-400/10',
                      SYSTEM: 'text-zinc-400 bg-zinc-800',
                      SUCCESS: 'text-green-400 bg-green-500/10',
                      ERROR: 'text-red-400 bg-red-500/10'
                    }[log.source]

                    return (
                      <div key={log.id} className="flex gap-2 items-start border-b border-zinc-900 pb-1.5">
                        <span className="text-zinc-600 select-none text-[10px] shrink-0 mt-0.5">{log.timestamp}</span>
                        <span className={`px-1.5 py-0.5 text-[9px] rounded font-bold uppercase shrink-0 tracking-wide ${statusColor}`}>
                          {log.source}
                        </span>
                        <span className="text-[#E6E1E5] text-[11px] leading-relaxed">{log.message}</span>
                      </div>
                    )
                  })}
                  <div ref={logEndRef} />
                </div>
              </div>
            )}

            {/* Interactive Tab: Settings */}
            {activeSidebarTab === 'settings' && (
              <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl p-5 shadow-sm flex-1 flex flex-col gap-4">
                <div>
                  <h2 className="text-sm font-bold text-[#E6E1E5] flex items-center gap-2">
                    <Settings className="size-4 text-[#D0BCFF]" /> AI Planner Settings & API Keys
                  </h2>
                  <p className="text-xs text-[#938F99] leading-relaxed mt-1">
                    Manage permissions and API credentials safely in local browser storage.
                  </p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {/* Accessibility Box */}
                  <div className="bg-[#2B2930]/50 p-4 rounded-xl flex flex-col justify-between border border-[#49454F]/30">
                    <div>
                      <div className="flex justify-between items-center mb-1">
                        <span className="text-xs font-bold text-white">Accessibility Service</span>
                        <span className={`text-[9px] font-bold px-2 py-0.5 rounded-full ${accessibilityConnected ? 'bg-green-500/10 text-green-400' : 'bg-amber-500/10 text-amber-400'}`}>
                          {accessibilityConnected ? 'Connected' : 'Required'}
                        </span>
                      </div>
                      <p className="text-[11px] text-[#938F99] leading-relaxed">
                        Exposes virtual gestures to traverse simulated widgets, type into inputs, and dispatch Home/Back controls.
                      </p>
                    </div>
                    <button 
                      onClick={() => {
                        setAccessibilityConnected(!accessibilityConnected)
                        addLog('SYSTEM', `Accessibility state modified to: ${!accessibilityConnected}`)
                      }}
                      className="mt-3 text-left text-[11px] text-[#D0BCFF] font-bold hover:underline"
                    >
                      {accessibilityConnected ? 'Disconnect Service' : 'Authorize Connection'}
                    </button>
                  </div>

                  {/* Recording permission box */}
                  <div className="bg-[#2B2930]/50 p-4 rounded-xl flex flex-col justify-between border border-[#49454F]/30">
                    <div>
                      <div className="flex justify-between items-center mb-1">
                        <span className="text-xs font-bold text-white">Audio Recording</span>
                        <span className={`text-[9px] font-bold px-2 py-0.5 rounded-full ${microphoneGranted ? 'bg-green-500/10 text-green-400' : 'bg-red-500/10 text-red-400'}`}>
                          {microphoneGranted ? 'Granted' : 'Required'}
                        </span>
                      </div>
                      <p className="text-[11px] text-[#938F99] leading-relaxed">
                        Required for real-time offline-voice recognition commands inside the local browser simulator.
                      </p>
                    </div>
                    <button 
                      onClick={() => {
                        setMicrophoneGranted(!microphoneGranted)
                        addLog('SYSTEM', `Microphone permission modified to: ${!microphoneGranted}`)
                      }}
                      className="mt-3 text-left text-[11px] text-[#D0BCFF] font-bold hover:underline"
                    >
                      {microphoneGranted ? 'Revoke Permission' : 'Grant Permission'}
                    </button>
                  </div>
                </div>

                <div className="border-t border-[#2B2930] pt-4 flex flex-col gap-4">
                  <div className="flex justify-between items-center">
                    <div>
                      <span className="text-xs font-bold text-[#E6E1E5] block">AI Planner Engine</span>
                      <span className="text-[11px] text-[#938F99]">Configure dynamic Gemini planner API key or default local matching rules.</span>
                    </div>

                    <div className="flex bg-[#2B2930] p-1 rounded-lg border border-[#49454F]">
                      <button 
                        onClick={() => setPlannerMode('offline')}
                        className={`text-[10px] px-2.5 py-1.5 rounded font-bold transition ${plannerMode === 'offline' ? 'bg-[#381E72] text-[#D0BCFF]' : 'text-zinc-400'}`}
                      >
                        Local Matching
                      </button>
                      <button 
                        onClick={() => setPlannerMode('gemini')}
                        className={`text-[10px] px-2.5 py-1.5 rounded font-bold transition ${plannerMode === 'gemini' ? 'bg-[#381E72] text-[#D0BCFF]' : 'text-zinc-400'}`}
                      >
                        Gemini API
                      </button>
                    </div>
                  </div>

                  {plannerMode === 'gemini' && (
                    <div className="bg-[#2B2930]/30 border border-[#49454F]/40 p-4 rounded-xl flex flex-col gap-3">
                      <div className="flex justify-between items-center">
                        <span className="text-xs font-bold text-[#D0BCFF] flex items-center gap-1.5">
                          <Lock className="size-3.5" /> Secure Gemini Credentials
                        </span>
                        <span className="text-[9px] bg-green-500/10 text-green-400 px-2 py-0.5 rounded font-bold">
                          Client-Side Session
                        </span>
                      </div>

                      <input 
                        type="password"
                        placeholder="Paste your Gemini API Key"
                        value={apiKey}
                        onChange={(e) => setApiKey(e.target.value)}
                        className="bg-[#2B2930] border border-[#49454F] text-xs px-3 py-2.5 rounded-lg focus:border-[#D0BCFF] outline-none placeholder-zinc-500 text-white"
                      />
                      <p className="text-[10px] text-[#938F99] leading-normal">
                        API keys are only used for direct browser requests to Google Gemini. Leaving this empty automatically triggers the simulator’s deterministic fallback planner.
                      </p>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Logs console output component */}
            {activeSidebarTab !== 'logs' && (
              <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl overflow-hidden flex flex-col min-h-[220px] shadow-sm">
                <div className="border-b border-[#2B2930] bg-[#17161D] px-4 py-2.5 flex items-center justify-between">
                  <div className="flex gap-2">
                    {[
                      { id: 'agent', label: 'Activity Logs', icon: Activity },
                      { id: 'terminal', label: 'Raw Node Hierarchy', icon: Terminal },
                      { id: 'policy', label: 'Action Policy', icon: FileText }
                    ].map(tab => (
                      <button 
                        key={tab.id}
                        onClick={() => setActiveConsoleTab(tab.id as any)}
                        className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                          activeConsoleTab === tab.id ? 'bg-[#36343B] text-[#D0BCFF]' : 'text-[#CAC4D0] hover:text-white'
                        }`}
                      >
                        <tab.icon className="size-3.5" />
                        {tab.label}
                      </button>
                    ))}
                  </div>

                  <button 
                    onClick={clearLogs}
                    className="text-xs text-zinc-500 hover:text-zinc-200 flex items-center gap-1 font-bold"
                  >
                    <Trash2 className="size-3.5" /> Clear
                  </button>
                </div>

                {activeConsoleTab === 'agent' && (
                  <div className="p-4 flex-1 overflow-y-auto max-h-[180px] font-mono text-[11px] flex flex-col gap-2">
                    {logs.slice(-10).map((log) => {
                      const statusColor = {
                        PLANNER: 'text-[#D0BCFF] bg-[#D0BCFF]/10',
                        DEVICE: 'text-[#E8DEF8] bg-[#4F378B]/40',
                        POLICY: 'text-amber-400 bg-amber-400/10',
                        SYSTEM: 'text-zinc-400 bg-zinc-800',
                        SUCCESS: 'text-green-400 bg-green-500/10',
                        ERROR: 'text-red-400 bg-red-500/10'
                      }[log.source]

                      return (
                        <div key={log.id} className="flex gap-2 items-start border-b border-zinc-900/40 pb-1">
                          <span className="text-zinc-600 select-none text-[9px] shrink-0 mt-0.5">{log.timestamp}</span>
                          <span className={`px-1.5 py-0.5 text-[8px] rounded font-bold uppercase tracking-wide shrink-0 ${statusColor}`}>
                            {log.source}
                          </span>
                          <span className="text-[#E6E1E5] text-[10px] truncate">{log.message}</span>
                        </div>
                      )
                    })}
                  </div>
                )}

                {activeConsoleTab === 'terminal' && (
                  <div className="p-4 flex-1 overflow-y-auto max-h-[180px] bg-[#0E0D12] font-mono text-[10px] text-green-400 leading-relaxed">
                    <span className="text-zinc-500">// Accessibility Node Dump (Simulated Snapshot)</span>
                    <pre className="mt-1">
{`{
  "screenState": "${screenState}",
  "visibleElementsCount": ${inspectorNodes.length},
  "permissions": {
    "BIND_ACCESSIBILITY_SERVICE": ${accessibilityConnected},
    "RECORD_AUDIO": ${microphoneGranted}
  },
  "nodes": ${JSON.stringify(inspectorNodes, null, 2)}
}`}
                    </pre>
                  </div>
                )}

                {activeConsoleTab === 'policy' && (
                  <div className="p-4 flex-1 overflow-y-auto max-h-[180px] flex flex-col gap-2.5 text-xs">
                    <div className="flex items-center gap-2 border-b border-zinc-800 pb-2">
                      <Lock className="size-4 text-amber-400" />
                      <span className="font-bold text-[#E6E1E5]">Action Validation Thresholds</span>
                    </div>
                    <div className="grid grid-cols-2 gap-2 text-[11px] leading-relaxed">
                      <div className="bg-[#2B2930]/40 p-2.5 rounded-lg border border-[#2B2930]">
                        <span className="font-bold text-[#D0BCFF] block mb-0.5">OPEN_APP / CLICK</span>
                        <p className="text-zinc-400">Classified as low-risk. Execution matches screen coordinates safely.</p>
                      </div>
                      <div className="bg-amber-400/5 p-2.5 border border-amber-400/10 rounded-lg">
                        <span className="font-bold text-amber-400 block mb-0.5">TYPE_TEXT / SEND</span>
                        <p className="text-zinc-400">Requires explicit permission grants before string insertion sequences.</p>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}
          </section>

          {/* 4. Visual Android Phone Emulator Frame */}
          <section className="xl:col-span-5 flex flex-col items-center">
            
            {/* Live Agent State Card */}
            <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl p-4 shadow-sm w-full max-w-[340px] mb-4 text-xs flex flex-col gap-2.5">
              <div className="flex justify-between items-center border-b border-[#2B2930] pb-2">
                <span className="font-bold text-[#E6E1E5]">Live Agent State</span>
                <span className="text-[10px] bg-[#6750A4]/20 text-[#D0BCFF] font-bold px-2 py-0.5 rounded-full">
                  Local Simulator
                </span>
              </div>

              <div className="grid grid-cols-2 gap-y-2 gap-x-4">
                <div className="flex flex-col">
                  <span className="text-[10px] text-zinc-500">Agent Status</span>
                  <span className={`font-bold ${isRunning ? 'text-green-400' : 'text-amber-400'}`}>{isRunning ? 'Executing' : 'Ready'}</span>
                </div>
                <div className="flex flex-col">
                  <span className="text-[10px] text-zinc-500">Current Task</span>
                  <span className="font-bold truncate text-[#E6E1E5]">{isRunning ? 'Running plan' : 'None'}</span>
                </div>
                <div className="flex flex-col">
                  <span className="text-[10px] text-zinc-500">Device Connection</span>
                  <span className="font-bold text-[#D0BCFF]">Simulator</span>
                </div>
                <div className="flex flex-col">
                  <span className="text-[10px] text-zinc-500">Accessibility</span>
                  <span className="font-bold text-zinc-400">Simulator Mode</span>
                </div>
                <div className="flex flex-col">
                  <span className="text-[10px] text-zinc-500">Voice Status</span>
                  <span className="font-bold text-[#E6E1E5]">Ready</span>
                </div>
                <div className="flex flex-col">
                  <span className="text-[10px] text-zinc-500">AI Provider</span>
                  <span className="font-bold text-[#D0BCFF] truncate">Mock local fallback</span>
                </div>
              </div>
            </div>

            {/* Hardware Smartphone Wrapper */}
            <div className="relative w-[340px] h-[700px] bg-black rounded-[52px] p-4.5 shadow-2xl border-4 border-[#3c3943] ring-12 ring-[#1D1B20] flex flex-col overflow-hidden">
              
              {/* Camera Notch Hole */}
              <div className="absolute top-0 left-1/2 transform -translate-x-1/2 w-32 h-7 bg-black rounded-b-3xl z-40 flex items-center justify-center">
                <div className="size-2 rounded-full bg-[#111116] border border-zinc-800"></div>
              </div>

              {/* Device Screens Container */}
              <div className="relative flex-1 w-full h-full bg-[#121115] rounded-[42px] overflow-hidden flex flex-col border border-zinc-900">
                
                {/* Android Status Bar */}
                <div className="h-9 bg-black/40 px-6 flex items-center justify-between text-[11px] text-[#CAC4D0] z-30 font-semibold select-none">
                  <span>05:30 PM</span>
                  <div className="flex items-center gap-1.5">
                    {wifiConnected ? <Wifi className="size-3 text-green-400" /> : <Wifi className="size-3 text-zinc-600" />}
                    <Battery className="size-3.5" />
                  </div>
                </div>

                {/* Router viewport */}
                <div className="flex-1 relative flex flex-col">
                  
                  {/* Selected Node Bounding Overlay Highlight (UI Inspector linkage) */}
                  {selectedNode && (
                    <div 
                      style={{
                        position: 'absolute',
                        top: selectedNode.bounds.top,
                        left: selectedNode.bounds.left,
                        width: selectedNode.bounds.width,
                        height: selectedNode.bounds.height,
                        zIndex: 45
                      }}
                      className="border-2 border-dashed border-[#D0BCFF] bg-[#D0BCFF]/10 pointer-events-none animate-pulse rounded-lg"
                    >
                      <div className="absolute -top-4 left-0 bg-[#381E72] text-white text-[8px] px-1 py-0.5 rounded shadow font-bold font-mono">
                        {selectedNode.role}
                      </div>
                    </div>
                  )}

                  {/* SCREEN 1: Companion App screen */}
                  {screenState === 'companion' && (
                    <div className="flex-1 p-4 flex flex-col gap-4 text-xs overflow-y-auto">
                      <div className="flex items-center gap-2 border-b border-[#2B2930] pb-3 pt-2">
                        <Smartphone className="size-5 text-[#D0BCFF]" />
                        <span className="font-bold text-sm text-[#E6E1E5]">AI Android Agent</span>
                      </div>

                      <div className="bg-[#211F26] border border-[#2B2930] rounded-xl p-3 flex flex-col gap-2 shadow">
                        <div className="flex justify-between items-center">
                          <span className="font-bold text-xs text-[#E6E1E5]">Agent State</span>
                          <span className={`text-[9px] font-bold px-2 py-0.5 rounded-full ${isRunning ? 'bg-green-500/10 text-green-400' : 'bg-amber-500/10 text-amber-400'}`}>
                            {isRunning ? 'ACTIVE' : 'READY'}
                          </span>
                        </div>

                        <div className="flex justify-between items-center text-[11px] text-[#938F99] border-t border-[#2B2930]/40 pt-2">
                          <span>Accessibility</span>
                          <span className={accessibilityConnected ? 'text-green-400 font-bold' : 'text-amber-400 font-bold'}>
                            {accessibilityConnected ? 'Connected' : 'Required'}
                          </span>
                        </div>
                        <div className="flex justify-between items-center text-[11px] text-[#938F99]">
                          <span>Microphone</span>
                          <span className={microphoneGranted ? 'text-green-400 font-bold' : 'text-red-400 font-bold'}>
                            {microphoneGranted ? 'Granted' : 'Required'}
                          </span>
                        </div>
                        <div className="flex justify-between items-center text-[11px] text-[#938F99]">
                          <span>Screen capture</span>
                          <span className={screenCaptureConsent ? 'text-green-400 font-bold' : 'text-zinc-500'}>
                            {screenCaptureConsent ? 'Authorized' : 'Required'}
                          </span>
                        </div>
                      </div>

                      <div className="flex flex-col gap-1 bg-[#1D1B20] border border-[#2B2930] p-3 rounded-xl">
                        <span className="text-[10px] text-[#938F99] font-bold">Active Command String</span>
                        <div className="text-[11px] text-[#E6E1E5] break-all">
                          {command || 'No command queued.'}
                        </div>
                      </div>

                      <div className="flex-1 bg-black/25 rounded-xl border border-[#2B2930] p-3 overflow-y-auto text-[10px] font-mono text-[#CAC4D0] min-h-[140px] flex flex-col gap-1.5">
                        <span className="text-[10px] text-[#D0BCFF] font-bold block mb-1">Companion Activity Logs</span>
                        <div>&gt; Service waiting for task queues</div>
                        {isRunning && (
                          <>
                            <div className="text-[#D0BCFF]">&gt; Active planner loop...</div>
                            <div className="text-green-400">&gt; Emulated accessibility clicks...</div>
                          </>
                        )}
                      </div>

                      <div className="flex justify-end pt-2 border-t border-zinc-800/60">
                        <button 
                          onClick={() => {
                            transitionToScreen('home')
                            addLog('DEVICE', 'Minimized companion app launcher.')
                          }}
                          className="bg-[#2B2930] text-[#CAC4D0] font-bold px-3 py-1.5 rounded-lg text-[10px] hover:bg-[#36343B]"
                        >
                          Minimize App
                        </button>
                      </div>
                    </div>
                  )}

                  {/* SCREEN 2: Home Launcher Screen */}
                  {screenState === 'home' && (
                    <div className="flex-1 p-5 flex flex-col justify-between bg-gradient-to-b from-[#1C1625] to-[#0D0A12] text-white">
                      <div className="text-center mt-6 select-none">
                        <span className="text-4xl font-light tracking-tight text-zinc-100">05:30</span>
                        <span className="text-xs text-zinc-400 block mt-1">Mon, Sep 7</span>
                      </div>

                      {/* App grids */}
                      <div className="grid grid-cols-4 gap-y-6 gap-x-2 text-center mt-8">
                        {/* YouTube App icon */}
                        <div className="flex flex-col items-center gap-1">
                          <div 
                            onClick={() => {
                              transitionToScreen('youtube')
                              addLog('DEVICE', 'Click gesture: Opened YouTube app launcher')
                            }}
                            className="size-11 bg-red-600 rounded-2xl flex items-center justify-center shadow-lg cursor-pointer hover:scale-105 active:scale-95 transition"
                          >
                            <Video className="size-6 text-white" />
                          </div>
                          <span className="text-[10px] text-zinc-300 font-medium select-none">YouTube</span>
                        </div>

                        {/* Maps app */}
                        <div className="flex flex-col items-center gap-1">
                          <div 
                            onClick={() => {
                              transitionToScreen('maps')
                              addLog('DEVICE', 'Click gesture: Opened Google Maps app launcher')
                            }}
                            className="size-11 bg-teal-600 rounded-2xl flex items-center justify-center shadow-lg cursor-pointer hover:scale-105 active:scale-95 transition"
                          >
                            <Compass className="size-6 text-white" />
                          </div>
                          <span className="text-[10px] text-zinc-300 font-medium select-none">Maps</span>
                        </div>

                        {/* Weather app */}
                        <div className="flex flex-col items-center gap-1">
                          <div 
                            onClick={() => {
                              transitionToScreen('weather')
                              addLog('DEVICE', 'Click gesture: Opened Weather forecast app launcher')
                            }}
                            className="size-11 bg-sky-500 rounded-2xl flex items-center justify-center shadow-lg cursor-pointer hover:scale-105 active:scale-95 transition"
                          >
                            <CloudSun className="size-6 text-white" />
                          </div>
                          <span className="text-[10px] text-zinc-300 font-medium select-none">Weather</span>
                        </div>

                        {/* Companion app */}
                        <div className="flex flex-col items-center gap-1">
                          <div 
                            onClick={() => {
                              transitionToScreen('companion')
                              addLog('DEVICE', 'Click gesture: Opened AI Companion dashboard')
                            }}
                            className="size-11 bg-[#6750A4] rounded-2xl flex items-center justify-center shadow-lg cursor-pointer hover:scale-105 active:scale-95 transition border border-[#D0BCFF]/30"
                          >
                            <Smartphone className="size-5 text-white" />
                          </div>
                          <span className="text-[10px] text-zinc-300 font-medium select-none">Agent app</span>
                        </div>
                      </div>

                      <div className="flex-1"></div>

                      {/* Dock apps */}
                      <div className="bg-white/5 border border-white/10 p-3 rounded-2xl grid grid-cols-4 gap-1.5 mb-2">
                        {['Settings', 'Chrome', 'Messages', 'Camera'].map((app, idx) => (
                          <div 
                            key={app}
                            onClick={() => {
                              if (app === 'Settings') {
                                transitionToScreen('settings')
                                addLog('DEVICE', 'Opened settings panel.')
                              } else {
                                addLog('DEVICE', `Simulated clicking ${app} app (non-interactive in workspace demo).`)
                              }
                            }}
                            className="size-10 bg-zinc-800 rounded-xl mx-auto flex items-center justify-center cursor-pointer hover:bg-zinc-700 transition"
                          >
                            <span className="text-[9px] text-zinc-300 font-bold">{app[0]}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* SCREEN 3: YouTube View */}
                  {screenState === 'youtube' && (
                    <div className="flex-1 bg-[#0F0F0F] text-white flex flex-col">
                      <div className="px-4 py-2 flex justify-between items-center border-b border-zinc-900">
                        <div className="flex items-center gap-1">
                          <Video className="size-5 text-red-600 fill-current" />
                          <span className="font-bold text-xs">YouTube</span>
                        </div>
                        <div className="w-24 h-6 bg-zinc-800 rounded-full text-[10px] text-zinc-400 flex items-center px-2">
                          {typedInput || searchQuery || 'Search'}
                        </div>
                      </div>

                      <div className="p-3 flex flex-col gap-3 flex-1 overflow-y-auto">
                        {searchQuery ? (
                          <div className="bg-zinc-900 rounded-xl overflow-hidden shadow-lg border border-zinc-800/80">
                            <div className="h-28 bg-zinc-950 flex items-center justify-center relative">
                              <Volume2 className="size-8 text-[#D0BCFF] animate-bounce" />
                              <span className="absolute bottom-1 right-1 bg-black text-[9px] px-1 rounded font-bold">LIVE</span>
                            </div>
                            <div className="p-2">
                              <span className="text-[11px] font-bold block leading-tight">Lofi Study Stream 📚 24/7 Deep Focus Music Radio</span>
                              <span className="text-[9px] text-zinc-500 block mt-1">Lofi Girl • 42k watching</span>
                            </div>
                          </div>
                        ) : (
                          <div className="text-center text-zinc-600 py-16 text-xs">
                            Awaiting search queries...
                          </div>
                        )}
                      </div>
                    </div>
                  )}

                  {/* SCREEN 4: Maps Screen */}
                  {screenState === 'maps' && (
                    <div className="flex-1 bg-zinc-950 text-white flex flex-col relative overflow-hidden">
                      <div className="absolute top-3 left-3 right-3 bg-zinc-800 border border-zinc-700 p-2 rounded-full shadow-lg z-20 flex items-center justify-between text-[11px]">
                        <span className="text-zinc-300 font-semibold ml-2">{typedInput || searchQuery || 'Search in Maps...'}</span>
                        <div className="size-6 rounded-full bg-zinc-700"></div>
                      </div>

                      {/* Mock drawing map layout */}
                      <div className="absolute inset-0 bg-[#1E2530] flex items-center justify-center z-10">
                        <svg width="100%" height="100%" className="opacity-40">
                          <path d="M 0,100 L 400,150 M 100,0 L 150,700 M 0,350 L 400,300" stroke="#4a5568" strokeWidth="5" />
                        </svg>

                        {searchQuery && (
                          <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 flex flex-col items-center">
                            <div className="bg-red-500 text-white px-2 py-0.5 rounded text-[9px] font-bold shadow animate-bounce">
                              Brew Cafe
                            </div>
                            <div className="size-2.5 bg-red-600 rounded-full border border-white"></div>
                          </div>
                        )}
                      </div>

                      {searchQuery && (
                        <div className="absolute bottom-3 left-3 right-3 bg-zinc-800 p-3 rounded-2xl z-20 border border-zinc-700 flex flex-col gap-1">
                          <div className="flex justify-between items-center text-xs">
                            <div>
                              <span className="font-bold text-[#E6E1E5] block">Brew & Brew Cafe</span>
                              <span className="text-[9px] text-zinc-400">12 min route active • Heavy traffic</span>
                            </div>
                            <div className="bg-blue-600 text-white px-2.5 py-1 rounded-lg text-[9px] font-bold">
                              Navigate
                            </div>
                          </div>
                        </div>
                      )}
                    </div>
                  )}

                  {/* SCREEN 5: Weather Screen */}
                  {screenState === 'weather' && (
                    <div className="flex-1 bg-gradient-to-b from-[#2C3E50] to-[#2980B9] text-white p-4 flex flex-col justify-between">
                      <div className="bg-white/10 backdrop-blur-sm rounded-full p-2 text-center text-[10px] select-none">
                        {typedInput || searchQuery || 'Search Location'}
                      </div>

                      {searchQuery ? (
                        <div className="text-center flex-1 flex flex-col justify-center gap-2">
                          <span className="text-4xl font-light">68°F</span>
                          <span className="text-base font-bold">San Francisco</span>
                          <span className="text-[10px] text-zinc-300">Sunny • L: 55° • H: 72°</span>

                          <div className="grid grid-cols-4 gap-1 mt-6 bg-black/15 p-2 rounded-xl text-[9px]">
                            <div>Tue<br/><b>72°</b></div>
                            <div>Wed<br/><b>69°</b></div>
                            <div>Thu<br/><b>68°</b></div>
                            <div>Fri<br/><b>70°</b></div>
                          </div>
                        </div>
                      ) : (
                        <div className="text-center text-zinc-300 text-xs py-16">
                          Enter location to fetch data forecasts.
                        </div>
                      )}

                      <div className="text-center text-[9px] text-zinc-400">Weather statistics API</div>
                    </div>
                  )}

                  {/* SCREEN 6: Settings Screen */}
                  {screenState === 'settings' && (
                    <div className="flex-1 bg-zinc-900 text-white p-4 flex flex-col gap-3">
                      <div className="flex items-center gap-2 border-b border-zinc-800 pb-2">
                        <ArrowLeft 
                          onClick={() => transitionToScreen('home')}
                          className="size-4 text-[#D0BCFF] cursor-pointer hover:scale-105" 
                        />
                        <span className="font-bold text-xs">Settings</span>
                      </div>

                      <div className="flex flex-col gap-1.5 text-xs">
                        {[
                          { id: 'wifi', label: 'Network & Wi-Fi', desc: wifiConnected ? 'Connected to local_access' : 'Disconnected' },
                          { id: 'bt', label: 'Bluetooth Options', desc: 'Disconnected' },
                          { id: 'display', label: 'Display Brightness', desc: '80%' },
                          { id: 'about', label: 'About Device Simulator', desc: 'API level 35 target' }
                        ].map(row => (
                          <div 
                            key={row.id}
                            onClick={() => {
                              if (row.id === 'wifi') {
                                setWifiConnected(!wifiConnected)
                                addLog('DEVICE', `Toggled Wi-Fi connected to: ${!wifiConnected}`)
                              }
                            }}
                            className="bg-zinc-800 hover:bg-zinc-700/80 p-3 rounded-xl cursor-pointer border border-zinc-700/40 flex justify-between items-center transition"
                          >
                            <div>
                              <span className="font-bold text-zinc-100 block">{row.label}</span>
                              <span className="text-[10px] text-zinc-400">{row.desc}</span>
                            </div>
                            <ChevronRight className="size-3 text-zinc-500" />
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* SCREEN 7: Custom/Fallback Screen */}
                  {screenState === 'custom' && (
                    <div className="flex-1 bg-zinc-950 p-6 flex flex-col justify-center items-center text-center text-white">
                      <div className="bg-[#6750A4]/20 p-4 rounded-full border border-[#D0BCFF]/20 mb-3 animate-pulse">
                        <Smartphone className="size-8 text-[#D0BCFF]" />
                      </div>
                      <span className="font-bold text-xs text-[#E6E1E5]">Simulated App Action Activity</span>
                      <p className="text-[10px] text-zinc-500 leading-normal mt-1 max-w-[200px]">
                        The local matching AI sequence successfully dispatched automation.
                      </p>
                      <div className="bg-[#0E0D12] p-2.5 rounded-lg text-left font-mono text-[9px] text-green-400 mt-4 border border-zinc-800 w-full break-all">
                        &gt; DispatchGesture click()<br/>
                        &gt; Read visible layout text<br/>
                        &gt; Status complete
                      </div>
                    </div>
                  )}

                  {/* Animated Finger Pointer Orb (representing agent gestures) */}
                  {pointerVisible && (
                    <div 
                      style={{
                        left: `${pointerPos.x}%`,
                        top: `${pointerPos.y}%`,
                        transition: 'all 1.0s cubic-bezier(0.25, 0.8, 0.25, 1)',
                        zIndex: 50
                      }}
                      className="absolute size-5 bg-orange-500/80 rounded-full border border-white shadow-xl pointer-events-none transform -translate-x-1/2 -translate-y-1/2 flex items-center justify-center"
                    >
                      <div className="size-1.5 bg-white rounded-full animate-ping"></div>
                      <div className="absolute top-6 left-1/2 transform -translate-x-1/2 bg-black text-[9px] text-[#D0BCFF] px-2 py-0.5 rounded shadow-md font-bold whitespace-nowrap">
                        {currentStepText}
                      </div>
                    </div>
                  )}

                  {/* Click touch click ripple */}
                  {rippleVisible && (
                    <div 
                      style={{
                        left: `${ripplePos.x}%`,
                        top: `${ripplePos.y}%`,
                        zIndex: 50
                      }}
                      className="absolute size-10 border-2 border-orange-400 bg-orange-400/20 rounded-full pointer-events-none transform -translate-x-1/2 -translate-y-1/2 animate-ping"
                    />
                  )}

                </div>

                {/* Bottom System Gestures Navigation Bar */}
                <div className="h-8 bg-[#121115] flex items-center justify-center z-30 select-none">
                  <div 
                    onClick={() => {
                      transitionToScreen('companion')
                      addLog('DEVICE', 'Pressed System Home Gesture. Navigated to AI Companion.')
                    }}
                    className="w-28 h-1 bg-zinc-600 rounded-full cursor-pointer hover:bg-zinc-400 transition"
                    title="System Home Gesture Controls"
                  />
                </div>

              </div>
            </div>

            {/* Diagnostics Panel Checklist */}
            <div className="bg-[#1D1B20] border border-[#2B2930] rounded-2xl p-4 shadow-sm w-full max-w-[340px] mt-4">
              <span className="font-bold text-xs text-[#E6E1E5] mb-2 block border-b border-[#2B2930] pb-1.5">Preview Diagnostic Checks</span>
              <div className="flex flex-col gap-1.5 text-[11px] leading-relaxed">
                <div className="flex items-center justify-between">
                  <span className="text-zinc-400">Frontend Preview UI</span>
                  <span className="text-green-400 font-bold flex items-center gap-1">● Healthy</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-zinc-400">Next.js Framework Router</span>
                  <span className="text-green-400 font-bold flex items-center gap-1">● Running</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-zinc-400">Local matching rules</span>
                  <span className="text-green-400 font-bold flex items-center gap-1">● Ready</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-zinc-400">Biometric action policy</span>
                  <span className="text-green-400 font-bold flex items-center gap-1">● Guard active</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-zinc-400">Android Build Settings</span>
                  <span className="text-[#D0BCFF] font-bold">● Standalone Ready</span>
                </div>
              </div>
            </div>
          </section>

        </main>

      </div>

      {/* Confirmation Safety Biometric/User dialog alert modal */}
      {pendingConfirmation && (
        <div className="fixed inset-0 bg-black/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-[#1D1B20] border border-[#2B2930] rounded-3xl max-w-sm w-full p-6 shadow-2xl flex flex-col gap-4 animate-in fade-in zoom-in-95 duration-200">
            <div className="flex items-center gap-3 text-amber-400">
              <AlertCircle className="size-6 shrink-0" />
              <h3 className="text-base font-bold text-[#E6E1E5]">Biometric Security Clearance</h3>
            </div>

            <div className="bg-[#2B2930] p-4 rounded-2xl border border-[#49454F]/40 flex flex-col gap-1 text-xs">
              <span className="font-bold text-[#E6E1E5]">Command Category</span>
              <span className="text-[#D0BCFF] block mb-2">{pendingConfirmation.action.type}</span>

              <span className="font-bold text-[#E6E1E5]">Description</span>
              <p className="text-[#938F99] leading-relaxed mb-2">{pendingConfirmation.action.description}</p>

              {pendingConfirmation.action.details && (
                <>
                  <span className="font-bold text-[#E6E1E5]">Command details</span>
                  <code className="text-[10px] bg-black/30 p-2 rounded text-amber-300 font-mono break-all leading-relaxed">{pendingConfirmation.action.details}</code>
                </>
              )}
            </div>

            <p className="text-[11px] text-[#938F99] leading-normal">
              Confirming this security clearance validates your permission thresholds under com.example.aiandroidagent.security rules.
            </p>

            <div className="flex gap-2 justify-end mt-2 text-xs font-bold">
              <button 
                onClick={() => pendingConfirmation.resolve(false)}
                className="px-4 py-2 border border-[#49454F] rounded-xl text-[#CAC4D0] hover:bg-[#2B2930]"
              >
                Deny Access
              </button>
              <button 
                onClick={() => pendingConfirmation.resolve(true)}
                className="px-4 py-2 bg-[#D0BCFF] text-[#381E72] rounded-xl hover:bg-[#D0BCFF]/90"
              >
                Authorize Action
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Workspace footer */}
      <footer className="border-t border-[#2B2930] bg-[#17161D] py-4 text-center text-xs text-[#938F99] mt-auto">
        <p>© 2026 AI Android Agent Workspace. Built for premium visual simulators with native Kotlin repositories preserved.</p>
      </footer>

    </div>
  )
}
