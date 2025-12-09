// [FILE] Monster 클래스 헤더
// [LEARN] 또 다른 Character 파생 클래스 구현
// [Order 5] Monster 클래스 정의

#ifndef MONSTER_H
#define MONSTER_H

#include "Character.h"

class Monster : public Character {
private:
    std::string type;

public:
    // [LEARN] Monster 생성자
    Monster(const std::string& name, const std::string& type);

    // [LEARN] 몬스터 타입 getter
    std::string getType() const { return type; }

    // [LEARN] 몬스터 액션 (AI)
    void performAction(Character& target);

    // [LEARN] 오버라이드된 스탯 표시
    void displayStats() const override;
};

#endif // MONSTER_H