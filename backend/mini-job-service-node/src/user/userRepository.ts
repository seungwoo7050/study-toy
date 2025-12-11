// [FILE]
// - 목적: User 데이터 저장/조회 (인메모리)
// - 관련 토이 버전: [Node-BE-v0.1]
//
// [LEARN] 실제 서비스에서는 데이터베이스를 사용하지만, 학습을 위해 Map을 활용합니다.
//         key를 email로 사용하여 빠르게 조회하며, id는 자동 증가값으로 부여합니다.
import { User } from './user';

export class UserRepository {
  private users = new Map<string, User>();
  private nextId = 1;

  // 이메일로 사용자 조회
  findByEmail(email: string): User | undefined {
    return this.users.get(email);
  }

  // 새로운 사용자 저장 후 User 객체 반환
  save(email: string, passwordHash: string): User {
    const user = new User(this.nextId++, email, passwordHash);
    this.users.set(email, user);
    return user;
  }
}