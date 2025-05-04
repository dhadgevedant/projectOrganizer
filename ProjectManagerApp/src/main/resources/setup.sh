#!/bin/bash

# Configuration
DB_USER="root"
DB_PASS=""
DB_HOST="localhost"
SCHEMA_FILE="schema.sql"

# Run schema.sql
if [ ! -f "$SCHEMA_FILE" ]; then
  echo "Schema file not found at $SCHEMA_FILE"
  exit 1
fi

echo "Running schema.sql..."
sudo mysql   < "$SCHEMA_FILE"

if [ $? -eq 0 ]; then
  echo "Schema executed successfully."
else
  echo "Failed to execute schema."
fi
