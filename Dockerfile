# 1. Dùng image nền có JDK 17
FROM eclipse-temurin:17-jre-alpine AS builder

# 2. Tạo thư mục làm việc trong container
WORKDIR /app

# Copy jar (giả sử build xong bằng Maven)
COPY target/*.jar app.jar

# Extract layered JAR content
RUN java -Djarmode=tools -Dspring-boot.jarmode.extract.target-dir=/app/layertemp -jar app.jar extract

RUN ls -R

# 2. Stage: Final image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy các layer đã extract từ builder stage
COPY --from=builder /app/layertemp/dependencies/ ./
COPY --from=builder /app/layertemp/spring-boot-loader/ ./
COPY --from=builder /app/layertemp/snapshot-dependencies/ ./
COPY --from=builder /app/layertemp/application/ ./

# 4. Mở cổng mặc định Spring Boot (nếu cần dùng bên ngoài)
EXPOSE 8080

# Spring Boot sẽ tìm đúng cấu trúc đã extract
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]



