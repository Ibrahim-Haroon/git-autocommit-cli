FROM ukgartifactory.pe.jfrog.io/images/base/openjdk-jdk:11 as builder

WORKDIR /app
COPY . ./

USER root

# If building this Dockerfile on a Windows host, uncomment the next 2 lines
#RUN apt install gradle
#RUN gradle wrapper --gradle-version 6.5

RUN ./gradlew shadowJar --no-daemon

FROM ukgartifactory.pe.jfrog.io/images/base/openjdk-jre:11-ubuntu
LABEL com.jfrog.artifactory.retention.maxCount="5"

USER root
RUN mkdir -p /app && chown ${SERVICE_USER}:${SERVICE_GROUP} /app && chmod g+w /app

COPY --from=builder /app/build/libs/git-auto-commit-cli-1.0-SNAPSHOT*all.jar ./autocommit.jar
RUN chown ${SERVICE_USER}:${SERVICE_GROUP} ./autocommit.jar

# Tools Used for Troubleshooting from inside the pod
RUN apt-get update
RUN apt-get -y install telnet
RUN apt-get -y install openssl
RUN apt-get -y install curl
RUN apt-get -y install git

USER notroot
WORKDIR /app

ENV JAVA_OPTS "-XX:+UseContainerSupport -XX:MinRAMPercentage=50.0 -XX:MaxRAMPercentage=80.0 -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/urandom"
ENV CONTAINER true

ENTRYPOINT ["/usr/local/bin/java.sh","-jar","/app/autocommit.jar"]