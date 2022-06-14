# => Build container
FROM node:alpine as builder
WORKDIR /app
COPY package.json .
COPY yarn.lock .
RUN yarn
COPY . .
RUN yarn build

# => Run container
FROM nginx:mainline-alpine

# Nginx config
RUN rm -rf /etc/nginx/conf.d
COPY conf /etc/nginx

# Static build
COPY --from=builder /app/build /usr/share/nginx/html/

# Default port exposure
EXPOSE 8080

# Copy .env file and shell script to container
WORKDIR /usr/share/nginx/html
COPY ./env.sh .

# Generate empty file for parameters
RUN echo "REACT_APP_TERRAKUBE_API_URL=" >> .env &&\
    echo "REACT_APP_CLIENT_ID=" >> .env &&\
    echo "REACT_APP_AUTHORITY=" >> .env &&\
    echo "REACT_APP_REDIRECT_URI=" >>.env &&\
    echo "REACT_APP_REGISTRY_URI=" >>.env &&\
    echo "REACT_APP_SCOPE=" >>.env

# Add bash
RUN apk add --no-cache bash

# Make our shell script executable
RUN chmod +x env.sh

# Start Nginx server
CMD ["/bin/bash", "-c", "/usr/share/nginx/html/env.sh && nginx -g \"daemon off;\""]