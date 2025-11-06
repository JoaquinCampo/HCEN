# HCEN (Sistema Central)

Sistema central de gestión de salud que coordina clínicas y pacientes a través de una arquitectura multi-tenant.

## Despliegue Rápido

### Buildear y Push a ECR

```bash
# 1. Autenticarse en ECR
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 879753557246.dkr.ecr.us-east-2.amazonaws.com

# 2. Buildear y subir imagen
docker buildx build \
  --platform linux/amd64 \
  -t 879753557246.dkr.ecr.us-east-2.amazonaws.com/tse/hcen:latest \
  --push .
```
