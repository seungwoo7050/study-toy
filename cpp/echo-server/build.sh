#!/bin/bash

# [FILE] 빌드 스크립트
# [LEARN] C++ 프로젝트 빌드 자동화
# [Order 17] 빌드 스크립트

echo "Echo Server 빌드 중..."

# 서버 컴파일
echo "서버 컴파일..."
g++ -std=c++17 -O2 -Wall server.cpp -o server

if [ $? -eq 0 ]; then
    echo "서버 컴파일 성공"
else
    echo "서버 컴파일 실패"
    exit 1
fi

# 클라이언트 컴파일
echo "클라이언트 컴파일..."
g++ -std=c++17 -O2 -Wall client.cpp -o client

if [ $? -eq 0 ]; then
    echo "클라이언트 컴파일 성공"
else
    echo "클라이언트 컴파일 실패"
    exit 1
fi

echo "빌드 완료!"
echo ""
echo "실행 방법:"
echo "  서버: ./server [포트]"
echo "  클라이언트: ./client [서버IP] [포트]"