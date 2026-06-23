docker build -t ms-user:1.0 ./backend/ms-user      
docker build -t ms-inventory:1.0 ./backend/ms-inventory
docker build -t ms-orders:1.0 ./backend/ms-orders
docker build -t ms-restock:2.0 ./backend/ms-restock
docker build -t bff:3.0 ./backend/bff
docker build -t frontend:1.0 ./frontend

# Apply namespace first
kubectl apply -f k8s/namespace.yaml

# Apply all services (order matters for dependencies)
kubectl apply -f backend/ms-user/k8s/
kubectl apply -f backend/ms-inventory/k8s/
kubectl apply -f backend/ms-orders/k8s/
kubectl apply -f backend/ms-restock/k8s/
kubectl apply -f backend/bff/k8s/
kubectl apply -f krakend/k8s/
kubectl apply -f frontend/k8s/

# Apply ingress
kubectl apply -f k8s/ingress-traefik.yaml
# kubectl apply -f k8s/traefik-middleware-cors.yaml
# kubectl apply -f k8s/traefik-service-account.yaml

# Verify deployment
kubectl get all -n smartlogix
kubectl get ingress -n smartlogix