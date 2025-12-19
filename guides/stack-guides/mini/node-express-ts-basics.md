# Node.js / Express + TypeScript 기초 학습 문서

## 0. 이 문서로 어디까지 가는가

최종 목표:

1. Node.js 런타임과 비동기 방식 이해
2. Express로 **간단한 REST API 서버** 만들기
3. TypeScript를 붙여서 **타입이 있는 Express 서버**로 바꾸기
4. **폴더 구조(routes/services/models)**로 레이어 분리
5. PostgreSQL을 붙여서 **실제 DB에 CRUD 하는 Notes API** 만들기

학습 대상 프로젝트 이름: `notes-api` (메모 앱 API)

---


## 1. Node.js/Express/TypeScript를 배우기 전에 알아두면 좋은 기초

이 문서를 따라가기 전에 아래 내용을 한 번쯤 경험해봤다면 훨씬 수월하게 학습할 수 있습니다. 완벽히 알 필요는 없고, "이런 게 있구나" 정도만 익혀도 충분합니다.

### 1.1 자바스크립트(JS) 기본 문법

- 변수 선언: `let`, `const`
- 함수 선언과 호출
- 조건문: `if`, `else`, `switch`
- 반복문: `for`, `while`

예시:

```js
const name = "Kim";
function greet(user) {
  if (user) {
    console.log("Hello, " + user);
  } else {
    console.log("Hello, guest");
  }
}
greet(name);
```

### 1.2 ES6(이후) 문법

- 화살표 함수: `(a, b) => a + b`
- 구조 분해 할당: `const { a, b } = obj;`, `const [x, y] = arr;`
- 템플릿 문자열: `` `Hello, ${name}` ``

예시:

```js
const add = (a, b) => a + b;
const user = { id: 1, name: "Kim" };
const { name } = user;
console.log(`이름: ${name}`);
```

### 1.3 터미널(명령 프롬프트) 사용법

- 폴더 이동: `cd my-folder`
- 파일 목록 보기: `ls` (Mac/Linux), `dir` (Windows)
- 파일 실행: `node 파일명.js`
- 패키지 설치: `npm install 패키지명`

예시:

```bash
cd notes-api
ls
npm install express
node index.js
```

### 1.4 npm과 모듈 시스템

- `npm`은 Node.js의 패키지(라이브러리) 관리 도구입니다.
- 외부 라이브러리 설치, 프로젝트 초기화 등에 사용합니다.
- JS 파일에서 `require` 또는 `import`로 다른 파일/패키지를 불러올 수 있습니다.

예시:

```js
// math.js
function add(a, b) { return a + b; }
module.exports = { add };

// main.js
const { add } = require("./math");
console.log(add(2, 3));
```

---

## 2. Node.js 기본


### 2.1 Node 런타임이란?

- **Node.js는 브라우저 밖에서 자바스크립트를 실행하는 프로그램(런타임)입니다.**
- 파일, 네트워크, 데이터베이스 등 **서버 개발에 필요한 기능(API)**을 제공합니다.

#### 비유: 브라우저 vs Node.js

- 브라우저: JS로 웹페이지를 꾸미고, 버튼 클릭 등 사용자와 상호작용
- Node.js: JS로 서버를 만들고, 파일을 읽거나 DB에 접근, 네트워크 통신 등

#### 실습: Node.js 실행해보기

```bash
node -v           # Node.js 버전 확인
node              # Node.js REPL(실행창) 진입
> 1 + 2
3
> console.log("hello node");
hello node
> .exit           # 종료
```

#### 자주 묻는 질문(FAQ)

- Q: Node.js는 왜 필요한가요?
  - A: JS를 서버 개발에도 쓸 수 있게 해줍니다. (웹서버, CLI, 배치 등)
- Q: Node.js로 프론트엔드도 만들 수 있나요?
  - A: 직접 화면을 그리진 않지만, 빌드 도구/서버/테스트 등 다양한 곳에 쓰입니다.

---


### 2.2 비동기 I/O와 이벤트 루프

Node.js가 서버에 적합한 진짜 이유:

- **파일/DB/네트워크 작업을 비동기(논블로킹)로 처리** → 동시에 많은 요청을 빠르게 처리 가능
- JS 코드는 한 번에 한 줄씩(싱글스레드) 실행, I/O는 OS에 맡기고, 끝나면 콜백/Promise로 이어감

#### 그림 비유

- "주방장 1명이 주문만 받고, 요리는 기계(운영체제)가 다 해줌. 요리 끝나면 주방장에게 알려줌."

#### 실습 예제

```js
// async-demo.js
const fs = require("fs");

console.log("A");

fs.readFile(__filename, "utf8", (err, data) => {
  if (err) {
    console.error(err);
    return;
  }
  console.log("B length =", data.length);
});

console.log("C");
```

```bash
node async-demo.js
# 출력 순서: A, C, B (B는 파일 읽기 끝난 뒤)
```

#### 자주 하는 실수 & 팁

- `for` 반복문 안에서 비동기 함수 쓸 때, 순서가 꼬일 수 있음
- 콜백 지옥 → Promise/async-await로 개선 가능

#### FAQ

- Q: Node.js는 멀티스레드인가요?
  - A: 기본적으로 싱글스레드지만, I/O는 OS가 처리해서 동시에 여러 작업이 가능합니다.
- Q: 비동기 코드를 동기처럼 쓰고 싶어요!
  - A: `async/await` 문법을 사용하세요.

---

### 2.3 CommonJS 모듈 & npm

모듈 분리:

```js
// math.js
function add(a, b) {
  return a + b;
}

module.exports = { add };
```

```js
// main.js
const { add } = require("./math");
console.log(add(2, 3));
```

npm:

```bash
mkdir notes-api
cd notes-api
npm init -y          # package.json 생성
npm install express
```

---

## 3. HTTP / REST 기본

### 3.1 HTTP 요청/응답이란?

웹에서 데이터를 주고받는 기본 규칙입니다. 서버와 클라이언트(브라우저, 앱 등)가 서로 대화할 때 사용합니다.

#### 요청(Request) 구성
- **메서드**: 동작의 종류 (GET, POST, PUT, DELETE 등)
- **URL**: 자원의 위치(`/notes`, `/notes/1` 등)
- **헤더**: 부가 정보 (`Content-Type`, `Authorization` 등)
- **바디**: 실제 데이터 (주로 JSON)

예시:
```http
POST /notes HTTP/1.1
Host: localhost:3000
Content-Type: application/json

{
  "text": "메모 내용"
}
```

#### 응답(Response) 구성
- **상태 코드**: 결과를 숫자로 표현 (200, 201, 400, 404, 500 등)
- **헤더**: 부가 정보
- **바디**: 실제 데이터 (JSON, HTML 등)

예시:
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "text": "메모 내용"
}
```

#### 자주 하는 실수 & 팁
- POST/PUT 요청 시 `Content-Type`을 꼭 지정해야 함 (예: `application/json`)
- 상태 코드 200/201/400/404/500의 의미를 헷갈릴 수 있음 → 아래 표 참고

| 코드 | 의미           | 언제 사용?                |
|------|----------------|--------------------------|
| 200  | 성공           | GET 등 일반 성공         |
| 201  | 생성됨         | POST로 새 리소스 생성    |
| 400  | 잘못된 요청    | 필수값 누락, 타입 오류 등|
| 404  | 없음           | 리소스가 없을 때         |
| 500  | 서버 내부 오류 | 예외, 버그 등            |

#### FAQ
- Q: GET/POST/PUT/DELETE 차이?
  - A: GET(조회), POST(생성), PUT(수정), DELETE(삭제)로 구분합니다.

---

### 3.2 REST란?

REST는 "리소스(데이터)를 URL로 표현하고, HTTP 메서드로 동작을 구분"하는 설계 방식입니다.

#### 실전 예시: 메모 앱 REST 설계

- `GET /notes` : 메모 목록 조회
- `POST /notes` : 메모 생성
- `DELETE /notes/:id` : 메모 삭제

#### 실전 팁
- URL은 명사(리소스)로, 동작은 HTTP 메서드로 표현
- 복잡한 동작(검색, 필터 등)은 쿼리스트링 사용: `/notes?author=kim`

#### 자주 하는 실수
- URL에 동사를 넣는 것: `/getNotes` (X), `/notes` (O)
- POST/PUT/DELETE 요청에 body를 안 보내거나, JSON 형식이 잘못됨

---

---

## 4. Express 기본 (JavaScript 버전)

### 4.1 Express란?

- Node.js에서 가장 널리 쓰이는 **웹 서버 프레임워크**입니다.
- 라우팅, 미들웨어, 에러처리 등 웹 서버에 필요한 기능을 쉽게 구현할 수 있습니다.

#### 주요 개념
- **라우터**: URL/메서드별로 동작을 나눔 (예: GET /, POST /notes)
- **미들웨어**: 요청/응답을 가로채서 처리하는 함수 (ex: body 파싱, 에러 처리)
- **req/res 객체**: 요청(req), 응답(res) 정보를 담은 객체

---

### 4.2 헬로 월드 서버 만들기

프로젝트 루트: `notes-api`

```bash
npm install express
npm install --save-dev nodemon
```

`package.json`에 스크립트 추가:

```jsonc
{
  "scripts": {
    "dev": "nodemon index.js"
  }
}
```

`index.js`:

```js
const express = require("express");
const app = express();

// 미들웨어: JSON body 파싱
app.use(express.json());

// 라우터: GET /
app.get("/", (req, res) => {
  res.send("Hello, Express");
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
```

```bash
npm run dev
# 브라우저에서 http://localhost:3000 접속
```

#### 실전 팁 & 자주 하는 실수
- 미들웨어(`app.use`)를 꼭 라우터보다 먼저 등록해야 함
- req.body를 쓰려면 `express.json()` 미들웨어가 필요
- 포트 충돌(이미 사용 중) 오류가 자주 발생 → 다른 포트로 변경

#### FAQ
- Q: 미들웨어란?
  - A: 요청/응답을 가로채서 가공하거나, 공통 처리를 하는 함수입니다. (ex: body 파싱, 로깅, 에러처리)
- Q: req, res는 뭔가요?
  - A: req는 요청 정보(파라미터, body 등), res는 응답을 만드는 객체입니다.

---


### 4.3 간단한 Notes API (JS 버전)


`index.js`를 아래처럼 바꿔보세요:


```js
const express = require("express");
const app = express();

app.use(express.json()); // JSON body 파싱

let notes = [];
let nextId = 1;

// GET /notes: 메모 목록 조회
app.get("/notes", (req, res) => {
  res.json(notes);
});

// POST /notes: 메모 생성
app.post("/notes", (req, res) => {
  const { text } = req.body;

  if (typeof text !== "string" || text.trim() === "") {
    return res.status(400).json({ error: "text는 비어있지 않은 문자열이어야 합니다." });
  }

  const newNote = { id: nextId++, text };
  notes.push(newNote);
  res.status(201).json(newNote);
});

// DELETE /notes/:id: 메모 삭제
app.delete("/notes/:id", (req, res) => {
  const id = Number(req.params.id);
  const index = notes.findIndex((n) => n.id === id);

  if (index === -1) {
    return res.status(404).json({ error: "메모를 찾을 수 없습니다." });
  }

  const deleted = notes.splice(index, 1)[0];
  res.json(deleted);
});

// 404 처리 미들웨어
app.use((req, res) => {
  res.status(404).json({ error: "Not Found" });
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
```



#### 빠른 테스트 (cURL로 바로 확인)

서버 실행(`npm run dev`) 후 아래를 순서대로 실행해보면 동작이 바로 검증됩니다.

```bash
# 목록 조회 (처음엔 빈 배열)
curl http://localhost:3000/notes

# 메모 생성
curl -X POST http://localhost:3000/notes \
  -H "Content-Type: application/json" \
  -d '{"text":"첫 메모"}'

# 다시 목록 조회
curl http://localhost:3000/notes

# 삭제 (id는 위 생성 응답에서 확인)
curl -X DELETE http://localhost:3000/notes/1
```
#### 실전 팁 & 자주 하는 실수
- req.body가 undefined면 `express.json()` 미들웨어가 빠진 것!
- 라우터 순서가 중요: 404 미들웨어는 항상 마지막에
- 숫자 파라미터(`req.params.id`)는 Number로 변환해서 사용

#### FAQ
- Q: 미들웨어에서 next()는 언제 쓰나요?
  - A: 에러 처리, 다음 미들웨어로 넘길 때 사용합니다.

---

여기까지가 **Node + Express 기본**.

---

## 5. TypeScript 기초 (백엔드에 필요한 만큼만)

여기서부터는 같은 프로젝트를 TypeScript로 옮깁니다.

### 5.1 TypeScript에서 꼭 알아야 할 기본 개념

1. **타입 주석**

```ts
let age: number = 29;
let name: string = "Kim";
let isAdmin: boolean = false;
```

2. **인터페이스 / 타입 별칭**

```ts
interface User {
  id: number;
  name: string;
}

type NoteId = number;
```

3. **선택적 프로퍼티, 유니온 타입**

```ts
interface Note {
  id: number;
  text: string;
  author?: string; // 있어도 되고 없어도 됨
}

type Id = number | string;
```

4. **함수 타입**

```ts
function add(a: number, b: number): number {
  return a + b;
}

const double: (x: number) => number = (x) => x * 2;
```

5. **타입 추론**

```ts
const n = 1;         // number (타입을 안 써도 자동 추론)
const s = "hello";   // string
```

6. **any, unknown 타입**

- `any`는 아무 타입이나 허용(타입 검사 안 함, 남용 주의)
- `unknown`은 타입을 모를 때 사용, 실제 쓸 땐 타입 체크 필요

7. **Promise / async 함수 타입**

```ts
async function fetchUser(id: number): Promise<User> {
  // ...
  return { id, name: "Kim" };
}
```

8. **ES 모듈 import**

TS에서는 보통 이 스타일로 씁니다:

```ts
import express from "express";
import { Router } from "express";
```

`tsconfig.json`에서 `esModuleInterop: true`를 켜야 위 문법이 동작합니다.

---

#### 실전 팁 & 자주 하는 실수
- 타입 에러 메시지는 "어떤 값이 어떤 타입이어야 하는데, 실제로는 뭐였다"를 읽으면 됨
- 타입을 너무 빡빡하게 쓰면 오히려 불편할 수 있음 → 필요한 곳만 명확히
- any 남용 금지! (타입스크립트의 장점이 사라짐)

#### FAQ
- Q: 타입스크립트는 왜 쓰나요?
  - A: 코드의 실수를 미리 잡아주고, 협업/유지보수에 강합니다.
- Q: 타입 에러가 나면 어떻게 해결하나요?
  - A: 에러 메시지를 천천히 읽고, 변수/함수의 타입 선언을 확인하세요.

---

---

## 6. TypeScript로 프로젝트 전환

### 6.1 의존성 설치 및 tsconfig 설정

```bash
npm install --save-dev typescript ts-node-dev @types/node @types/express
npx tsc --init
```

`tsconfig.json`에서 아래 옵션을 꼭 확인하세요:

```jsonc
{
  "compilerOptions": {
    "target": "ES2019",         // 어떤 JS 버전으로 변환할지
    "module": "commonjs",       // Node.js용 모듈 시스템
    "rootDir": "src",           // 소스 폴더
    "outDir": "dist",           // 빌드 결과 폴더
    "strict": true,              // 엄격한 타입 검사
    "esModuleInterop": true      // import 호환성
  }
}
```

> 참고: `package.json`에 `"type": "module"`을 설정하면(ESM 모드) import/실행 방식이 달라집니다.
> 이 문서는 **CommonJS 기준**(`"type": "module"` 없이)으로 설명합니다.


---

### 6.2 폴더/파일 이동 및 스크립트 설정

```bash
mkdir src
mv index.js src/index.js
mv src/index.js src/index.ts
```

`package.json` 스크립트 교체:

```jsonc
{
  "scripts": {
    "dev": "ts-node-dev --respawn --transpile-only src/index.ts",
    "build": "tsc",
    "start": "node dist/index.js"
  }
}
```

---

#### 실전 팁 & 자주 하는 실수
- `src` 폴더와 `dist` 폴더를 혼동하지 않기 (빌드 결과는 항상 dist)
- `ts-node-dev`는 개발용, 실제 배포는 `tsc`로 빌드 후 `node dist/...`로 실행
- import 경로가 틀리면 "Cannot find module" 에러가 남

#### FAQ
- Q: @types/express, @types/node는 왜 설치하나요?
  - A: JS 라이브러리의 타입 정보를 추가로 제공해줍니다.
- Q: tsconfig.json이 너무 복잡해요!
  - A: 위 예시처럼 최소 옵션만 맞추고, 나머지는 기본값으로 두면 됩니다.

---

### 6.3 `src/index.ts` (TS 버전)

```ts
import express, { Request, Response } from "express";

const app = express();
app.use(express.json());

interface Note {
  id: number;
  text: string;
}

let notes: Note[] = [];
let nextId = 1;

app.get("/notes", (req: Request, res: Response) => {
  res.json(notes);
});

app.post("/notes", (req: Request, res: Response) => {
  const { text } = req.body as { text?: unknown };

  if (typeof text !== "string" || text.trim() === "") {
    return res.status(400).json({ error: "text는 비어있지 않은 문자열이어야 합니다." });
  }

  const newNote: Note = { id: nextId++, text };
  notes.push(newNote);
  res.status(201).json(newNote);
});

app.delete("/notes/:id", (req: Request, res: Response) => {
  const id = Number(req.params.id);
  const index = notes.findIndex((n) => n.id === id);

  if (index === -1) {
    return res.status(404).json({ error: "메모를 찾을 수 없습니다." });
  }

  const deleted = notes.splice(index, 1)[0];
  res.json(deleted);
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
```

```bash
npm run dev
```

여기까지가 **"단일 파일 TS Express 서버"**.

---

## 7. 폴더 구조 분리 (routes / services / models)

단일 파일로 개발하다 보면 코드가 금방 복잡해집니다. 그래서 **역할별로 폴더를 분리**하면 유지보수와 확장성이 훨씬 좋아집니다.

### 7.1 목표 구조와 각 폴더의 역할

```text
notes-api/
  src/
    app.ts           # Express 앱 설정, 공통 미들웨어 등록
    server.ts        # 서버 실행 (listen)
    db.ts            # (나중에 DB 연결용)
    models/          # 타입/엔티티 정의
      note.ts
    services/        # 비즈니스 로직 (DB 접근, 데이터 처리)
      notesService.ts
    routes/          # HTTP 라우팅, 요청/응답 처리
      notesRouter.ts
```

#### 각 레이어의 역할
- `models` : 데이터 구조(타입, 인터페이스)만 정의 (ex: Note)
- `services` : 실제 데이터 처리/비즈니스 로직 (ex: 메모 저장, 삭제)
- `routes` : HTTP 요청/응답 처리, 서비스 호출 (ex: req → service → res)
- `app.ts` : Express 앱 생성, 미들웨어/라우터 등록
- `server.ts` : 서버 실행 (포트 listen)

#### 구조 분리의 장점
- 코드가 길어져도 각 역할별로 파일이 분리되어 유지보수가 쉬움
- 테스트, 확장, 협업에 유리함

#### 실전에서 자주 하는 실수
- 서비스 로직을 라우터에 다 몰아넣음 → 역할 분리 실패
- models/services/routes import 경로 헷갈림
- app.ts와 server.ts의 차이(앱 설정 vs 실행) 혼동

#### FAQ
- Q: 꼭 이렇게 분리해야 하나요?
  - A: 작은 프로젝트는 단일 파일도 가능하지만, 실무/협업/확장에는 분리가 필수입니다.
- Q: 폴더 이름은 꼭 저렇게 해야 하나요?
  - A: 관례일 뿐, 팀/상황에 맞게 바꿔도 됩니다.

---

### 7.2 모델

`src/models/note.ts`:

```ts
export interface Note {
  id: number;
  text: string;
}
```

### 7.3 서비스 (인메모리 버전)

`src/services/notesService.ts`:

```ts
import { Note } from "../models/note";

let notes: Note[] = [];
let nextId = 1;

function getNotes(): Note[] {
  return notes;
}

function createNote(text: string): Note {
  const newNote: Note = { id: nextId++, text };
  notes.push(newNote);
  return newNote;
}

function deleteNote(id: number): Note | null {
  const index = notes.findIndex((n) => n.id === id);
  if (index === -1) return null;
  const deleted = notes.splice(index, 1)[0];
  return deleted;
}

export const notesService = {
  getNotes,
  createNote,
  deleteNote,
};
```

### 7.4 라우터

`src/routes/notesRouter.ts`:

```ts
import { Router, Request, Response } from "express";
import { notesService } from "../services/notesService";

const router = Router();

// GET /notes
router.get("/", (req: Request, res: Response) => {
  const notes = notesService.getNotes();
  res.json(notes);
});

// POST /notes
router.post("/", (req: Request, res: Response) => {
  const { text } = req.body as { text?: unknown };

  if (typeof text !== "string" || text.trim() === "") {
    return res.status(400).json({ error: "text는 비어있지 않은 문자열이어야 합니다." });
  }

  const newNote = notesService.createNote(text);
  res.status(201).json(newNote);
});

// DELETE /notes/:id
router.delete("/:id", (req: Request, res: Response) => {
  const id = Number(req.params.id);
  const deleted = notesService.deleteNote(id);

  if (!deleted) {
    return res.status(404).json({ error: "메모를 찾을 수 없습니다." });
  }

  res.json(deleted);
});

export default router;
```

### 7.5 app / server 분리

`src/app.ts`:

```ts
import express, { Request, Response, NextFunction } from "express";
import notesRouter from "./routes/notesRouter";

const app = express();

app.use(express.json());
app.use("/notes", notesRouter);

app.get("/", (req: Request, res: Response) => {
  res.send("Notes API (TS + layered)");
});

// 공통 에러 핸들러 (기초 버전)
app.use(
  (err: unknown, req: Request, res: Response, next: NextFunction) => {
    console.error(err);
    res.status(500).json({ error: "Internal Server Error" });
  }
);

export default app;
```

`src/server.ts`:

```ts
import app from "./app";

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
```

`package.json` 스크립트 수정:

```jsonc
{
  "scripts": {
    "dev": "ts-node-dev --respawn --transpile-only src/server.ts",
    "build": "tsc",
    "start": "node dist/server.js"
  }
}
```

여기까지가 **타입 + 레이어드 구조**의 기본 틀.

---

## 8. 환경 변수 / 설정 분리 (기본만)

DB 연결, 포트 등 설정값을 코드에 직접 쓰면 나중에 관리가 힘들어집니다. **.env 파일로 분리**하면 환경별로 쉽게 관리할 수 있습니다.

### 8.1 dotenv 설치 및 .env 파일 작성

```bash
npm install dotenv
```

> (참고) dotenv는 버전에 따라 타입이 포함되어 있을 수 있습니다.
> 만약 TS에서 타입 관련 문제가 생기면 그때 `@types/dotenv`를 추가해도 됩니다.


프로젝트 루트(최상위 폴더)에 `.env` 파일 생성:

```env
PORT=3000
DB_HOST=localhost
DB_PORT=5432
DB_USER=notes_user
DB_PASS=pass
DB_NAME=notes_db
```

### 8.2 서버 코드에서 환경 변수 사용

`src/server.ts` 예시:

```ts
import dotenv from "dotenv";
dotenv.config(); // .env 파일을 읽어서 process.env에 등록

import app from "./app";

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
```

---



#### .gitignore에 .env 추가 (중요)

```gitignore
.env
```
#### 실전 팁 & 자주 하는 실수
- `.env` 파일은 **절대 깃허브에 올리지 말 것!** (`.gitignore`에 추가)
- 환경 변수 값이 undefined면 오타/경로/파일 위치를 확인
- 배포 환경에서는 `.env` 대신 환경변수 직접 주입하는 경우도 많음

#### FAQ
- Q: .env 파일은 어디에 두나요?
  - A: 프로젝트 루트(최상위 폴더)에 둡니다.
- Q: .env 파일이 안 읽혀요!
  - A: `dotenv.config()`가 코드 맨 위에 있는지, 파일명이 맞는지 확인하세요.

---

---

## 9. PostgreSQL 연동 (DB 연결)

### 9.1 pg 설치 및 DB 준비

```bash
npm install pg
npm install --save-dev @types/pg
```

#### PostgreSQL 설치/실행이 안 되어 있다면?
- Mac: `brew install postgresql`
- Ubuntu: `sudo apt install postgresql`
- 실행: `sudo service postgresql start` 또는 `brew services start postgresql`

---

### 9.2 db.ts (DB 연결 코드)

`src/db.ts`:

```ts
import { Pool } from "pg";

const pool = new Pool({
  host: process.env.DB_HOST,
  port: Number(process.env.DB_PORT || 5432),
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME,
});

export async function query<T = any>(
  text: string,
  params?: any[]
): Promise<{ rows: T[] }> {
  return pool.query(text, params);
}
```

---

### 9.3 테이블 생성 (DB 마이그레이션)

PostgreSQL에서 아래 쿼리로 테이블을 만듭니다:

```sql
CREATE TABLE notes (
  id SERIAL PRIMARY KEY,
  text TEXT NOT NULL
);
```

#### 실전 팁
- DB 연결이 안 되면 환경변수, DB 실행 상태, 권한, 방화벽 등을 점검
- 테이블이 없으면 "relation does not exist" 에러가 남 → 쿼리 실행 여부 확인

---

### 9.4 notesService를 DB 버전으로 변경

`src/services/notesService.ts` 예시:

```ts
import { Note } from "../models/note";
import { query } from "../db";

async function getNotes(): Promise<Note[]> {
  const result = await query<Note>(
    "SELECT id, text FROM notes ORDER BY id ASC"
  );
  return result.rows;
}

async function createNote(text: string): Promise<Note> {
  const result = await query<Note>(
    "INSERT INTO notes (text) VALUES ($1) RETURNING id, text",
    [text]
  );
  return result.rows[0];
}

async function deleteNote(id: number): Promise<Note | null> {
  const result = await query<Note>(
    "DELETE FROM notes WHERE id = $1 RETURNING id, text",
    [id]
  );
  if (result.rows.length === 0) return null;
  return result.rows[0];
}

export const notesService = {
  getNotes,
  createNote,
  deleteNote,
};
```

---

#### 실전 팁 & 자주 하는 실수
- DB 연결 실패: 환경변수, DB 실행 상태, 비밀번호, 포트, 방화벽 등 점검
- SQL 쿼리 오타, 테이블/컬럼명 오타 주의
- 쿼리 결과가 없을 때(빈 배열) null 체크 필수

#### FAQ
- Q: DB 연결이 안 돼요!
  - A: DB가 실행 중인지, .env 설정이 맞는지, 권한이 있는지 확인하세요.
- Q: 테이블이 없다는 에러가 나요!
  - A: 테이블 생성 쿼리를 실행했는지, DB 이름이 맞는지 확인하세요.

---

### 9.5 라우터를 async/await 대응

`src/routes/notesRouter.ts`:

```ts
import { Router, Request, Response, NextFunction } from "express";
import { notesService } from "../services/notesService";

const router = Router();

router.get("/", async (req: Request, res: Response, next: NextFunction) => {
  try {
    const notes = await notesService.getNotes();
    res.json(notes);
  } catch (err) {
    next(err);
  }
});

router.post("/", async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { text } = req.body as { text?: unknown };

    if (typeof text !== "string" || text.trim() === "") {
      return res.status(400).json({ error: "text는 비어있지 않은 문자열이어야 합니다." });
    }

    const newNote = await notesService.createNote(text);
    res.status(201).json(newNote);
  } catch (err) {
    next(err);
  }
});

router.delete(
  "/:id",
  async (req: Request, res: Response, next: NextFunction) => {
    try {
      const id = Number(req.params.id);
      const deleted = await notesService.deleteNote(id);

      if (!deleted) {
        return res.status(404).json({ error: "메모를 찾을 수 없습니다." });
      }

      res.json(deleted);
    } catch (err) {
      next(err);
    }
  }
);

export default router;
```

`app.ts`의 에러 핸들러는 아까 것 그대로 사용.

---

## 10. 체크리스트

아래 항목을 "예/아니오"로 체크해보세요. 모두 "예"라면 이 문서의 목표를 달성한 것입니다!

| 체크 | 항목 |
|:---:|:---------------------------------------------------------------|
|     | 1. Node.js가 뭔지, 이벤트 루프/비동기 I/O 개념을 설명할 수 있다 |
|     | 2. HTTP 요청/응답, 상태 코드, REST URL 설계를 예시로 들 수 있다 |
|     | 3. Express로 GET/POST/DELETE 라우터를 만들고 JSON 응답을 줄 수 있다 |
|     | 4. TypeScript에서 기본 타입, 인터페이스, 유니온 타입, 타입 추론을 쓸 줄 안다 |
|     | 5. Request, Response에 타입을 붙여서 Express 핸들러를 작성할 수 있다 |
|     | 6. 프로젝트를 routes/services/models 구조로 나누고, 역할을 분리할 수 있다 |
|     | 7. PostgreSQL과 연결해서 Notes를 DB에 저장/조회/삭제할 수 있다 |
|     | 8. 설정값(포트, DB 정보)을 .env로 뺄 수 있다 |
|     | 9. 기본적인 에러 핸들러 미들웨어를 추가해서 500 에러를 공통 처리할 수 있다 |

#### 예시 체크 방법
- 각 항목별로 "예/아니오" 체크박스(☑/☐)를 직접 그려보거나, 실제로 코드를 작성해보세요.
- 막히는 부분이 있다면 해당 섹션을 다시 복습하거나, 공식 문서/구글링을 활용하세요.

#### 추가로 공부하면 좋은 자료
- [Node.js 공식 문서](https://nodejs.org/ko/docs)
- [Express 공식 문서](https://expressjs.com/ko/)
- [TypeScript 핸드북](https://typescript-kr.github.io/)
- [PostgreSQL 공식 문서](https://www.postgresql.org/docs/)

---