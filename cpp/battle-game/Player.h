// [FILE] Player 클래스 헤더
// [LEARN] C++ 상속과 다형성 구현
// [Order 3] Player 클래스 정의

#ifndef PLAYER_H
#define PLAYER_H

#include "Character.h"

class Player : public Character {
private:
    int mana;
    int maxMana;

public:
    // [LEARN] 파생 클래스 생성자
    Player(const std::string& name);

    // [LEARN] 마나 관리
    int getMana() const { return mana; }
    int getMaxMana() const { return maxMana; }
    void useMana(int amount);
    void restoreMana(int amount);

    // [LEARN] 플레이어 액션
    void attack(Character& target);
    void useSkill(Character& target);

    // [LEARN] 오버라이드된 가상 함수
    void displayStats() const override;
};

#endif // PLAYER_H