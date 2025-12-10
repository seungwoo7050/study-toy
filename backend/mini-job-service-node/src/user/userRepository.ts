// [FILE]
// - 목적: User 데이터 저장/조회 (인메모리)
// - 관련 토이 버전: [Node-BE-v0.1]
import { User } from './user';

export class UserRepository {
  private users = new Map<string, User>();
  private nextId = 1;

  findByEmail(email: string): User | undefined {
    return this.users.get(email);
  }

  save(email: string, passwordHash: string): User {
    const user = new User(this.nextId++, email, passwordHash);
    this.users.set(email, user);
    return user;
  }
}
