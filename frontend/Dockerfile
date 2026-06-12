# Etapa 1: Compilación
FROM node:22-alpine AS build
WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./
RUN npm ci --only=production

# Copiar código fuente y compilar
COPY . .
RUN pnpm install
RUN pnpm install axios react-router-dom
RUN pnpm run build

# Etapa 2: Servir con Nginx
FROM nginx:alpine

# Copiar los archivos compilados
COPY --from=build /app/dist /usr/share/nginx/html

# Copiar configuración personalizada de nginx (opcional)
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]