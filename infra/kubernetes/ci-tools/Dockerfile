FROM alpine:3.18

# Install the usual suspects for pipelines
RUN apk add --no-cache \
    bash \
    curl \
    jq \
    git \
    ca-certificates \
    && rm -rf /var/cache/apk/*

# Default shell
CMD ["/bin/bash"]