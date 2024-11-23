FROM eclipse-temurin:17-jre-alpine

WORKDIR /data

# Install required packages
RUN apk add --no-cache \
    curl \
    jq \
    bash \
    wget

# Environment variables
ENV MINECRAFT_VERSION="latest" \
    PAPER_BUILD="latest" \
    SERVER_PORT=25565 \
    RCON_PORT=25575 \
    JAVA_MEMORY=4G \
    EULA=false

# Download and set up PaperMC
RUN wget -O /usr/local/bin/papermc-install.sh https://raw.githubusercontent.com/mtoensing/Docker-Minecraft-PaperMC-Server/master/install.sh && \
    chmod +x /usr/local/bin/papermc-install.sh && \
    /usr/local/bin/papermc-install.sh

# Create plugins directory
RUN mkdir -p /data/plugins

# Copy server configurations
COPY config/paper.yml /data/paper.yml
COPY config/server.properties /data/server.properties
COPY config/spigot.yml /data/spigot.yml
COPY config/bukkit.yml /data/bukkit.yml

# Copy plugins
COPY plugins/ /data/plugins/

# Set up entrypoint script
COPY scripts/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE ${SERVER_PORT} ${RCON_PORT}

ENTRYPOINT ["/entrypoint.sh"]
