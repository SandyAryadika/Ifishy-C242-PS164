FROM node:18-alpine

WORKDIR /usr/src/app
COPY package*.json ./

RUN npm install

COPY . .
COPY .env /usr/src/app/.env

EXPOSE 8080
CMD ["node", "server.js"]