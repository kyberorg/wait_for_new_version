# Create a minimal Java docker container and copy the app into it
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
WORKDIR /app
COPY target/wait4version.jar wait4version.jar
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh
ENTRYPOINT ["/app/entrypoint.sh"]
