FROM mcr.microsoft.com/devcontainers/base:dev-ubuntu22.04

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

ENV ACCEPT_EULA=Y

RUN curl -sSL https://packages.microsoft.com/keys/microsoft.asc | tee /etc/apt/trusted.gpg.d/microsoft.asc && \
    add-apt-repository -y "$(wget -qO- https://packages.microsoft.com/config/ubuntu/$(lsb_release -rs)/prod.list)" && \
    apt-get update && \
    apt-get install -y mssql-tools18 unixodbc-dev && \
    ln -s /opt/mssql-tools18/bin/* /usr/local/bin/

COPY rootCA.pem /usr/local/share/ca-certificates/root.crt

RUN update-ca-certificates


ENTRYPOINT ["sleep", "infinity"]
