# 1. Dùng image nền có JDK 17
FROM eclipse-temurin:17-jre-alpine AS builder


# Copy jar (giả sử build xong bằng Maven)
COPY ./target/*.jar ./app.jar

# Extract layered JAR content
RUN java -Djarmode=tools -jar ./app.jar extract --layers --launcher

# 2. Stage: Final image
FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# 4. Mở cổng mặc định Spring Boot (nếu cần dùng bên ngoài)
EXPOSE 8080

# Spring Boot sẽ tìm đúng cấu trúc đã extract
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]




