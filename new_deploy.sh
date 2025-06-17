#!/bin/bash
# Build multi-arquitetura e push para ECR usando docker buildx

PROJECT=$1

if [ -z "$PROJECT" ]; then
    echo "Uso: deploy.sh <nome_do_projeto>"
    echo "Erro: Nome do projeto não informado"
    exit 1
fi

PROJECT_ENV="env/${PROJECT}.env"

if [ ! -r "$PROJECT_ENV" ]; then
    echo "Erro: Arquivo ${PROJECT_ENV} não existe ou não é legível"
    exit 2
fi

# Carrega variáveis de ambiente
source .env
source "$PROJECT_ENV"

export ECR_REPO="${ECR_REPO_BASE}/${PROJECT}"
ECR_REPO_URI="${ECR_URI}/${ECR_REPO}"

# Cria e usa um builder buildx se necessário
if ! docker buildx inspect multiarch-builder >/dev/null 2>&1; then
    docker buildx create --name multiarch-builder --use
else
    docker buildx use multiarch-builder
fi

docker buildx inspect --bootstrap

# Monta argumentos de build
BUILD_ARGS="--build-arg PLATFORM=linux/arm64 \
            --build-arg PROJECT=${PROJECT} \
            --build-arg JAVA_VERSION=${JAVA_VERSION}"

# Para cada TAG, faz o build e push
for TAG in $TAGS; do
    echo "📦 Construindo e enviando imagem: ${ECR_REPO_URI}:${TAG} para linux/arm64"

    docker buildx build \
        --platform linux/arm64 \
        -t "${ECR_REPO_URI}:${TAG}" \
        $BUILD_ARGS \
        --push \
        .
done
