FROM quay.io/keycloak/keycloak:latest

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure the database vendor
ENV KC_DB=mysql

# Set environment variables for MySQL connection
ENV KC_DB_URL=jdbc:mysql://mysql_keycloak:3308/keycloak_db
ENV KC_DB_USERNAME=KEYCLOAK_DB_USER
ENV KC_DB_PASSWORD=KEYCLOAK_DB_PASSWORD
ENV KC_HOSTNAME=localhost

# Set environment variables for Keycloak connection
ENV KEYCLOAK_ADMIN=KEYCLOAK_ADMIN
ENV KEYCLOAK_ADMIN_PASSWORD=KEYCLOAK_ADMIN_PASSWORD

# Configurar la importación del realm
ENV KEYCLOAK_IMPORT=/opt/keycloak/data/import/back-dmh-realm.json

# Copiar el archivo de exportación del realm
COPY realm-export.json /opt/keycloak/data/import/back-dmh-realm.json

# Set the working directory
WORKDIR /opt/keycloak

# Start Keycloak
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
