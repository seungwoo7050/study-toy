// [FILE]
// - 목적: 서버 실행 entrypoint
// - 주요 역할: Express 앱 생성, Job 스케줄러 구동
// - 관련 토이 버전: [Node-BE-v0.1]
import { createApp } from './app';
import { JobScheduler } from './job/jobScheduler';
import { jobServiceSingleton } from './router/routes/jobRouter';
import { appConfig } from './config/appConfig';

const app = createApp();

// [Order 1] 배경 스케줄러 기동
const scheduler = new JobScheduler(jobServiceSingleton);
scheduler.start();

// [Order 2] HTTP 서버 시작
app.listen(appConfig.port, () => {
  // eslint-disable-next-line no-console
  console.log(`mini-job-service-node is running on http://localhost:${appConfig.port}`);
});
