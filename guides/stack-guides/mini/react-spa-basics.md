# React + SPA 기초 학습 문서

## 0. 이 문서로 어디까지 가는가

최종 목표:

1. Vite(또는 CRA)로 **새 React 프로젝트**를 만들고
2. **컴포넌트 / JSX / props / state**의 개념을 이해하고
3. **간단한 Todo(또는 메모) SPA**를 직접 구현하고
4. `react-router-dom`으로 **기본 라우팅**을 붙이고
5. `fetch` 또는 `axios`로 **간단한 API 호출**을 할 수 있는 수준까지 간다.

이 문서는 이미 한 번 Node.js/Express + TypeScript 기초를 따라가 본 사람을 기준으로 한다.
(굳이 선행은 아니지만, Node 쪽을 먼저 보면 전체 웹 구조를 이해하기가 쉽다.)

---

## 1. React를 배우기 전에 알아두면 좋은 기초

완벽하게 알 필요는 없다. "들어본 적은 있고, 대충 이런 느낌이다" 정도면 충분하다. 모르면 이 문서 보면서 동시에 검색해도 된다.

### 1.1 Node.js 런타임과 패키지 관리자

#### 핵심 개념

* **Node.js**: 브라우저 밖에서 JS 실행하는 런타임
* **npm**: Node.js 기본 패키지 관리자
* **yarn / pnpm**: npm 대체 도구 (속도/기능 차이 정도만 알고 있으면 됨)

#### 설치 확인

```bash
node -v      # v18.x, v20.x 등 나오면 설치된 것
npm -v       # npm 버전
```

문제 없으면 `node`, `npm` 둘 다 버전이 찍힌다.

#### 자주 하는 실수 & 팁

* **여러 버전 섞여 있음**

  * 회사/튜토리얼마다 요구 Node 버전이 다를 수 있음 → `nvm` 같은 버전 관리 도구 고려.
* **npm, yarn 혼용**

  * 한 프로젝트에서 `npm install`, `yarn add` 섞어서 쓰지 말 것 → `node_modules` 꼬인다.
* **전역 설치 vs 로컬 설치 구분 안 됨**

  * CLI 도구(예: `create-react-app`)는 전역/global 설치가 필요할 수도 있음 (`-g` 옵션).

#### FAQ

* Q: npm이랑 Node는 다른 건가요?
  A: Node는 런타임, npm은 그 위에서 돌아가는 패키지 관리자. Node 설치하면 보통 npm도 같이 깔린다.

* Q: yarn/pnpm 꼭 써야 하나요?
  A: 필수 아님. 팀에서 정한 게 없으면 그냥 npm으로 시작해도 된다.

---

### 1.2 터미널(명령 프롬프트) 사용법

React 개발은 터미널에서 명령을 꽤 많이 친다.

#### 최소 명령

```bash
cd my-project       # 디렉터리 이동
ls                  # 목록(Mac/Linux)
dir                 # 목록(Windows)

npm install         # package.json에 있는 의존성 설치
npm run dev         # package.json의 "dev" 스크립트 실행
npm run build       # "build" 스크립트 실행
```

#### 자주 하는 실수 & 팁

* **프로젝트 폴더를 잘못 들어감**

  * `cd`로 이동 후 `ls`/`dir` 해서 `package.json` 있는지 항상 확인.
* **권한 문제**

  * Windows: 관리자 권한 / 보안 프로그램이 막는 경우가 있음.
* **npm run dev를 다른 폴더에서 실행**

  * 에러 메시지에 `missing script: dev` 뜨면 `package.json` 있는 폴더 맞는지 확인.

#### FAQ

* Q: PowerShell, CMD, Git Bash 뭐 써야 하나요?
  A: 아무거나 상관없다. 회사/팀에서 쓰는 거 맞추는 걸 추천.

* Q: dev 서버 끄는 법?
  A: 터미널에서 `Ctrl + C`.

---

### 1.3 웹 기본(HTML/CSS/DOM)

React 자체는 "JS로 컴포넌트 작성 → DOM에 반영해주는 라이브러리"에 불과하다. DOM을 전혀 몰라도 시작은 할 수 있지만, 결국 한 번은 짚어야 한다.

#### HTML 구조

대략 이런 구조만 이해하면 된다:

```html
<!doctype html>
<html>
  <head>
    <meta charset="utf-8" />
    <title>My App</title>
  </head>
  <body>
    <div id="root"></div> <!-- React가 이 div 안에 랜더링 -->
    <script src="/main.js"></script>
  </body>
</html>
```

* `header`, `nav`, `main`, `footer` 같은 **시맨틱 태그** 존재.
* React는 보통 `id="root"` 또는 `id="app"`에 렌더링한다.

#### CSS 기초 – Flex / Grid

* Flex: 1차원(가로/세로) 배치
* Grid: 2차원 배치

예시 (Flex):

```css
.container {
  display: flex;
  gap: 8px;
}

.item {
  flex: 1;
}
```

#### DOM & 개발자 도구

브라우저에서 F12:

* **Elements**: 실제 DOM 확인, 스타일 확인
* **Console**: 에러 메시지, `console.log` 확인

#### 자주 하는 실수 & 팁

* CSS 안 먹을 때 →

  * 파일이 제대로 import 되었는지 / 우선순위(CSS 상속, 구체성) 문제인지 확인.
* DOM 구조랑 React 컴포넌트 구조를 헷갈림

  * React 컴포넌트 트리 ≠ 실제 DOM 구조 1:1. 비슷하지만 엄밀히 같진 않음.

#### FAQ

* Q: CSS 잘 못해도 React 해도 되나요?
  A: 된다. 다만, 결국 UI 만들려면 기본은 필요하다. 일단 기능 위주로 가다가, 나중에 스타일 보완해도 된다.

---

### 1.4 JavaScript(ES6+) 문법

React 코드에서 거의 반드시 쓰는 것만 정리.

#### const/let

```js
const a = 1;   // 재할당 불가
let b = 2;     // 재할당 가능
```

#### 화살표 함수

```js
const add = (a, b) => a + b;
```

#### 구조 분해 / 스프레드

```js
const user = { id: 1, name: "Kim" };
const { id, name } = user;

const arr = [1, 2, 3];
const [first, ...rest] = arr;

const newUser = { ...user, name: "Lee" }; // 복사 + 일부 수정
```

#### 모듈 import/export

```js
// math.js
export function add(a, b) {
  return a + b;
}

// main.js
import { add } from "./math";
```

#### 자주 하는 실수 & 팁

* `this`를 잘못 이해하고 화살표 함수/일반 함수 섞어서 사용 → 초반엔 그냥 함수형 컴포넌트 + 화살표 함수 위주로 가는 게 편하다.
* `undefined`/`null` 체크 안 하고 구조 분해 하다가 에러.

#### FAQ

* Q: JS를 어느 정도 알아야 React를 시작할 수 있나요?
  A: 위 내용(변수, 함수, 객체/배열, import/export)을 코드로 직접 쳐보고 에러 없이 돌아갈 정도면 시작 가능.

---

## 2. React 프로젝트 시작

이 문서는 Vite 기준으로 설명한다. CRA(create-react-app)는 점점 덜 쓰이는 추세라서 Vite 추천.

### 2.1 Vite로 프로젝트 생성

```bash
npm create vite@latest my-react-app -- --template react
cd my-react-app
npm install
```

(Windows PowerShell에서 `-- --template` 부분이 잘 안 먹으면 공식 문서 확인. 기본 아이디어만 알면 된다.)

#### 디렉터리 구조 훑기

대략:

```text
my-react-app/
  index.html
  package.json
  vite.config.js
  src/
    main.jsx
    App.jsx
    assets/
```

* `index.html` : root, React가 들어가는 HTML
* `src/main.jsx` : 최상위 엔트리, ReactDOM.createRoot 호출
* `src/App.jsx` : 메인 컴포넌트

#### 자주 하는 실수 & 팁

* 프로젝트 폴더를 잘못 잡고 `npm install` 다른 곳에서 치는 경우.
* 템플릿을 `react-ts`로 만들고도 TypeScript 문법을 모른 채 시작 → 이 문서는 JS 기준이라 `react` 템플릿으로 가는 걸 가정.

#### FAQ

* Q: Vite가 정확히 뭐 하는 건가요?
  A: 개발 서버 + 번들러. "개발 중 빠르게 코드를 번들해서 브라우저에 보여주는 도구" 정도로 이해하면 된다.

---

### 2.2 개발 서버 실행과 Hot Reload

```bash
npm run dev
```

터미널에 나오는 주소(보통 `http://localhost:5173`)를 브라우저로 들어가면 기본 화면이 뜬다. `src/App.jsx`를 수정하면 브라우저가 자동으로 갱신된다.

#### 자주 하는 실수 & 팁

* 포트 충돌 → 다른 dev 서버가 떠 있으면 `Ctrl + C`로 끄고 다시 실행.
* 코드 수정했는데 반영 안 되면:

  * 브라우저 캐시 강제 새로고침(Ctrl+Shift+R)
  * dev 서버 로그에 에러가 있는지 확인.

#### FAQ

* Q: build는 언제 하나요?
  A: 배포 전. `npm run build` → `dist` 폴더 생성. 지금은 dev만 쓰면 된다.

---

## 3. React 핵심 개념 1: 컴포넌트와 JSX

### 3.1 JSX 기본

JSX = JavaScript + XML 형태 문법.

```jsx
const element = <h1>Hello, world</h1>;
```

* **JSX는 결국 JS로 컴파일**된다. 그래서 JS 문법 규칙을 따른다.
* `class` 대신 `className`, `for` 대신 `htmlFor` 등 HTML과 살짝 다른 점이 있다.

중괄호 `{}` 안에 JS 표현식 사용 가능:

```jsx
const name = "Kim";

function Greeting() {
  return <h1>안녕하세요, {name}님</h1>;
}
```

#### 자주 하는 실수 & 팁

* JSX 안에서 `if`문 직접 사용 불가 → 삼항(`condition ? A : B`) 또는 && 사용.
* 하나의 컴포넌트는 **하나의 부모 요소만 반환**해야 한다.

```jsx
// 잘못된 예
return (
  <h1>Title</h1>
  <p>내용</p>
);

// 올바른 예
return (
  <>
    <h1>Title</h1>
    <p>내용</p>
  </>
);
```

#### FAQ

* Q: JSX를 반드시 써야 하나요?
  A: 아니지만, 사실상 React는 JSX 전제라고 보면 된다.

---

### 3.2 함수형 컴포넌트

기본 형태:

```jsx
function App() {
  return <div>My App</div>;
}

export default App;
```

또는:

```jsx
const App = () => {
  return <div>My App</div>;
};
```

컴포넌트 파일 분리:

```jsx
// src/components/Header.jsx
export function Header() {
  return <header>헤더</header>;
}

// src/App.jsx
import { Header } from "./components/Header";

function App() {
  return (
    <div>
      <Header />
      <main>내용</main>
    </div>
  );
}

export default App;
```

#### 자주 하는 실수 & 팁

* 컴포넌트 이름은 **대문자로 시작**해야 한다 (`<app />` X, `<App />` O).
* export / import 경로 잘못 쓰는 경우 많음 → 상대 경로(`./`, `../`) 신경 쓸 것.

#### FAQ

* Q: 클래스형 컴포넌트는 안 배워도 되나요?
  A: 초반에는 안 봐도 무방. 요즘 코드베이스는 함수형 + hooks가 대부분.

---

### 3.3 props

부모 → 자식 데이터 전달.

```jsx
// Greeting.jsx
export function Greeting({ name }) {
  return <p>안녕하세요, {name}님</p>;
}

// App.jsx
import { Greeting } from "./Greeting";

function App() {
  return (
    <div>
      <Greeting name="승우" />
      <Greeting name="지민" />
    </div>
  );
}
```

구조 분해 없이 쓸 수도 있다:

```jsx
export function Greeting(props) {
  return <p>안녕하세요, {props.name}님</p>;
}
```

#### 실습

* `Greeting` 컴포넌트를 직접 만들고, `name` props를 바꿔가며 여러 번 렌더링해본다.
* `message` 같은 props 추가해서 문구를 바꿔본다.

#### 자주 하는 실수 & 팁

* props는 "읽기 전용"이다. 자식에서 props를 직접 바꾸지 말 것.
* "부모가 상태 관리, 자식은 표시만" 하는 패턴이 기본.

#### FAQ

* Q: props랑 state 차이?
  A: props는 "외부에서 주입되는 값", state는 "컴포넌트 내부에서 관리하는 값".

---

## 4. React 핵심 개념 2: state와 이벤트 처리

### 4.1 useState로 상태 관리

```jsx
import { useState } from "react";

function Counter() {
  const [count, setCount] = useState(0); // 초기값 0

  return (
    <div>
      <p>현재 값: {count}</p>
      <button onClick={() => setCount(count + 1)}>+1</button>
    </div>
  );
}
```

* `useState`는 [값, 값을 바꾸는 함수]를 반환.
* 상태가 바뀌면 해당 컴포넌트가 다시 렌더링된다.

#### 자주 하는 실수 & 팁

* `setCount(count + 1)`를 연속으로 여러 번 호출할 때 → 최신 값을 기준으로 하려면 함수형 업데이트 사용:

```jsx
setCount((prev) => prev + 1);
```

* state를 직접 변경하면 안 된다:

```jsx
// X
count = count + 1;

// O
setCount(count + 1);
```

#### FAQ

* Q: state는 어디에 두는 게 맞나요?
  A: "해당 데이터가 필요한 컴포넌트 중 가장 상위"에 둔다(상태 끌어올리기 패턴).

---

### 4.2 이벤트 핸들러

기본 패턴:

```jsx
function Button() {
  const handleClick = () => {
    console.log("clicked");
  };

  return <button onClick={handleClick}>클릭</button>;
}
```

인자 전달:

```jsx
<button onClick={() => handleClick(1)}>1번</button>
```

#### 실습 – Counter

* 버튼 두 개: `+1`, `-1`
* 0 밑으로는 내려가지 않게 처리해보기.

#### 자주 하는 실수 & 팁

* `onClick={handleClick()}`처럼 괄호 붙여서 **바로 실행**하는 실수 → `onClick={handleClick}` 혹은 `() => handleClick()` 사용.
* 이벤트 핸들러에서 `event.preventDefault()`를 적절히 쓰지 않아 form이 새로고침 되는 경우 있음.

#### FAQ

* Q: 이벤트 객체는 어떻게 받나요?
  A: `const handleChange = (event) => { ... }` 식으로 첫 인자로 온다.

---

### 4.3 리스트 렌더링과 key

```jsx
const todos = [
  { id: 1, text: "React 공부" },
  { id: 2, text: "운동" },
];

function TodoList() {
  return (
    <ul>
      {todos.map((todo) => (
        <li key={todo.id}>{todo.text}</li>
      ))}
    </ul>
  );
}
```

* `key`는 React가 요소를 구분하기 위한 식별자.
* 가능하면 고유 ID 사용. 인덱스(index)를 key로 쓰면 재정렬/삭제 시 문제 생길 수 있다.

#### 실습

* `useState`로 `todos` 상태를 만들고, `map`으로 렌더링해본다.
* 삭제 버튼을 붙이고, 삭제 시 배열에서 제거해보기.

#### 자주 하는 실수 & 팁

* key를 안 주거나, index를 무조건 key로 쓰는 습관.
* key는 JSX 상에서만 쓰이고, 실제 DOM 속성으로는 안 쓰인다고 생각해도 된다.

#### FAQ

* Q: key 값이 중복되면 어떻게 되나요?
  A: React가 요소를 잘못 재사용해서 이상한 UI 버그 발생. 경고도 뜬다.

---

## 5. 간단한 Todo/메모 앱 (JS 버전)

### 5.1 요구사항 정의

* 할 일 추가
* 할 일 삭제
* (옵션) 완료 여부 토글
* 새로고침하면 초기화 (지금은 로컬 상태만)

### 5.2 컴포넌트 설계

예시:

* `App` : 전체 페이지, todo 상태 관리
* `TodoInput` : 입력 + 추가 버튼
* `TodoList` : 리스트 전체
* `TodoItem` : 개별 아이템(텍스트, 삭제 버튼, 완료 토글)

상태:

* `App`에서 todos 관리:

```jsx
const [todos, setTodos] = useState([]);
```

* `TodoInput`은 입력값만 로컬 state로 갖고, submit 시 부모(App)에 콜백으로 전달.

### 5.3 폼과 제어 컴포넌트

```jsx
function TodoInput({ onAdd }) {
  const [text, setText] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!text.trim()) return;
    onAdd(text);
    setText("");
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="할 일을 입력하세요"
      />
      <button type="submit">추가</button>
    </form>
  );
}
```

* `input`의 `value`와 `onChange`를 state와 연결 → **제어 컴포넌트**.
* form의 submit 이벤트에서 `e.preventDefault()`로 새로고침 막기.

#### 자주 하는 실수 & 팁

* input의 `value`를 state와 안 묶고, uncontrolled로 쓰다가 동작 헷갈림.
* `name`/`id` 등 HTML 속성과 React props를 섞어 쓰는 실수.

#### FAQ

* Q: 제어 컴포넌트가 꼭 필요한가요?
  A: React에서 폼을 제대로 다루려면 결국 필요해진다. state 기반으로 값이 관리되는 구조가 기본.

---

## 6. 라우팅 기초 (react-router-dom)

### 6.1 SPA와 라우팅 개념

* **MPA**: 페이지 이동마다 서버에서 새로운 HTML 응답.
* **SPA**: 초기 한 번 로드 후, JS가 내부에서 화면 전환.

클라이언트 사이드 라우팅:

* URL은 바뀌지만, 실제로는 JS가 컴포넌트만 바꿔서 보여준다.

#### 자주 하는 실수 & 팁

* SPA인데 서버가 모든 경로를 `/index.html`로 리다이렉트 안 해주면, 새로고침 시 404가 날 수 있다. (개발 환경에서는 Vite가 처리.)

---

### 6.2 기본 설정

설치:

```bash
npm install react-router-dom
```

엔트리 구성 (예시):

```jsx
// main.jsx
import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";

ReactDOM.createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
```

```jsx
// App.jsx
import { Routes, Route } from "react-router-dom";
import TodoPage from "./pages/TodoPage";
import AboutPage from "./pages/AboutPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<TodoPage />} />
      <Route path="/about" element={<AboutPage />} />
    </Routes>
  );
}

export default App;
```

#### 자주 하는 실수 & 팁

* `BrowserRouter`를 App 안에 또 감싸는 등 중복으로 감싸지 말 것.
* `Route`의 `element`에는 `<Component />`를 넣어야 한다. `component={}`(v5 문법)랑 헷갈림.

#### FAQ

* Q: HashRouter는 뭔가요?
  A: URL에 `#/` 붙는 방식. 서버 설정 없이도 SPA 라우팅이 가능하지만, 요즘은 주로 BrowserRouter를 쓰고 서버에서 404를 SPA로 돌리는 방식 사용.

---

### 6.3 링크와 상세 페이지

링크:

```jsx
import { Link } from "react-router-dom";

function Nav() {
  return (
    <nav>
      <Link to="/">Todo</Link>
      {" | "}
      <Link to="/about">About</Link>
    </nav>
  );
}
```

상세 페이지 (URL 파라미터):

```jsx
// routes: <Route path="/todos/:id" element={<TodoDetailPage />} />

import { useParams } from "react-router-dom";

function TodoDetailPage() {
  const { id } = useParams(); // 문자열

  return <div>Todo 상세 페이지, id: {id}</div>;
}
```

프로그래매틱 네비게이션:

```jsx
import { useNavigate } from "react-router-dom";

const navigate = useNavigate();

const goToAbout = () => {
  navigate("/about");
};
```

#### 자주 하는 실수 & 팁

* `Link` 대신 `<a href>`를 사용하면 전체 페이지가 새로고침됨 → SPA 의미가 사라진다.
* `useParams` 값은 항상 문자열이므로 필요하면 `Number(id)`로 변환.

#### FAQ

* Q: 쿼리스트링은 어떻게 읽나요?
  A: `useSearchParams` 훅 사용. 필요할 때 공식 문서 참고.

---

## 7. API 연동(기본)

Todo 앱을 "API와 연동된 버전"으로 확장한다고 가정.

### 7.1 fetch/axios로 HTTP 호출

#### 기본 개념

* REST API: URL + HTTP 메서드로 리소스 CRUD
* 프론트에서 JS로 `GET/POST/DELETE`를 날려서 서버와 통신.

#### fetch 예시

```jsx
async function fetchTodos() {
  const res = await fetch("http://localhost:3000/notes");
  if (!res.ok) {
    throw new Error("Failed to fetch");
  }
  return res.json();
}
```

컴포넌트에서 사용 (최소한의 `useEffect` 소개):

```jsx
import { useEffect, useState } from "react";

function TodoPage() {
  const [todos, setTodos] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    setIsLoading(true);
    setError(null);

    fetch("http://localhost:3000/notes")
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch");
        return res.json();
      })
      .then((data) => setTodos(data))
      .catch((err) => setError(err.message))
      .finally(() => setIsLoading(false));
  }, []);

  // 렌더링 부분에서는 isLoading, error, todos 사용
}
```

axios 예시:

```bash
npm install axios
```

```jsx
import axios from "axios";

async function fetchTodos() {
  const res = await axios.get("http://localhost:3000/notes");
  return res.data;
}
```

#### 자주 하는 실수 & 팁

* CORS 에러 → 서버에서 CORS 허용 설정 필요. 프론트 문제라기보단 서버 설정 이슈.
* `useEffect`의 dependency 배열을 잘못 설정해서 무한 요청 → 초반엔 "마운트 시 1번만"이라면 `[]` 고정.

#### FAQ

* Q: fetch랑 axios 뭐가 다른가요?
  A: axios가 편의 기능이 많다. 하지만 기본 개념은 같다. 하나만 제대로 익혀도 충분.

---

### 7.2 로딩/에러 상태 관리

패턴:

```jsx
if (isLoading) return <p>로딩 중...</p>;
if (error) return <p>에러: {error}</p>;
return <TodoList todos={todos} />;
```

* 최소한 **로딩/에러** 두 상태는 항상 고려하는 습관을 들여라.

#### 자주 하는 실수 & 팁

* 에러를 전혀 처리하지 않으면 사용자 입장에서 "아무 것도 안 나옴".
* 로딩 스피너/문구 없어도 최소한 텍스트로라도 표시.

#### FAQ

* Q: 로딩/에러 상태가 너무 자주 필요한데, 매번 state 세 개씩 만드는 게 귀찮습니다.
  A: 그게 맞다. 그래서 React Query 같은 라이브러리가 나온다. 이 문서에서는 기본 패턴까지만.

---

## 8. 폴더 구조 분리

### 8.1 기본 구조

초기에는 전부 `src/App.jsx`에 때려 넣어도 돌아가긴 한다. 하지만 조금만 커져도 유지보수 불가능.

예시 구조:

```text
src/
  main.jsx
  App.jsx
  components/
    TodoInput.jsx
    TodoList.jsx
    TodoItem.jsx
    common/
      Button.jsx
      Input.jsx
  pages/
    TodoPage.jsx
    AboutPage.jsx
  hooks/
    useTodos.js      # 커스텀 훅 (선택)
  api/
    client.js        # fetch/axios 래퍼
    todos.js         # todos 관련 API 함수
```

#### 자주 하는 실수 & 팁

* `components`에 모든 걸 다 때려 넣는 구조 → 페이지, 레이아웃, 도메인 컴포넌트 등을 적당히 분리.
* 상대 경로가 너무 길어지면(`../../../components/...`) → alias 설정 or 폴더 구조 재검토.

---

### 8.2 재사용 가능한 UI 컴포넌트

예시 `Button`:

```jsx
// components/common/Button.jsx
export function Button({ children, ...rest }) {
  return (
    <button
      style={{ padding: "4px 8px", borderRadius: 4, border: "1px solid #ccc" }}
      {...rest}
    >
      {children}
    </button>
  );
}
```

이제 여러 곳에서 공통 스타일로 사용:

```jsx
import { Button } from "../components/common/Button";

<Button onClick={...}>추가</Button>
```

#### 자주 하는 실수 & 팁

* 너무 초반부터 "디자인 시스템" 만든다고 추상화에 과투자 → 실제 사용하는 패턴이 어느 정도 반복되는 시점에 공용 컴포넌트로 추출하는 게 낫다.

---

## 9. 간단한 상태 관리 확장(선택)

### 9.1 Context API

props drilling 문제:

* `App` → `Layout` → `Sidebar` → `ThemeToggle` 이렇게 깊게 이어지는데, 가장 아래에서만 필요한 값(예: theme)을 모든 컴포넌트가 props로 전달해야 하는 문제.

Context 패턴:

```jsx
// ThemeContext.js
import { createContext, useContext, useState } from "react";

const ThemeContext = createContext();

export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState("light");

  const value = {
    theme,
    toggle: () => setTheme((t) => (t === "light" ? "dark" : "light")),
  };

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

export function useTheme() {
  return useContext(ThemeContext);
}
```

사용:

```jsx
// main.jsx
import { ThemeProvider } from "./ThemeContext";

ReactDOM.createRoot(...).render(
  <ThemeProvider>
    <App />
  </ThemeProvider>
);
```

```jsx
// 어떤 컴포넌트
import { useTheme } from "../ThemeContext";

function ThemeToggleButton() {
  const { theme, toggle } = useTheme();
  return <button onClick={toggle}>현재: {theme}</button>;
}
```

#### 자주 하는 실수 & 팁

* Context를 너무 남발하면 오히려 구조가 복잡해진다.
* "여러 컴포넌트에서 자주 공유하는, 비교적 변동이 적은 값"에 사용하는 게 적당.

#### FAQ

* Q: Context vs 전역 상태 라이브러리(Redux 등)?
  A: Context는 전역 값 전달 도구일 뿐이고, 복잡한 상태 관리 로직까지 대신해 주진 않는다. 작은 앱은 Context만으로도 충분하다.

---

## 10. 체크리스트

### 10.1 예시 체크 항목

아래 항목에 대해 "예/아니오"를 스스로 체크해보면 된다.

|  체크 | 항목                                                               |
| :-: | :--------------------------------------------------------------- |
|     | 1. Vite로 새 React 프로젝트를 생성하고 dev 서버를 띄울 수 있다                      |
|     | 2. JSX, 함수형 컴포넌트, props 개념을 설명할 수 있다                             |
|     | 3. `useState`로 상태를 만들고, 버튼/입력 이벤트로 상태를 바꿀 수 있다                   |
|     | 4. 배열을 `map`으로 렌더링하고, 적절한 `key`를 줄 수 있다                          |
|     | 5. Todo/메모 앱을 직접 구현해서 브라우저에서 동작시키고 있다                            |
|     | 6. `react-router-dom`으로 최소 두 페이지(`/`, `/about`)를 가진 SPA를 만들 수 있다 |
|     | 7. `fetch` 또는 `axios`로 간단한 HTTP API를 호출해서 데이터를 화면에 표시할 수 있다      |
|     | 8. 로딩/에러 상태를 구분해서 UI에 반영할 수 있다                                   |
|     | 9. `components/pages/api` 등으로 기본 폴더 구조를 나누어 유지보수 가능하게 만들 수 있다    |
|     | 10. Context를 이용해 테마 토글 같은 전역 상태를 관리하는 간단 예제를 만들 수 있다             |

### 10.2 추가로 공부하면 좋은 키워드

* React + TypeScript (컴포넌트에 타입 붙이기)
* React Query / SWR (데이터 패칭/캐싱 라이브러리)
* Redux Toolkit / Zustand / Jotai (상태 관리)
* 스타일링

  * CSS Module
  * styled-components
  * Emotion
  * Tailwind CSS
* 성능 이슈

  * React.memo
  * useMemo / useCallback
* 테스트

  * React Testing Library
  * Jest

여기까지 끝냈다면, "React + SPA 입문" 레벨은 확보한 것이다.
이후부터는 실제로 필요한 기능을 정해서, Todo 수준을 넘어가는 작은 사이드 프로젝트를 하나 만드는 게 좋다. (ex. 메모 + 태그, 간단한 게시판, 간단한 영화 검색 등)
