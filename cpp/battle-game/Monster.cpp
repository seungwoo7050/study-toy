// [FILE] Monster 클래스 구현
// [LEARN] 몬스터의 AI 로직과 자동 전투 구현
// [Order 6] Monster 클래스 메서드 구현

#include "Monster.h"
#include <iostream>
#include <cstdlib>  // rand() 사용
#include <ctime>    // time() 사용

// [LEARN] Monster 생성자 - 타입에 따른 스탯 설정
Monster::Monster(const std::string& name, const std::string& type)
    : Character(name, 80, 12, 3), type(type) {
    // 타입에 따라 스탯 조정
    if (type == "Goblin") {
        // Goblin: 빠르고 약함
        attackPower = 10;
        defense = 2;
    } else if (type == "Orc") {
        // Orc: 강하고 느림
        health = 120;
        maxHealth = 120;
        attackPower = 18;
        defense = 6;
    }
}

// [LEARN] 몬스터의 자동 액션 (간단한 AI)
void Monster::performAction(Character& target) {
    // 70% 확률로 공격, 30% 확률로 아무것도 하지 않음
    if (rand() % 100 < 70) {
        int damage = calculateDamage();
        std::cout << name << "의 공격!" << std::endl;
        target.takeDamage(damage);
    } else {
        std::cout << name << "은(는) 가만히 있습니다." << std::endl;
    }
}

// [LEARN] 몬스터 스탯 표시 (타입 정보 추가)
void Monster::displayStats() const {
    Character::displayStats();
    std::cout << "타입: " << type << std::endl;
}