#!/usr/bin/env bash

enable_strict_mode() {
  set -e
  set -u
  if (set -o pipefail) 2>/dev/null; then
    set -o pipefail
  fi
}

enable_strict_mode

# Function to terminate port forwarding
function terminate_port_forward() {
  echo "Terminating port forwarding..."
  pids=$(pgrep -f "kubectl port-forward")
  if [ -n "$pids" ]; then
    for pid in $pids; do
      kill "$pid"
    done
  fi
}

# Check if kubectl is configured to access the cluster
if ! kubectl cluster-info &>/dev/null; then
  echo "Error: kubectl is not configured to access the cluster. Please configure kubectl."
  exit 1
fi

# Set up trap to call terminate_port_forward function on script exit
trap terminate_port_forward EXIT

participant_pod_name=$(kubectl get pods --all-namespaces | grep participant- | awk '{print $2}' | head -n 1)
if [ -z "$participant_pod_name" ]; then
    echo "Error: Could not find participant pod. Exiting."
    exit 1
fi
participant_ns=$(kubectl get pods --all-namespaces | grep participant- | awk '{print $1}' | head -n 1)
echo "Setting up port forwarding to $participant_pod_name..."
kubectl port-forward -n "$participant_ns" "$participant_pod_name" 5002:5002 &>/dev/null &
kubectl port-forward -n "$participant_ns" "$participant_pod_name" 5001:5001 &>/dev/null &

echo "Port forwarding established. Press Ctrl+C to exit..."

while true; do
  sleep 1
done