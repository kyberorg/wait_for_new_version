FROM ghcr.io/graalvm/graalvm-ce:21.2.0 AS build-aot

RUN curl https://downloads.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz -o /tmp/maven.tar.gz
RUN tar xf /tmp/maven.tar.gz -C /opt
RUN ln -s /opt/apache-maven-3.8.4 /opt/maven
RUN ln -s /opt/graalvm-ce-java11-21.2.0 /opt/graalvm
RUN gu install native-image

ENV JAVA_HOME=/opt/graalvm
ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}
ENV PATH=${JAVA_HOME}/bin:${PATH}

COPY ./pom.xml ./pom.xml
COPY src ./src/
COPY reflect.json /reflect.json

ENV MAVEN_OPTS='-Xmx10g'
RUN mvn clean package

FROM quay.io/kyberorg/golang:1.17.5 as entrypointBuild
WORKDIR /go/src/app
COPY cmd/entrypoint.go cmd/entrypoint.go

RUN  GO111MODULE=off CGO_ENABLED=0 go install ./...

# Create a minimal docker container and copy the app into it
FROM gcr.io/distroless/static AS final
WORKDIR /app

ENV javax.net.ssl.trustStore /cacerts
ENV javax.net.ssl.trustAnchors /cacerts

COPY --from=build-aot --chown=nonroot:nonroot target/wait4version /app/wait4version
COPY --from=build-aot --chown=nonroot:nonroot /opt/graalvm/lib/libsunec.so /libsunec.so
COPY --from=build-aot --chown=nonroot:nonroot /opt/graalvm/lib/security/cacerts /cacerts

COPY --from=entrypointBuild --chown=nonroot:nonroot /go/bin/cmd /app/entrypoint
RUN chmod +x /app/entrypoint

USER nonroot:nonroot
ENTRYPOINT ["/app/entrypoint"]
