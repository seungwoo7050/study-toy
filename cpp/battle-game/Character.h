// [FILE] Character 클래스 헤더
// [LEARN] C++ 클래스 설계와 객체 지향 프로그래밍 기초
// [Order 1] Character 기본 클래스 정의

#ifndef CHARACTER_H
#define CHARACTER_H

#include <string>

class Character {
protected:
    std::string name;
    int health;
    int maxHealth;
    int attackPower;
    int defense;

public:
    // [LEARN] 생성자와 소멸자
    Character(const std::string& name, int health, int attackPower, int defense);
    virtual ~Character() = default;

    // [LEARN] getter 메서드들
    std::string getName() const { return name; }
    int getHealth() const { return health; }
    int getMaxHealth() const { return maxHealth; }
    int getAttackPower() const { return attackPower; }
    int getDefense() const { return defense; }

    // [LEARN] 체력 관리
    void takeDamage(int damage);
    void heal(int amount);
    bool isAlive() const { return health > 0; }

    // [LEARN] 가상 함수 - 다형성 구현
    virtual void displayStats() const;
    virtual int calculateDamage() const;
};

#endif // CHARACTER_H