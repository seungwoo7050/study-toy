// [FILE]
// - 목적: React 애플리케이션의 진입점
// - 주요 역할: React 앱을 DOM에 렌더링하고, StrictMode로 개발 모드 최적화 적용
// - 관련 토이 버전: [FE-F0.1]
// - 권장 읽는 순서: import → createRoot() → render()
//
// [LEARN] React 18의 createRoot API를 사용하여 앱을 렌더링한다.
//         StrictMode는 개발 모드에서 추가 검사를 수행하여 잠재적 문제를 발견한다.

import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)