# DeskFlow

Hot desk booking API for the Finnish office mini-project.

## Run in GitHub Codespaces

1. Open this repo in a Codespace (Dev Container includes Docker).
2. Start the stack:

```bash
docker compose up -d --build
```

3. In the **Ports** panel, set port `8080` to **Public**.
4. Share the forwarded URL, for example:

`https://<codespace-name>-8080.app.github.dev/`

## Run locally (Docker)

```bash
docker compose up -d --build
```

- UI / API: http://localhost:8080
- Health: http://localhost:8080/api/health

```bash
docker compose logs -f api
docker compose down
```

## Demo accounts

- Employees: Joey/1, Ethan/2, Alfie/3, Julian/4, Justin/5
- Admin: admin/99

## Main endpoints

- `GET /api/health`
- `GET /api/desks?floor=&hasMonitor=`
- `GET /api/desks/available?date=YYYY-MM-DD&floor=` — all active desks for the date, each with `available` true/false
- `POST /api/login`
- `POST /api/bookings`
- `GET /api/bookings?date=YYYY-MM-DD`
- `GET /api/bookings?employeeId=`
- `DELETE /api/bookings/{id}?employeeId=`
