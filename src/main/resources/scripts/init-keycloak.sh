#!/bin/bash

# Wait until Keycloak is fully up
until curl -s http://localhost:8080/realms/master; do
  echo "Waiting for Keycloak to start..."
  sleep 10
done

# Authenticate to master realm (admin realm)
echo "Logging into Keycloak admin..."
/opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user admin \
  --password admin

# Apply brute-force protection to the 'brave' realm
echo "Applying brute-force protection to realm 'brave'..."
/opt/keycloak/bin/kcadm.sh update realms/brave \
  -s bruteForceProtected=true \
  -s permanentLockout=false \
  -s failureFactor=3 \
  -s maxFailureWaitSeconds=180 \
  -s minimumQuickLoginWaitSeconds=30 \
  -s waitIncrementSeconds=30 \
  -s quickLoginCheckMilliSeconds=1000 \
  -s maxDeltaTimeSeconds=900

echo "Brute-force settings applied successfully!"
