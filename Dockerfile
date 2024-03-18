# 베이스 이미지 Amazon Corretto 17로 설정 (애플리케이션 Java Vendor)
FROM amazoncorretto:17

# 작업 디렉토리 설정 (원하는 경로로 수정 가능)
WORKDIR /haru-dongseon

# 변수 설정
ARG JAR_FILE_PATH=./build/libs/*.jar
ARG JAR_FILE_NAME=haru-dongseon.jar

# 빌드한 JAR 파일 원하는 경로에 복사
COPY ${JAR_FILE_PATH} /haru-dongseon/${JAR_FILE_NAME}

# 어플리케이션 실행 명령어를 지정합니다.
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "haru-dongseon.jar"]
