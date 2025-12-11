// [FILE]
// - 목적: User 도메인 모델 정의
// - 관련 토이 버전: [Node-BE-v0.1]
//
// [LEARN] TypeScript의 public, readonly 접근 제한자는 도메인 객체를 안전하게 정의하는 데 도움이 됩니다.
//         passwordHash는 나중에 수정될 수 있으므로 readonly가 아닌 public으로 선언합니다.
export class User {
  constructor(
    public readonly id: number,
    public readonly email: string,
    public passwordHash: string,
  ) {}
}