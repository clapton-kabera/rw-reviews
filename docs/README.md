# Rwanda Reviews

A Yelp-style backend for discovering and reviewing businesses in Rwanda. Businesses register with their service type, location and services (with RWF price ranges), and anyone can read and write reviews.

Built as a small agile project: the product is delivered in sprints, and each sprint has a review and a retrospective document.

## Repository layout

```
.
├── .github/workflows/ci.yml    # CI: build, test and smoke-test on every push / PR
├── docs/                       # Agile documentation (sprint reviews and retrospectives)
├── rw_reviews/                 # Spring Boot application (all the code)
└── README.md
```

## Tech stack

| Concern | Choice |
|---------|--------|
| Language / framework | Java 21, Spring Boot 4.1 |
| Persistence | Spring Data JPA, H2 in-memory database |
| Validation | Jakarta Bean Validation |
| Monitoring | Spring Boot Actuator |
| Tests | JUnit 5, MockMvc |
| CI | GitHub Actions |

There is **no authentication**: anyone can register a business and submit reviews. The database is in memory, so all data is wiped on restart and re-seeded (see [Seed data](#seed-data)).

## Getting started

Requirements: JDK 21 and Maven 3.9+.

```bash
cd rw_review
mvn spring-boot:run        # starts on http://localhost:8080
mvn verify                 # compile + run all tests + package
```

Useful URLs while the app is running:

| URL | What it is |
|-----|------------|
| http://localhost:8080/actuator/health | Health check |
| http://localhost:8080/h2-console | Database browser (JDBC URL `jdbc:h2:mem:reviewsdb`, user `sa`, empty password) |

## API reference

Base path: `/api/businesses`. All bodies are JSON. Prices are whole Rwandan francs (RWF).

### Businesses

#### `POST /api/businesses`: register a business
Creates a business together with the services it offers. Returns `201 Created` with a `Location` header.

```json
{
  "name": "Mama Rwanda Kitchen",
  "serviceType": "RESTAURANT",
  "description": "Traditional Rwandan food",
  "district": "Gasabo",
  "address": "KG 11 Ave, Kimironko",
  "phone": "+250788000000",
  "services": [
    { "name": "Lunch buffet", "minPrice": 5000, "maxPrice": 9000 }
  ]
}
```

Rules: `name`, `serviceType` and at least one service are required; every service needs a name and a price range where `minPrice <= maxPrice`.
Service types: `RESTAURANT, CAFE, BAR, HOTEL, GUESTHOUSE, SALON, SPA, GARAGE, TOUR_OPERATOR, SHOPPING, HEALTH_CLINIC, OTHER`.

#### `GET /api/businesses`: browse, search and filter (paginated)
Every parameter is optional and filters can be combined freely.

| Parameter | Description |
|-----------|-------------|
| `serviceType` | Only this category, e.g. `CAFE` |
| `district` | Location filter, case-insensitive exact match, e.g. `Gasabo` |
| `minRating` | Only businesses whose average rating is at least this value (1 to 5). Businesses with no reviews are excluded when this is used |
| `q` | Case-insensitive search in the business name |
| `page` | Zero-based page number (default `0`) |
| `size` | Page size (default `10`, maximum `50`) |

Example: `GET /api/businesses?district=Gasabo&serviceType=RESTAURANT&minRating=4&page=0&size=10`

Results are sorted alphabetically by name. Each business includes `averageRating` and `reviewCount`.

#### `GET /api/businesses/{id}`: one business
Returns the business with its services, average rating (one decimal) and review count. `404` if it does not exist.

### Reviews

Reviews belong to a business, so they live under it.

#### `POST /api/businesses/{id}/reviews`: submit a review
No login needed. Returns `201 Created` with the saved review.

```json
{ "reviewerName": "Aline U.", "rating": 5, "comment": "Great food and friendly staff!" }
```

Rules: `reviewerName` required (max 80 chars), `rating` required and between 1 and 5, `comment` optional (max 2000 chars). `404` if the business does not exist.

#### `GET /api/businesses/{id}/reviews`: read reviews (paginated)

| Parameter | Description |
|-----------|-------------|
| `page` | Zero-based page number (default `0`) |
| `size` | Page size (default `10`, maximum `50`) |
| `sort` | `newest` (default), `highest` or `lowest` |

Example response:

```json
{
  "content": [
    { "id": 17, "businessId": 1, "reviewerName": "Eric M.", "rating": 4,
      "comment": "Great value for the price.", "createdAt": "2026-01-15T10:22:31Z" }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 120,
  "totalPages": 12,
  "last": false
}
```

A page past the end returns an empty `content` array (not an error). An unknown business returns `404`.

### Monitoring

| Endpoint | Purpose |
|----------|---------|
| `GET /actuator/health` | Overall status plus database, disk and data checks (`UP` / `DOWN`) |
| `GET /actuator/metrics` | Lists available metrics |
| `GET /actuator/metrics/http.server.requests` | Request counts and response times |

### Error format

Errors use the standard problem-details shape. Validation errors list the problem per field:

```json
{
  "status": 400,
  "detail": "Validation failed",
  "errors": { "rating": "rating must be at most 5" }
}
```

| Status | When |
|--------|------|
| `400` | Validation failed, bad parameter value (unknown `sort`, `minRating` outside 1 to 5, malformed JSON) |
| `404` | Business not found |
| `500` | Unexpected error (details are logged on the server, never sent to the client) |

## Quick try with curl

```bash
# Register a business
curl -X POST localhost:8080/api/businesses -H 'Content-Type: application/json' -d '{
  "name": "Mama Rwanda Kitchen", "serviceType": "RESTAURANT", "district": "Gasabo",
  "services": [{"name": "Lunch", "minPrice": 3000, "maxPrice": 7000}]
}'

# Review it (use the id returned above)
curl -X POST localhost:8080/api/businesses/1/reviews -H 'Content-Type: application/json' \
  -d '{"reviewerName": "Aline U.", "rating": 5, "comment": "Great food!"}'

# Read reviews, second page of 5, highest rated first
curl "localhost:8080/api/businesses/1/reviews?page=1&size=5&sort=highest"

# Find well-rated restaurants in Gasabo
curl "localhost:8080/api/businesses?district=Gasabo&serviceType=RESTAURANT&minRating=4"
```

## Seed data

On every startup `DataSeeder` creates 9 fictional businesses across several districts and categories, with roughly 400 reviews (the first business has 120, so pagination has many pages to walk through). A fixed random seed makes the data identical on every run, which keeps manual and automated testing predictable.

Disable it with `app.seed.enabled=false` in `application.properties`. Tests turn it off automatically through the `test` profile and create their own data.

## Logging and monitoring

- Every API request produces one console line: `METHOD /path?query -> status (N ms)`.
- Each request gets a short id, shown in the log lines and returned in the `X-Request-Id` response header, so a problem report can be matched to its logs.
- Created reviews are logged with ids and ratings only. Review text and reviewer names are deliberately not logged.
- Log levels are configured in `application.properties` (`logging.level.com.rwandareviews=DEBUG` for more detail).

## Testing

```bash
cd rw_review
mvn test
```

The tests run the full stack (HTTP, controller, service, H2) and cover business registration and validation, review submission, pagination and sorting, rating calculation, filters, seeding and the health endpoint.

## Continuous integration

`.github/workflows/ci.yml` runs on every push and pull request. It builds the project, runs all tests, then starts the packaged jar and calls the APIs to confirm it boots with data. A failing step marks the commit with a red X.

Because the code lives in `rw_reviews/`, the workflow runs its Maven and jar steps from that folder (`working-directory: rw_reviews`).

## Project documentation

Sprint documentation lives in [`docs/`](docs/):

| Sprint | Review | Retrospective |
|--------|--------|---------------|
| 1: core reviews | [Sprint 1 review](docs/sprint1-review.md) | [Sprint 1 retrospective](docs/sprint1-retrospective.md) |
| 2: logging and monitoring | [Sprint 2 review](docs/sprint2-review.md) | [Sprint 2 retrospective](docs/sprint2-retrospective.md) |

## Backlog / ideas for later sprints

- Edit and delete reviews, which needs some form of identity
- Sort businesses by rating or review count
- Filter by price range or by several districts at once
- Photos on reviews
- Persistent database (PostgreSQL) instead of in-memory H2
- Authentication and rate limiting
