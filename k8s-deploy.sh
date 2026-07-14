# k8s-deploy.ps1
# PowerShell script for deploying SmartLogix to Kubernetes

Write-Host " Building Docker images with local tags..." -ForegroundColor Cyan
docker build -t ms-user:1.0 ./backend/ms-user
docker build -t ms-inventory:1.0 ./backend/ms-inventory
docker build -t ms-orders:1.0 ./backend/ms-orders
docker build -t ms-restock:1.0 ./backend/ms-restock
docker build -t bff:1.0 ./backend/bff
# Build frontend with VITE_BFF_URL build arg
docker build --build-arg VITE_BFF_URL=http://api.smartlogix.local -t frontend:1.0 ./frontend


Write-Host "Installing Traefik ingress controller..." -ForegroundColor Cyan
helm repo add traefik https://traefik.github.io/charts
helm repo update
# Create namespace if it doesn't exist (ignore errors)
kubectl create namespace smartlogix 2>$null
helm install traefik traefik/traefik --namespace smartlogix --set service.type=NodePort

Write-Host "Applying shared resources (namespace & secrets)..." -ForegroundColor Cyan
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets.yaml

Write-Host "Deploying microservices (local images, pullPolicy: Never)..." -ForegroundColor Cyan
kubectl apply -f backend/ms-user/k8s/
kubectl apply -f backend/ms-inventory/k8s/
kubectl apply -f backend/ms-orders/k8s/
kubectl apply -f backend/ms-restock/k8s/

Write-Host "Deploying KrakenD gateway (image: bff:1.0, service name: krakend)..." -ForegroundColor Cyan
kubectl apply -f backend/bff/k8s/

Write-Host "Deploying frontend..." -ForegroundColor Cyan
kubectl apply -f frontend/k8s/

Write-Host "Applying Traefik Ingress..." -ForegroundColor Cyan
kubectl apply -f k8s/ingress-traefik.yaml

Write-Host "Deployment complete. Waiting for pods to be ready..." -ForegroundColor Green
kubectl wait --for=condition=ready pod -l app=ms-user -n smartlogix --timeout=120s
kubectl wait --for=condition=ready pod -l app=ms-inventory -n smartlogix --timeout=120s
kubectl wait --for=condition=ready pod -l app=ms-orders -n smartlogix --timeout=120s
kubectl wait --for=condition=ready pod -l app=ms-restock -n smartlogix --timeout=120s
kubectl wait --for=condition=ready pod -l app=krakend -n smartlogix --timeout=120s
kubectl wait --for=condition=ready pod -l app=frontend -n smartlogix --timeout=120s

Write-Host "`n Current resources in namespace smartlogix:" -ForegroundColor Cyan
kubectl get all -n smartlogix
kubectl get ingress -n smartlogix

Write-Host "`n Port-forwarding services:" -ForegroundColor Yellow
Write-Host "  - KrakenD gateway: http://localhost:8081"
Write-Host "  - Frontend:        http://localhost:8080"
Write-Host "`nRun these commands in separate terminals:"
Write-Host "  kubectl port-forward -n smartlogix service/krakend 8081:8080"
Write-Host "  kubectl port-forward -n smartlogix service/frontend 8080:80"