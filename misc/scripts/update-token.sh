#!/bin/bash

# Script to update the OAuth token in connect.conf
# Requires environment variables: OAUTH_URL, CLIENT_ID, CLIENT_SECRET

enable_strict_mode() {
  set -e
  set -u
  if (set -o pipefail) 2>/dev/null; then
    set -o pipefail
  fi
}

enable_strict_mode

# Check if required environment variables are set
if [[ -z "${OAUTH_URL:-}" || -z "${CLIENT_ID:-}" || -z "${CLIENT_SECRET:-}" ]]; then
    echo "Error: Required environment variables are not set."
    echo "Please set: OAUTH_URL, CLIENT_ID, CLIENT_SECRET"
    exit 1
fi

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Path to connect.conf (assuming it's in the misc directory, one level up from scripts)
CONNECT_CONF="${SCRIPT_DIR}/../connect.conf"

echo "Requesting new OAuth token..."

# Call the OAuth endpoint to get a new token
RESPONSE=$(curl -s -X POST \
  "${OAUTH_URL}" \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d "grant_type=client_credentials&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}")

# Check if curl was successful
if [[ $? -ne 0 ]]; then
    echo "Error: Failed to call OAuth endpoint"
    exit 1
fi

# Extract the access_token from the JSON response using jq or basic parsing
if command -v jq &> /dev/null; then
    # Use jq if available
    NEW_TOKEN=$(echo "$RESPONSE" | jq -r '.access_token')
    if [[ "$NEW_TOKEN" == "null" || -z "$NEW_TOKEN" ]]; then
        echo "Error: Could not extract access_token from response"
        echo "Response: $RESPONSE"
        exit 1
    fi
else
    # Fallback to basic parsing if jq is not available
    NEW_TOKEN=$(echo "$RESPONSE" | sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p')
    if [[ -z "$NEW_TOKEN" ]]; then
        echo "Error: Could not extract access_token from response"
        echo "Response: $RESPONSE"
        echo "Note: Install 'jq' for better JSON parsing"
        exit 1
    fi
fi

echo "Successfully obtained new token"

# Check if connect.conf exists
if [[ ! -f "$CONNECT_CONF" ]]; then
    echo "Error: connect.conf not found at $CONNECT_CONF"
    exit 1
fi

# Create a backup of the original file
cp "$CONNECT_CONF" "${CONNECT_CONF}.backup.$(date +%Y%m%d_%H%M%S)"

# Update the token in connect.conf
# Use sed to replace the token value, handling multi-line tokens properly
sed -i "s|token = \"[^\"]*\"|token = \"$NEW_TOKEN\"|g" "$CONNECT_CONF"

echo "Token updated successfully in $CONNECT_CONF"
echo "Backup created at ${CONNECT_CONF}.backup.$(date +%Y%m%d_%H%M%S)"
