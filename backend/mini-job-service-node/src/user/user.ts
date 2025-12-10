// [FILE]
// - 목적: User 도메인 모델 정의
// - 관련 토이 버전: [Node-BE-v0.1]
export class User {
  constructor(public readonly id: number, public readonly email: string, public passwordHash: string) {}
}
