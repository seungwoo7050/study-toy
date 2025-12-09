// [FILE] 메인 게임 루프
// [LEARN] C++ 프로그램의 진입점과 게임 루프 구현
// [Order 7] 메인 함수와 게임 로직

#include <iostream>
#include <string>
#include <limits>  // numeric_limits 사용
#include "Player.h"
#include "Monster.h"

int main() {
    // [LEARN] 랜덤 시드 설정
    srand(static_cast<unsigned int>(time(nullptr)));

    std::cout << "=== Battle Game ===" << std::endl;
    std::cout << "턴제 배틀 게임에 오신 것을 환영합니다!" << std::endl << std::endl;

    // [Order 8] 플레이어 생성
    std::string playerName;
    std::cout << "플레이어 이름을 입력하세요: ";
    std::getline(std::cin, playerName);

    if (playerName.empty()) {
        playerName = "Hero";
    }

    Player player(playerName);

    // [Order 9] 몬스터 생성
    Monster monster("Goblin King", "Goblin");

    std::cout << std::endl;
    std::cout << "전투 시작!" << std::endl;
    std::cout << "====================" << std::endl;

    // [Order 10] 게임 루프
    bool gameRunning = true;
    while (gameRunning && player.isAlive() && monster.isAlive()) {
        // [LEARN] 턴 시작 - 스탯 표시
        std::cout << std::endl;
        player.displayStats();
        std::cout << std::endl;
        monster.displayStats();
        std::cout << std::endl;

        // 플레이어 턴
        std::cout << "당신의 턴입니다." << std::endl;
        std::cout << "1. 공격" << std::endl;
        std::cout << "2. 스킬 사용 (MP 10 소모)" << std::endl;
        std::cout << "선택: ";

        int choice;
        std::cin >> choice;

        // [LEARN] 입력 버퍼 정리
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case 1:
                player.attack(monster);
                break;
            case 2:
                player.useSkill(monster);
                break;
            default:
                std::cout << "잘못된 선택입니다. 기본 공격을 실행합니다." << std::endl;
                player.attack(monster);
                break;
        }

        // 몬스터가 살아있으면 몬스터 턴
        if (monster.isAlive()) {
            std::cout << std::endl;
            std::cout << monster.getName() << "의 턴입니다." << std::endl;
            monster.performAction(player);
        }

        // 턴 종료 후 마나 회복 (플레이어만)
        player.restoreMana(5);
    }

    // [Order 11] 게임 결과
    std::cout << std::endl;
    std::cout << "====================" << std::endl;
    if (player.isAlive()) {
        std::cout << "축하합니다! " << player.getName() << "의 승리!" << std::endl;
    } else {
        std::cout << "패배했습니다. 다음 기회에 다시 도전하세요!" << std::endl;
    }

    return 0;
}