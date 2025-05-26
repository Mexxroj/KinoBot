# 1. Build bosqichi uchun Gradle va JDK image
FROM gradle:8.7-jdk17 AS build

# 2. Ishchi direktoriyani belgilaymiz
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src/

# --- BU QATORNI O'ZGARTIRING ---
# Endi 'build' o'rniga 'shadowJar' taskini ishlatamiz
RUN gradle shadowJar --no-daemon -x test
# ------------------------------

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# --- BU QATORNI HAM O'ZGARTIRING ---
# Shadow plugin tomonidan yaratilgan JAR fayl nomini ko'chiramiz.
# Odatda nom 'proyekt_nomi-versiya-all.jar' shaklida bo'ladi.
# Sizning holatingizda 'server-1.0-SNAPSHOT-all.jar' bo'lishi kerak.
COPY --from=build /app/build/libs/server-1.0-SNAPSHOT-all.jar app.jar
# ----------------------------------

# Expose the service port
EXPOSE 7140

ENTRYPOINT ["java", "-jar", "app.jar"]
