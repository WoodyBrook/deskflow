#!/usr/bin/env bash
set -euo pipefail

# Wait for Docker, then start the app stack.
until docker info >/dev/null 2>&1; do
  sleep 2
done
docker compose up -d --build

# Wait until the API is listening so the port is forwarded.
for _ in $(seq 1 45); do
  if curl -sf http://localhost:8080/api/health >/dev/null; then
    break
  fi
  sleep 2
done

# Codespaces resets public ports to private on restart; re-publish 8080.
# See: https://docs.github.com/en/codespaces/reference/security-in-github-codespaces
if [[ -n "${CODESPACE_NAME:-}" ]] && command -v gh >/dev/null 2>&1; then
  for _ in $(seq 1 8); do
    if gh codespace ports visibility 8080:public -c "$CODESPACE_NAME"; then
      echo "Port 8080 set to public"
      exit 0
    fi
    sleep 3
  done
  echo "Warning: could not set port 8080 to public" >&2
fi
