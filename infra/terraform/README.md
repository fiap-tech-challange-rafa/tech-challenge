Infra Terraform - instruções (skeleton)

Objetivo: fornecer instruções rápidas para provisionamento local com kind/minikube e referências para módulos cloud.

Local (recomendado para desenvolvimento):
1. Instale `kind` (https://kind.sigs.k8s.io/) e `kubectl`.
2. Crie um cluster local: `kind create cluster --name tech-challenge`.
3. Construa a imagem local: `docker build -t tech-challenge:latest .`.
4. Carregue a imagem no cluster kind: `kind load docker-image tech-challenge:latest`.
5. Aplique os manifests Kubernetes: `kubectl apply -f k8s/`.

Cloud (opcional):
- Este diretório serve como ponto de partida para adicionar módulos Terraform para EKS/GKE/AKS e provisionamento de banco (RDS/Cloud SQL). Crie subpastas `cloud/eks` ou `cloud/gke` com seus `main.tf`, `variables.tf` e `outputs.tf`.

Notas:
- Não inclua credenciais no repositório. Utilize variáveis e arquivos de variáveis para terraform.
