docker build -t ms-user:1.0 ./backend/ms-user      
docker build -t ms-inventory:1.0 ./backend/ms-inventory
docker build -t ms-orders:1.0 ./backend/ms-orders
docker build -t ms-restock:1.0 ./backend/ms-restock
docker build -t bff:1.0 ./backend/bff
docker build -t frontend:1.0 ./frontend

winget install Helm.Helm
helm repo add traefik https://traefik.github.io/charts
helm repo update
kubectl create namespace smartlogix  # Only if namespace doesn't exist yet
helm install traefik traefik/traefik --namespace smartlogix

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

# Verify deployment
kubectl get all -n smartlogix
kubectl get ingress -n smartlogix

kubectl port-forward -n smartlogix service/krakend 8081:8080
kubectl port-forward -n smartlogix service/frontend 8080:80
# Then access at http://localhost:8080