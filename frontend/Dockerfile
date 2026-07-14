FROM node:22-alpine AS build

WORKDIR /app

# Accept build argument and make it available during build
ARG VITE_BFF_URL
ENV VITE_BFF_URL=$VITE_BFF_URL

RUN npm install -g pnpm

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]