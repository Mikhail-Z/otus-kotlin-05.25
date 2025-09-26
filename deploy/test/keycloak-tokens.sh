#!/bin/bash

ENV_DIR="../env"
KCHOST=http://localhost:8080

# Проверяем, существует ли папка
if [ ! -d "$ENV_DIR" ]; then
  echo "Папка $ENV_DIR не найдена"
  exit 1
fi

# Загружаем все переменные из файлов в папке env/
for file in "$ENV_DIR"/*; do
  if [ -f "$file" ]; then
    export $(grep -v '^#' "$file" | xargs)
    echo $(grep -v '^#' "$file" | xargs)
  fi
done

# Получаем токен
ACCESS_TOKEN=$(curl -s \
  -d "client_id=$CLIENT_ID" \
  -d "username=$KEYCLOAK_ADMIN" \
  -d "password=$KEYCLOAK_ADMIN_PASSWORD" \
  -d "grant_type=password" \
  "$KCHOST/realms/$REALM/protocol/openid-connect/token" | jq -r '.access_token')

# Проверка на успех
if [ "$ACCESS_TOKEN" == "null" ] || [ -z "$ACCESS_TOKEN" ]; then
  echo "Не удалось получить токен"
  exit 1
fi