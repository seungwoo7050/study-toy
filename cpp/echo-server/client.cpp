// [FILE] TCP 에코 클라이언트
// [LEARN] C++ 소켓 프로그래밍 클라이언트 구현
// [Order 10] TCP 에코 클라이언트 구현

#include <iostream>
#include <string>
#include <cstring>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>

class EchoClient {
private:
    int clientSocket;
    struct sockaddr_in serverAddr;

public:
    // [LEARN] 생성자 - 서버 연결
    EchoClient(const std::string& serverIP, int port) {
        // [Order 11] 클라이언트 소켓 생성
        clientSocket = socket(AF_INET, SOCK_STREAM, 0);
        if (clientSocket < 0) {
            throw std::runtime_error("소켓 생성 실패");
        }

        // [Order 12] 서버 주소 설정
        memset(&serverAddr, 0, sizeof(serverAddr));
        serverAddr.sin_family = AF_INET;
        serverAddr.sin_port = htons(port);

        if (inet_pton(AF_INET, serverIP.c_str(), &serverAddr.sin_addr) <= 0) {
            close(clientSocket);
            throw std::runtime_error("잘못된 서버 IP 주소");
        }

        // [Order 13] 서버 연결
        if (connect(clientSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) {
            close(clientSocket);
            throw std::runtime_error("서버 연결 실패");
        }

        std::cout << "서버 " << serverIP << ":" << port << "에 연결되었습니다." << std::endl;
    }

    // [LEARN] 소멸자 - 소켓 정리
    ~EchoClient() {
        close(clientSocket);
    }

    // [Order 14] 메시지 송수신
    void sendAndReceive(const std::string& message) {
        // [LEARN] 서버로 메시지 전송
        if (send(clientSocket, message.c_str(), message.length(), 0) < 0) {
            throw std::runtime_error("메시지 전송 실패");
        }

        // [LEARN] 서버로부터 응답 수신
        char buffer[1024];
        int bytesRead = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);

        if (bytesRead < 0) {
            throw std::runtime_error("응답 수신 실패");
        }

        buffer[bytesRead] = '\0'; // 문자열 종료
        std::cout << "서버 응답: " << buffer << std::endl;
    }
};

int main(int argc, char* argv[]) {
    try {
        // [Order 15] 기본 설정
        std::string serverIP = "127.0.0.1";
        int port = 9000;

        // 명령줄 인자로 서버 IP와 포트 설정
        if (argc > 1) serverIP = argv[1];
        if (argc > 2) port = std::stoi(argv[2]);

        // [Order 16] 클라이언트 생성
        EchoClient client(serverIP, port);

        std::cout << "메시지를 입력하세요 (종료하려면 'quit' 입력):" << std::endl;

        std::string message;
        while (true) {
            std::cout << "> ";
            std::getline(std::cin, message);

            if (message == "quit") {
                break;
            }

            if (!message.empty()) {
                client.sendAndReceive(message);
            }
        }

    } catch (const std::exception& e) {
        std::cerr << "에러: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}