# battle-game (C++ 콘솔 턴제 배틀)

> ⚠️ 이 리포지토리는 **학습용 토이 프로젝트**입니다.  
>   실서비스 운영/보안/장애 대응을 전제로 설계되지 않았습니다.

콘솔 기반 턴제 배틀 게임입니다.  
C++ 클래스/구조체 설계와 객체 지향 프로그래밍 기초를 학습합니다.

---

## 기술 스택

- C++17
- g++ / clang++
- 표준 라이브러리 (iostream, vector, string)

---

## Version

| 버전 | 설명 | Git 태그 |
|------|------|----------|
| C0.1 | 콘솔 턴제 배틀 게임 | `CPP-C0.1` |

---

## Implementation Order (Files)

### CPP-C0.1

1. `Player.h` / `Player.cpp` - 플레이어 클래스
2. `Monster.h` / `Monster.cpp` - 몬스터 클래스
3. `BattleSystem.h` / `BattleSystem.cpp` - 전투 시스템
4. `main.cpp` - 메인 진입점

---

## 빌드 및 실행

```bash
cd cpp/battle-game

# 컴파일
g++ -std=c++17 -O2 -Wall *.cpp -o battle-game

# 실행
./battle-game
```

---

## 게임 플레이

```
=== Battle Game ===
Player HP: 100
Monster HP: 50

Your turn! Choose action:
1. Attack
2. Defend
3. Run

> 1

You attacked! Monster takes 15 damage.
Monster HP: 35

Monster attacks! You take 10 damage.
Player HP: 90

...
```

---

## 학습 목표

- C++ 클래스/헤더/소스 파일 분리
- 생성자, 멤버 함수, 접근 제한자
- std::vector를 이용한 객체 관리
- 간단한 게임 루프 구현

---

## Troubleshooting

### 컴파일 에러 (std::string 관련)

- 컴파일 옵션에 `-std=c++17`이 포함되어 있는지 확인한다.

```bash
# 올바른 컴파일 명령어
g++ -std=c++17 -O2 -Wall *.cpp -o battle-game
```

### undefined reference 에러

- 모든 `.cpp` 파일을 컴파일 명령어에 포함했는지 확인한다.

```bash
# 개별 파일 지정
g++ -std=c++17 main.cpp Player.cpp Monster.cpp BattleSystem.cpp -o battle-game
```

### 랜덤 값이 항상 같은 경우

- `main()`에서 `srand(time(nullptr))`로 시드를 초기화했는지 확인한다.
