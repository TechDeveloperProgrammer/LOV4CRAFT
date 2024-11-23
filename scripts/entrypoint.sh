#!/bin/bash
set -e

# Accept EULA if set to true
if [ "${EULA}" == "true" ] || [ "${EULA}" == "TRUE" ]; then
    echo "eula=true" > /data/eula.txt
fi

# Set memory limits in JVM arguments
JVM_OPTS="-Xms${JAVA_MEMORY} -Xmx${JAVA_MEMORY}"

# Additional JVM optimizations
JVM_OPTS="${JVM_OPTS} -XX:+UseG1GC"
JVM_OPTS="${JVM_OPTS} -XX:+ParallelRefProcEnabled"
JVM_OPTS="${JVM_OPTS} -XX:MaxGCPauseMillis=200"
JVM_OPTS="${JVM_OPTS} -XX:+UnlockExperimentalVMOptions"
JVM_OPTS="${JVM_OPTS} -XX:+DisableExplicitGC"
JVM_OPTS="${JVM_OPTS} -XX:+AlwaysPreTouch"
JVM_OPTS="${JVM_OPTS} -XX:G1NewSizePercent=30"
JVM_OPTS="${JVM_OPTS} -XX:G1MaxNewSizePercent=40"
JVM_OPTS="${JVM_OPTS} -XX:G1HeapRegionSize=8M"
JVM_OPTS="${JVM_OPTS} -XX:G1ReservePercent=20"
JVM_OPTS="${JVM_OPTS} -XX:G1HeapWastePercent=5"
JVM_OPTS="${JVM_OPTS} -XX:G1MixedGCCountTarget=4"
JVM_OPTS="${JVM_OPTS} -XX:InitiatingHeapOccupancyPercent=15"
JVM_OPTS="${JVM_OPTS} -XX:G1MixedGCLiveThresholdPercent=90"
JVM_OPTS="${JVM_OPTS} -XX:G1RSetUpdatingPauseTimePercent=5"
JVM_OPTS="${JVM_OPTS} -XX:SurvivorRatio=32"
JVM_OPTS="${JVM_OPTS} -XX:+PerfDisableSharedMem"
JVM_OPTS="${JVM_OPTS} -XX:MaxTenuringThreshold=1"
JVM_OPTS="${JVM_OPTS} -Dusing.aikars.flags=https://mcflags.emc.gs"
JVM_OPTS="${JVM_OPTS} -Daikars.new.flags=true"

# Create required directories if they don't exist
mkdir -p /data/plugins
mkdir -p /data/world
mkdir -p /data/logs

# Start the Minecraft server
exec java ${JVM_OPTS} -jar /data/paper.jar --nogui
