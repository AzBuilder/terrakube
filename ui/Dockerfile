#=> Build container
FROM node:alpine as builder
WORKDIR /app
COPY package.json .
COPY yarn.lock .
RUN yarn
COPY . .
RUN yarn build

# => Run container
FROM bitnami/nginx:latest

# Default Terraform version, updated at build time
ARG REACT_APP_TERRAKUBE_VERSION=2.0.0

# Static build
COPY --from=builder /app/build /app
COPY ./conf/conf.d/bitnami.conf /opt/bitnami/nginx/conf/server_blocks/my_server_block.conf