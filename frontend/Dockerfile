FROM node:latest

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

COPY . .

RUN npm install -g @angular/cli

CMD ["ng", "serve", "--host", "0.0.0.0"]
