#!/bin/bash

# ==============================================================================
# OTT API Gateway Rate Limiter Test Script
# ==============================================================================

# 1. Variables
GATEWAY_URL="http://localhost:8080/catalog/products"
TOTAL_REQUESTS=30
DELAY=0.05 # Delay in seconds between requests (50ms)

# 2. Get a valid JWT Token from AuthController
echo "Fetching active JWT token from Gateway AuthController..."
AUTH_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username": "john.doe", "role": "USER"}')

# Extract token using simple sed pattern matching (avoiding extra jq dependency)
JWT_TOKEN=$(echo $AUTH_RESPONSE | sed -E 's/.*"token":"([^"]*)".*/\1/')

if [ -z "$JWT_TOKEN" ] || [[ "$JWT_TOKEN" == *"error"* ]]; then
    echo "❌ Error: Could not retrieve a valid JWT token. Is your Gateway running?"
    exit 1
fi

echo "✅ Token retrieved successfully."
echo "------------------------------------------------------------"
echo "Starting Onslaught: Sending $TOTAL_REQUESTS rapid requests..."
echo "------------------------------------------------------------"

success_count=0
blocked_count=0

# 3. Request Execution Loop
for ((i=1; i<=TOTAL_REQUESTS; i++))
do
    # Execute the request and grab the HTTP status code
    STATUS_CODE=$(curl -o /dev/null -s -w "%{http_code}" -X GET "$GATEWAY_URL" \
      -H "Authorization: Bearer $JWT_TOKEN")

    if [ "$STATUS_CODE" == "200" ]; then
        echo "Request #$i: Status $STATUS_CODE ✅ (Processed)"
        ((success_count++))
    elif [ "$STATUS_CODE" == "429" ]; then
        echo "Request #$i: Status $STATUS_CODE ❌ (Rate Limited - Too Many Requests)"
        ((blocked_count++))
    else
        echo "Request #$i: Status $STATUS_CODE ⚠️ (Other Error - Check your downstream service)"
    fi

    # Tiny sleep interval to control the blast velocity
    sleep $DELAY
done

echo "------------------------------------------------------------"
echo "📊 Test Run Summary:"
echo "Processed (200 OK): $success_count"
echo "Rate Limited (429): $blocked_count"
echo "------------------------------------------------------------"