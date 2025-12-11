// [FILE]
// - 목적: 회원가입/로그인 엔드포인트
// - 주요 역할: JWT 발급을 통해 대시보드와 연동 가능한 인증 흐름 제공
// - 관련 토이 버전: [Node-BE-v0.1]
//
// [LEARN] 비동기 핸들러에서는 Promise가 reject될 수 있으므로 try/catch로 감싸고 next(err)로 전달합니다.
//         UserService는 패스워드 해싱과 토큰 발급을 내부적으로 처리합니다.
import { Router } from 'express';
import { UserRepository } from '../../user/userRepository';
import { UserService } from '../../user/userService';

// 저장소/서비스 싱글턴 초기화
const repo = new UserRepository();
const userService = new UserService(repo);

export const authRouter = Router();

// [Order 1] 회원가입 엔드포인트
// POST /api/auth/signup -> email과 password를 받아 유저를 생성하고 JWT를 발급
authRouter.post('/signup', async (req, res, next) => {
  try {
    const { email, password } = req.body;
    const token = await userService.signup(email, password);
    res.status(201).json({ token });
  } catch (err) {
    next(err);
  }
});

// [Order 2] 로그인 엔드포인트
// POST /api/auth/login -> email과 password로 인증 후 JWT를 반환
authRouter.post('/login', async (req, res, next) => {
  try {
    const { email, password } = req.body;
    const token = await userService.login(email, password);
    res.json({ token });
  } catch (err) {
    next(err);
  }
});