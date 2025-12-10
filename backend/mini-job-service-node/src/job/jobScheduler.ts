// [FILE]
// - 목적: 간단한 비동기 Job 처리 시뮬레이션
// - 주요 역할: 일정 주기로 PENDING → RUNNING → DONE 상태 전이
// - 관련 토이 버전: [Node-BE-v0.1]
// - 권장 읽기 순서: 생성자 → start → 내부 transition
import { JobService } from './jobService';

export class JobScheduler {
  private interval?: NodeJS.Timeout;

  constructor(private readonly service: JobService) {}

  start() {
    if (this.interval) return;
    this.interval = setInterval(() => this.tick(), 2000);
  }

  stop() {
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = undefined;
    }
  }

  private tick() {
    const pending = this.service.findNextPending();
    if (!pending) return;

    const running = this.service.transition(pending.id, 'RUNNING');
    setTimeout(() => {
      // 아주 단순한 성공 시뮬레이션
      this.service.transition(running.id, 'DONE');
    }, 1200);
  }
}
