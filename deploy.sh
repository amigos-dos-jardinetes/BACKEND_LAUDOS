source aws/aws.env

# Cria a imagem localmente
docker compose build

# Autentica no ECR
aws ecr get-login-password --region ${ECR_REGION} | docker login --username AWS --password-stdin ${ECR_URI}

# Aplca tags à imagem
for t in ${TAGS}; do
    docker tag ${ECR_REPO}:backend ${ECR_REPO_URI}:${t}
done

# Envia imagem para o ECR
docker push --all-tags ${ECR_REPO_URI}
