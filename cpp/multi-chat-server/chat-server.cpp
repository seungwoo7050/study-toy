// [FILE] 멀티 클라이언트 채팅 서버
// [LEARN] C++ select()를 사용한 다중 클라이언트 처리
// [Order 1] 채팅 서버 구현

#include <iostream>
#include <vector>
#include <string>
#include <cstring>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>
#include <algorithm>

class ChatServer {
private:
    int serverSocket;
    struct sockaddr_in serverAddr;
    std::vector<int> clientSockets;
    fd_set masterSet;
    int maxFd;

public:
    // [LEARN] 생성자 - 서버 초기화
    ChatServer(int port) : maxFd(0) {
        // [Order 2] 서버 소켓 생성
        serverSocket = socket(AF_INET, SOCK_STREAM, 0);
        if (serverSocket < 0) {
            throw std::runtime_error("소켓 생성 실패");
        }

        // [Order 3] 서버 주소 설정
        memset(&serverAddr, 0, sizeof(serverAddr));
        serverAddr.sin_family = AF_INET;
        serverAddr.sin_addr.s_addr = INADDR_ANY;
        serverAddr.sin_port = htons(port);

        // [Order 4] 소켓 바인딩
        if (bind(serverSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) {
            close(serverSocket);
            throw std::runtime_error("바인딩 실패");
        }

        // [Order 5] 연결 대기
        if (listen(serverSocket, 10) < 0) {
            close(serverSocket);
            throw std::runtime_error("리스닝 실패");
        }

        // [Order 6] FD_SET 초기화
        FD_ZERO(&masterSet);
        FD_SET(serverSocket, &masterSet);
        maxFd = serverSocket;

        std::cout << "채팅 서버가 포트 " << port << "에서 시작되었습니다." << std::endl;
        std::cout << "클라이언트 연결을 기다리는 중..." << std::endl;
    }

    // [LEARN] 소멸자 - 리소스 정리
    ~ChatServer() {
        for (int client : clientSockets) {
            close(client);
        }
        close(serverSocket);
    }

    // [Order 7] 서버 실행
    void run() {
        fd_set readSet;

        while (true) {
            readSet = masterSet;

            // [LEARN] select()로 이벤트 대기
            if (select(maxFd + 1, &readSet, nullptr, nullptr, nullptr) < 0) {
                std::cerr << "select() 에러" << std::endl;
                continue;
            }

            // [Order 8] 서버 소켓 이벤트 (새 클라이언트 연결)
            if (FD_ISSET(serverSocket, &readSet)) {
                acceptNewClient();
            }

            // [Order 9] 클라이언트 소켓 이벤트들 처리
            handleClientMessages(readSet);
        }
    }

private:
    // [Order 10] 새 클라이언트 연결 수락
    void acceptNewClient() {
        struct sockaddr_in clientAddr;
        socklen_t clientAddrLen = sizeof(clientAddr);

        int clientSocket = accept(serverSocket, (struct sockaddr*)&clientAddr, &clientAddrLen);
        if (clientSocket < 0) {
            std::cerr << "클라이언트 연결 수락 실패" << std::endl;
            return;
        }

        // 클라이언트 목록에 추가
        clientSockets.push_back(clientSocket);
        FD_SET(clientSocket, &masterSet);

        if (clientSocket > maxFd) {
            maxFd = clientSocket;
        }

        std::cout << "새 클라이언트 연결됨 (소켓: " << clientSocket << ")" << std::endl;

        // 환영 메시지 전송
        std::string welcomeMsg = "채팅 서버에 연결되었습니다!\n";
        send(clientSocket, welcomeMsg.c_str(), welcomeMsg.length(), 0);
    }

    // [Order 11] 클라이언트 메시지 처리
    void handleClientMessages(fd_set& readSet) {
        for (size_t i = 0; i < clientSockets.size(); ++i) {
            int clientSocket = clientSockets[i];

            if (FD_ISSET(clientSocket, &readSet)) {
                char buffer[1024];
                int bytesRead = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);

                if (bytesRead <= 0) {
                    // 클라이언트 연결 종료
                    std::cout << "클라이언트 연결 종료 (소켓: " << clientSocket << ")" << std::endl;
                    close(clientSocket);
                    FD_CLR(clientSocket, &masterSet);
                    clientSockets.erase(clientSockets.begin() + i);
                    --i; // 인덱스 조정
                } else {
                    // 메시지 처리
                    buffer[bytesRead] = '\0';
                    std::string message(buffer);

                    // 메시지 브로드캐스트
                    broadcastMessage(message, clientSocket);
                }
            }
        }
    }

    // [Order 12] 메시지를 모든 클라이언트에게 브로드캐스트
    void broadcastMessage(const std::string& message, int senderSocket) {
        for (int clientSocket : clientSockets) {
            if (clientSocket != senderSocket) { // 송신자는 제외
                send(clientSocket, message.c_str(), message.length(), 0);
            }
        }
    }
};

int main(int argc, char* argv[]) {
    try {
        // [Order 13] 포트 설정 (기본값 8080)
        int port = 8080;
        if (argc > 1) {
            port = std::stoi(argv[1]);
        }

        // [Order 14] 서버 생성 및 실행
        ChatServer server(port);
        server.run();

    } catch (const std::exception& e) {
        std::cerr << "에러: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}