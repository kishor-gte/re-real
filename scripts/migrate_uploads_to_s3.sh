#!/usr/bin/env bash
# Usage: set AWS credentials in env or configured profile, then run
# APP_S3_BUCKET=my-bucket APP_S3_REGION=us-east-1 ./scripts/migrate_uploads_to_s3.sh

set -euo pipefail

if [ -z "${APP_S3_BUCKET:-}" ]; then
  echo "Please set APP_S3_BUCKET environment variable"
  exit 1
fi

echo "Syncing local uploads/ to s3://${APP_S3_BUCKET}/uploads/"
aws s3 sync uploads/ s3://${APP_S3_BUCKET}/uploads/ --acl private
echo "Done"
