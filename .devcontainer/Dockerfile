FROM mcr.microsoft.com/devcontainers/base:ubuntu

# Install necessary packages
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends \
    curl \
    git \
    wget \
    gnupg \
    lsb-release \
    ca-certificates \
    apt-transport-https \
    software-properties-common \
    unzip \
    zip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY rootCA.pem /usr/local/share/ca-certificates/root.crt

RUN update-ca-certificates

ENTRYPOINT ["sleep", "infinity"]
