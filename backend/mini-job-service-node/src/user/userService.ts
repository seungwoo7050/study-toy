// [FILE]
// - 목적: 인증/인가 비즈니스 로직
// - 주요 역할: 회원가입, 로그인, JWT 발급
// - 관련 토이 버전: [Node-BE-v0.1]
//
// [LEARN] bcrypt로 비밀번호를 해싱하고, jsonwebtoken으로 stateless 인증을 구성한다.
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { HttpError } from '../common/errorHandler';
import { appConfig } from '../config/appConfig';
import { UserRepository } from './userRepository';
import { User } from './user';

export class UserService {
  constructor(private readonly repo: UserRepository) {}

  async signup(email: string, password: string): Promise<string> {
    this.validate(email, password);
    if (this.repo.findByEmail(email)) {
      throw new HttpError(409, 'User already exists');
    }
    const hash = await bcrypt.hash(password, 10);
    const user = this.repo.save(email, hash);
    return this.issueToken(user);
  }

  async login(email: string, password: string): Promise<string> {
    this.validate(email, password);
    const existing = this.repo.findByEmail(email);
    if (!existing) {
      throw new HttpError(401, 'Invalid credentials');
    }
    const ok = await bcrypt.compare(password, existing.passwordHash);
    if (!ok) {
      throw new HttpError(401, 'Invalid credentials');
    }
    return this.issueToken(existing);
  }

  private issueToken(user: User): string {
    return jwt.sign({ sub: user.id, email: user.email }, appConfig.jwtSecret, {
      expiresIn: appConfig.jwtExpirationMs / 1000,
    });
  }

  private validate(email: string, password: string) {
    if (!email || !password) {
      throw new HttpError(400, 'email and password are required');
    }
  }
}
