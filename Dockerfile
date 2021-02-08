# Create a minimal docker container and copy the app into it
FROM alpine:latest
WORKDIR /app
COPY target/wait4version wait4version
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh && chmod +x wait4version
ENTRYPOINT ["/app/entrypoint.sh"]
