// [FILE]
// - 목적: 회원가입/로그인 엔드포인트
// - 주요 역할: JWT 발급을 통해 대시보드와 연동 가능한 인증 흐름 제공
// - 관련 토이 버전: [Node-BE-v0.1]
import { Router } from 'express';
import { UserRepository } from '../../user/userRepository';
import { UserService } from '../../user/userService';

const repo = new UserRepository();
const userService = new UserService(repo);

export const authRouter = Router();

authRouter.post('/signup', async (req, res, next) => {
  try {
    const { email, password } = req.body;
    const token = await userService.signup(email, password);
    res.status(201).json({ token });
  } catch (err) {
    next(err);
  }
});

authRouter.post('/login', async (req, res, next) => {
  try {
    const { email, password } = req.body;
    const token = await userService.login(email, password);
    res.json({ token });
  } catch (err) {
    next(err);
  }
});
