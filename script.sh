#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(pwd)"
echo "Working directory: $ROOT_DIR"

# Basic prereq checks
command -v java >/dev/null 2>&1 || { echo "java not found in PATH; install JDK 21 or set JAVA_HOME."; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo "mvn not found in PATH; install Maven."; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "docker not found in PATH; install Docker."; exit 1; }
# docker-compose may be docker compose or docker-compose
if ! command -v docker-compose >/dev/null 2>&1 && ! docker compose version >/dev/null 2>&1; then
  echo "docker-compose not found (neither 'docker-compose' nor 'docker compose'). Install docker-compose or use Docker's compose plugin."
  exit 1
fi

# 1) Create ResourceNotFoundException
mkdir -p src/main/java/com/devhub/exception
cat > src/main/java/com/devhub/exception/ResourceNotFoundException.java <<'EOF'
package com.devhub.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() { super(); }
    public ResourceNotFoundException(String message) { super(message); }
    public ResourceNotFoundException(String message, Throwable cause) { super(message, cause); }
}
