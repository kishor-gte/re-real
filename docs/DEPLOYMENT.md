## Deployment checklist

1) Secrets & configuration
   - Do NOT store secrets in `application.properties`. Use environment variables or a secret manager.
   - Required env vars (see `.env.example`): `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `APP_JWT_SECRET`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`.

2) Build & run (local with Docker)
```bash
docker build -t realestate:latest .
docker run -e SPRING_DATASOURCE_USERNAME=... -e SPRING_DATASOURCE_PASSWORD=... -p 8081:8080 realestate:latest
```

3) Runtime notes
   - Use object storage (S3/Azure Blob) for uploads in production; avoid local `uploads/` directory for multi-instance deployments.
   - Terminate TLS at load-balancer or use sidecar; set `APP_PUBLIC_BASE_URL` if behind proxy.
   - Provide `APP_JWT_SECRET` via secret manager and rotate regularly.

4) CI/Quality
   - GitHub Actions workflow `/.github/workflows/ci.yml` runs tests and dependency checks.
   - Dependabot is enabled via `/.github/dependabot.yml`.

5) Database migrations
   - Flyway is configured; place migrations under `src/main/resources/db/migration`.

6) Observability
   7) Uploads migration to S3

      - Configure `APP_S3_BUCKET` and `APP_S3_REGION` environment variables.
      - Install and configure AWS CLI with credentials, then run the provided script:

   ```bash
   APP_S3_BUCKET=my-bucket APP_S3_REGION=us-east-1 ./scripts/migrate_uploads_to_s3.sh
   ```

   8) Security runtime flags

   - To force cookies to be set as Secure in production, set `APP_FORCE_SECURE_COOKIES=true`.
   - To require HTTPS at the application level, set `APP_ENFORCE_HTTPS=true` (also ensure TLS is terminated at the LB).

   9) AWS Secrets Manager

   - To load secrets from AWS Secrets Manager at startup set `APP_SECRETS_PROVIDER=aws` and `APP_SECRETS_ID` to the secret's name/ARN. The secret should contain a JSON map of properties (e.g. `{ "SPRING_DATASOURCE_PASSWORD": "...", "APP_JWT_SECRET": "..." }`).


   - Actuator endpoints exposed: `health`, `info`, `metrics`, `prometheus`.
