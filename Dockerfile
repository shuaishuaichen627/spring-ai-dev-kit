FROM openjdk:17-jdk-slim

LABEL maintainer="Spring AI Dev Kit"

WORKDIR /app

# 复制 jar 包
COPY boot/target/boot-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]

