# SmartLogix — Kubernetes Deployment Runbook

## File layout

```
k8s/                          shared/cluster-wide resources
  namespace.yaml
  secrets.yaml
  ingress.yaml

backend/ms-user/k8s/          ms-user + its own postgres
backend/ms-inventory/k8s/     ms-inventory + its own postgres
backend/ms-orders/k8s/        ms-orders + its own postgres
backend/ms-restock/k8s/       ms-restock + its own postgres
backend/bff/k8s/              KrakenD gateway (config as a ConfigMap)
frontend/k8s/                 static frontend
```

Namespace and secrets live at the top level because they aren't owned by any
single service. Everything else sits inside the service folder it belongs to,
matching your existing `backend/<service>/` layout.

## 0. Prerequisites

- A running cluster (kind, minikube, or a real one) and `kubectl` pointed at it.
- An ingress controller installed, e.g.:
  ```bash
  kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
  ```
- Docker images for all 6 services, built and either pushed to a registry your
  cluster can pull from, or loaded directly into a local cluster:
  ```bash
  # kind
  kind load docker-image smartlogix/ms-user:latest
  kind load docker-image smartlogix/ms-inventory:latest
  kind load docker-image smartlogix/ms-orders:latest
  kind load docker-image smartlogix/ms-restock:latest
  kind load docker-image smartlogix/bff:latest
  kind load docker-image smartlogix/frontend:latest

  # minikube (alternative)
  minikube image load smartlogix/ms-user:latest
  # ...repeat for each image
  ```

## 1. IMPORTANT: rebuild the frontend with the right BFF URL first

`VITE_BFF_URL` is baked into the frontend's static JS at **build time** by
Vite — it cannot be changed by an env var on the running container. Before
building the frontend image, decide the hostname the Ingress below will use
(default in `k8s/ingress.yaml` is `api.smartlogix.local`), then:

```bash
cd frontend
docker build --build-arg VITE_BFF_URL=http://api.smartlogix.local -t smartlogix/frontend:latest .
```

If your frontend's Dockerfile doesn't yet declare `ARG VITE_BFF_URL` /
`ENV VITE_BFF_URL=$VITE_BFF_URL` before the `npm run build` step, add that —
otherwise the build arg has no effect and it'll fall back to
`http://localhost:8080`, which won't resolve from someone else's browser.

Build the rest of the images normally, e.g.:
```bash
docker build -t smartlogix/ms-user:latest backend/ms-user
docker build -t smartlogix/ms-inventory:latest backend/ms-inventory
docker build -t smartlogix/ms-orders:latest backend/ms-orders
docker build -t smartlogix/ms-restock:latest backend/ms-restock
docker build -t smartlogix/bff:latest backend/bff
```

## 2. Local DNS (kind/minikube only)

Point both Ingress hostnames at your cluster's ingress address (for kind with
the standard extraPortMappings setup, that's usually `127.0.0.1`):

```bash
echo "127.0.0.1 smartlogix.local api.smartlogix.local" | sudo tee -a /etc/hosts
```

## 3. Apply everything, in order

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets.yaml

kubectl apply -f backend/ms-user/k8s/
kubectl apply -f backend/ms-inventory/k8s/
kubectl apply -f backend/ms-orders/k8s/
kubectl apply -f backend/ms-restock/k8s/

kubectl apply -f backend/bff/k8s/
kubectl apply -f frontend/k8s/

kubectl apply -f k8s/ingress.yaml
```

Each microservice Deployment has an `initContainer` that polls its own
Postgres with `pg_isready` before the app container starts, so apply order
between a service and its database doesn't matter — but applying
`k8s/secrets.yaml` before anything else does, since every Deployment
references it.

## 4. Check status

```bash
kubectl get pods -n smartlogix -w
kubectl get ingress -n smartlogix
```

Once everything is `Running`/`1/1`, visit `http://smartlogix.local` in a
browser.

## 5. Making config changes without a rebuild

`krakend.json` and `jwk.json` are mounted from the `bff-config` ConfigMap
(`backend/bff/k8s/configmap.yaml`), not baked into the image. To change
gateway routing or JWT settings:

```bash
# edit backend/bff/k8s/configmap.yaml, then:
kubectl apply -f backend/bff/k8s/configmap.yaml
kubectl rollout restart deployment/bff -n smartlogix
```

## 6. Known gaps / things to decide before production use

- `k8s/secrets.yaml` has plaintext credentials committed for convenience —
  swap for a real secrets manager (Sealed Secrets, External Secrets, Vault,
  cloud KMS-backed secret store) before this goes anywhere real.
- Postgres runs as a single-replica Deployment with a PVC, not a StatefulSet
  — fine for local/dev, but for anything with real durability requirements
  look at a managed Postgres or an operator (Zalando, CloudNativePG, etc).
- No `HorizontalPodAutoscaler` or `PodDisruptionBudget` — all Deployments are
  `replicas: 1`.
- Readiness/liveness probes use raw TCP checks since none of the Java
  services expose `spring-boot-actuator`'s `/actuator/health` — adding that
  dependency would let these probes verify the app is actually healthy
  (DB connectivity, etc.), not just that a port is open.
