// [FILE] TCP 에코 서버
// [LEARN] C++ 소켓 프로그래밍 기초와 네트워크 서버 구현
// [Order 1] TCP 에코 서버 구현

#include <iostream>
#include <string>
#include <cstring>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>

class EchoServer {
private:
    int serverSocket;
    int clientSocket;
    struct sockaddr_in serverAddr;
    struct sockaddr_in clientAddr;
    socklen_t clientAddrLen;

public:
    // [LEARN] 생성자 - 소켓 초기화
    EchoServer(int port) {
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
        if (listen(serverSocket, 1) < 0) {
            close(serverSocket);
            throw std::runtime_error("리스닝 실패");
        }

        std::cout << "에코 서버가 포트 " << port << "에서 대기 중입니다..." << std::endl;
    }

    // [LEARN] 소멸자 - 소켓 정리
    ~EchoServer() {
        close(clientSocket);
        close(serverSocket);
    }

    // [Order 6] 클라이언트 연결 수락
    void acceptConnection() {
        clientAddrLen = sizeof(clientAddr);
        clientSocket = accept(serverSocket, (struct sockaddr*)&clientAddr, &clientAddrLen);

        if (clientSocket < 0) {
            throw std::runtime_error("클라이언트 연결 수락 실패");
        }

        std::cout << "클라이언트가 연결되었습니다." << std::endl;
    }

    // [Order 7] 에코 서비스 제공
    void runEchoService() {
        char buffer[1024];
        int bytesRead;

        while (true) {
            // [LEARN] 클라이언트로부터 데이터 수신
            bytesRead = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);

            if (bytesRead <= 0) {
                std::cout << "클라이언트 연결이 종료되었습니다." << std::endl;
                break;
            }

            buffer[bytesRead] = '\0'; // 문자열 종료
            std::cout << "받은 메시지: " << buffer << std::endl;

            // [LEARN] 받은 데이터를 그대로 에코
            send(clientSocket, buffer, bytesRead, 0);
        }
    }
};

int main(int argc, char* argv[]) {
    try {
        // [Order 8] 포트 설정 (기본값 9000)
        int port = 9000;
        if (argc > 1) {
            port = std::stoi(argv[1]);
        }

        // [Order 9] 서버 생성 및 실행
        EchoServer server(port);
        server.acceptConnection();
        server.runEchoService();

    } catch (const std::exception& e) {
        std::cerr << "에러: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}