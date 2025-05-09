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

ENTRYPOINT ["sleep", "infinity"]

#SHELL ["/bin/bash", "-c"]
#
#ARG HOME=/home/vscode
#
#USER vscode
#WORKDIR $HOME
#
#ARG JDK_VERSION=21.0.7-librca
#
#RUN curl -fsSL "https://get.sdkman.io" | bash - \
#    && source "$HOME/.sdkman/bin/sdkman-init.sh" \
#    && sdk install java $JDK_VERSION \
#    && sdk default java $JDK_VERSION \
#    && sdk install maven
#
#ARG NODE_VERSION=20
#ARG NVM_VERSION=v0.40.3
#ENV NVM_DIR=$HOME/.nvm
#
## install nvm
#RUN curl -o- "https://raw.githubusercontent.com/nvm-sh/nvm/$NVM_VERSION/install.sh" | bash - \
#    && bash -c "source $NVM_DIR/nvm.sh && nvm install $NODE_VERSION && nvm use $NODE_VERSION && npm install -g yarn"
