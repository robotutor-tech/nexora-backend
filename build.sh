#!/bin/bash
set -e  # Exit on error

# Generate version tag from date
newTag=$(date +%y.%m.%d)
echo "Building Docker image with tag: $newTag"

# Build and tag Docker image with multi-stage build
docker build -t shiviraj/nexora-backend:latest -t shiviraj/nexora-backend:$newTag .

# Push images to registry
echo "Pushing images to Docker registry..."
docker push shiviraj/nexora-backend:latest
docker push shiviraj/nexora-backend:$newTag

echo "Build and push completed successfully!"

