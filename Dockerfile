# Create a minimal docker container and copy the app into it
FROM alpine:latest
WORKDIR /app
COPY target/wait4version.jar wait4version.jar
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh
ENTRYPOINT ["/app/entrypoint.sh"]
