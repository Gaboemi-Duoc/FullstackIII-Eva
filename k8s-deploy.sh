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