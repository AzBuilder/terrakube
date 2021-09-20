FROM node:lts-buster as build

WORKDIR /usr/local/app

COPY ./ /usr/local/app/

ARG BUILDER_API_URL
ENV REACT_CONFIG_AZBUILDER_URL=$BUILDER_API_URL

ARG CLIENT_ID
ENV REACT_CONFIG_CLIENT_ID=$CLIENT_ID

ARG AUTHORITY
ENV REACT_CONFIG_AUTHORITY=$AUTHORITY

ARG REDIRECT_URI
ENV REACT_CONFIG_REDIRECT=$REDIRECT_URI

RUN ./setupEnv.sh

RUN yarn install

RUN yarn build

WORKDIR /usr/local/app/server

RUN yarn install

FROM node:lts-alpine

# Copy the build output to replace the default nginx contents.
COPY --from=build /usr/local/app/build /opt/azbuilder/build/
COPY --from=build /usr/local/app/server /opt/azbuilder/server/

WORKDIR /opt/azbuilder/server

EXPOSE 3000

CMD [ "node", "server.js" ]