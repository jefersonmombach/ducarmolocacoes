FROM mcr.microsoft.com/devcontainers/java:1-8-bullseye

ARG NODE_VERSION=v8.12.0
ARG NPM_VERSION=6.4.1

# Install the exact Node.js/NPM versions expected by this project.
RUN set -eux; \
    arch="$(dpkg --print-architecture)"; \
    case "${arch}" in \
      amd64) node_arch="x64" ;; \
      arm64) node_arch="arm64" ;; \
      *) echo "Unsupported architecture: ${arch}"; exit 1 ;; \
    esac; \
    curl -fsSL "https://nodejs.org/dist/${NODE_VERSION}/node-${NODE_VERSION}-linux-${node_arch}.tar.xz" -o /tmp/node.tar.xz; \
    tar -xJf /tmp/node.tar.xz -C /usr/local --strip-components=1 --no-same-owner; \
    rm -f /tmp/node.tar.xz; \
    npm install -g "npm@${NPM_VERSION}" "@angular/cli@6.1.2"; \
    npm cache clean --force
