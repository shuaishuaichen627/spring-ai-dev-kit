# 使用固定版本的 OpenJDK 镜像
FROM openjdk:17-jdk-slim-bullseye

LABEL maintainer="Spring AI Dev Kit Team"
LABEL version="1.0.0"
LABEL description="Spring AI Dev Kit - 智能研发助手"

# 设置工作目录
WORKDIR /app

# 复制 jar 包
COPY boot/target/boot-1.0.0.jar app.jar

# 创建日志目录
RUN mkdir -p /var/log/spring-ai-dev-kit

# 创建报表导出目录
RUN mkdir -p /data/reports

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 设置 JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

