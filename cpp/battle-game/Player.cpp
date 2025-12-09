// [FILE] Player 클래스 구현
// [LEARN] 상속받은 클래스의 메서드 구현과 게임 액션 로직
// [Order 4] Player 클래스 메서드 구현

#include "Player.h"
#include <iostream>

// [LEARN] Player 생성자 - 기본 스탯 설정
Player::Player(const std::string& name)
    : Character(name, 100, 15, 5), mana(50), maxMana(50) {
}

// [LEARN] 마나 사용
void Player::useMana(int amount) {
    if (mana >= amount) {
        mana -= amount;
    }
}

// [LEARN] 마나 회복
void Player::restoreMana(int amount) {
    mana += amount;
    if (mana > maxMana) mana = maxMana;
}

// [LEARN] 기본 공격
void Player::attack(Character& target) {
    int damage = calculateDamage();
    std::cout << name << "의 공격!" << std::endl;
    target.takeDamage(damage);
}

// [LEARN] 스킬 사용 (마나 소비)
void Player::useSkill(Character& target) {
    if (mana >= 10) {
        useMana(10);
        int skillDamage = calculateDamage() * 2; // 스킬은 2배 데미지
        std::cout << name << "의 파워 스트라이크!" << std::endl;
        target.takeDamage(skillDamage);
    } else {
        std::cout << "마나가 부족합니다!" << std::endl;
        attack(target); // 마나 부족시 기본 공격
    }
}

// [LEARN] 플레이어 스탯 표시 (부모 클래스 메서드 오버라이드)
void Player::displayStats() const {
    Character::displayStats(); // 부모 클래스의 displayStats 호출
    std::cout << "MP: " << mana << "/" << maxMana << std::endl;
}