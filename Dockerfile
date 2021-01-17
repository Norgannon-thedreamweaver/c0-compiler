# gradle 好大
FROM gradle:jdk14
WORKDIR /app
COPY build.gradle gradle settings.gradle c0-compiler.iml /app/
COPY src /app/src
RUN gradle fatjar --no-daemon
