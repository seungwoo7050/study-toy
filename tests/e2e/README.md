E2E Basic Exercise
------------------

This folder contains a basic E2E exercise description (manual) for students.

Steps:
1. Run the backend:
   ```bash
   cd backend/mini-job-service
   ./gradlew bootRun
   ```
2. Run the frontend dev-server in a separate terminal:
   ```bash
   cd frontend/mini-job-dashboard
   npm ci
   npm run dev
   ```
3. Manually create a job via curl and verify UI:
   ```bash
   curl -X POST http://localhost:8080/api/jobs -H 'Content-Type: application/json' -d '{"type":"VIDEO_TRIM","payload":"{}"}'
   ```
4. Open http://localhost:5173 and verify the job appears in the list.

Optional: You can use `scripts/frontend-smoke-test.sh` and verify the dist served properly for the E2E build. Running automated headless tests requires Playwright setup and is out of the scope of this sample exercise.

참고: 스크립트에 대한 자세한 설명 및 사용방법은 `DOCS/SCRIPTS.md`를 참고하세요.
