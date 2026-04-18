#!/bin/sh
set -eu

API_BASE_URL="${APP_API_BASE_URL:-${VITE_API_BASE_URL:-/api}}"

cat <<EOF >/usr/share/nginx/html/runtime-config.js
window.__APP_CONFIG__ = Object.assign({}, window.__APP_CONFIG__, {
  apiBaseUrl: "${API_BASE_URL}"
});
EOF
