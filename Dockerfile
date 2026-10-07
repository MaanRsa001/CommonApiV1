FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

COPY settings.xml /root/.m2/settings.xml

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8085 9011

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=60", "-XX:MetaspaceSize=128m", "-XX:MaxMetaspaceSize=1024m", "-XX:MaxDirectMemorySize=650m", "-XX:+ExitOnOutOfMemoryError", "-Xlog:gc*,metaspace", "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.port=9011", "-Dcom.sun.management.jmxremote.rmi.port=9011", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-Djava.rmi.server.hostname=192.168.1.185", "-jar", "app.jar"]
 