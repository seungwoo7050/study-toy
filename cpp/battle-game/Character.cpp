// [FILE] Character 클래스 구현
// [LEARN] C++ 클래스 메서드 구현과 기본적인 게임 로직
// [Order 2] Character 클래스 메서드 구현

#include "Character.h"
#include <iostream>

// [LEARN] 생성자 구현
Character::Character(const std::string& name, int health, int attackPower, int defense)
    : name(name), health(health), maxHealth(health), attackPower(attackPower), defense(defense) {
}

// [LEARN] 데미지 처리 로직
void Character::takeDamage(int damage) {
    int actualDamage = damage - defense;
    if (actualDamage < 0) actualDamage = 0;

    health -= actualDamage;
    if (health < 0) health = 0;

    std::cout << name << "은(는) " << actualDamage << " 데미지를 받았습니다!" << std::endl;
}

// [LEARN] 회복 로직
void Character::heal(int amount) {
    health += amount;
    if (health > maxHealth) health = maxHealth;

    std::cout << name << "은(는) " << amount << " HP를 회복했습니다!" << std::endl;
}

// [LEARN] 스탯 표시 (가상 함수 구현)
void Character::displayStats() const {
    std::cout << "=== " << name << " ===" << std::endl;
    std::cout << "HP: " << health << "/" << maxHealth << std::endl;
    std::cout << "공격력: " << attackPower << std::endl;
    std::cout << "방어력: " << defense << std::endl;
}

// [LEARN] 데미지 계산 (기본 구현)
int Character::calculateDamage() const {
    return attackPower;
}