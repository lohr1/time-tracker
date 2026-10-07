# Time Tracker

Kleine Quarkus-Anwendung für das technische Interview bei ti&m: ein Entwickler bucht Arbeitszeit auf Projekte.

## Starten

Voraussetzungen: Java 21, Maven 3.9, Podman (oder Docker) mit Compose.

```bash
podman compose up -d        # PostgreSQL 16 auf localhost:5433
mvn quarkus:dev             # Anwendung auf http://localhost:8080
```

Tests (`mvn test`) brauchen keine laufende Datenbank: Quarkus Dev Services startet über Testcontainers einen eigenen
Postgres-Container. Bei Podman muss Testcontainers den Socket kennen, z. B. in `~/.testcontainers.properties`:

```
docker.host=unix:///run/user/1000/podman/podman.sock
ryuk.disabled=true
```

## Umgesetzte Use Cases

| Use Case              | Aufruf                                        |
|-----------------------|-----------------------------------------------|
| Zeiteintrag erfassen  | `POST /time-entries`                          |
| Einträge eines Tages  | `GET /time-entries?date=2026-10-07`           |
| Summe eines Tages     | `GET /time-entries/summary?date=2026-10-07`   |

```bash
curl -X POST localhost:8080/time-entries -H 'Content-Type: application/json' \
  -d '{"project":"Kunde A","startTime":"2026-10-07T09:00","endTime":"2026-10-07T10:30","description":"Code Review"}'
```

Fachliche Regeln: Ende nach Start, Start und Ende am selben Tag, keine Überlappung mit bestehenden Einträgen.
Verstöße liefern `400 {"error": "..."}`. Die Summe wird gesamt und pro Projekt in Minuten ausgegeben.

## Technische Entscheidungen

- Drei Schichten in einem Package: `TimeEntryResource` (HTTP) → `TimeEntryService` (Regeln) → `TimeEntryRepository` (DB).
  Keine Fachlogik in der Resource.
- Hibernate ORM mit Panache im Repository-Pattern. Die Entity bleibt plain JPA, damit sie keine Persistenz-API erbt.
- Schema per Flyway-Migration (`V1__create_time_entry.sql`), Hibernate erzeugt kein DDL.
- Records als Request- und Response-DTOs; die Entity wird nie direkt serialisiert. Dauer wird berechnet, nicht gespeichert.
- Strukturelle Validierung (Bean Validation) am Request, fachliche Regeln im Service, Übersetzung in 400 per ExceptionMapper.
- Tests gegen eine echte Datenbank statt Mocks: Service-Tests für die Regeln, REST-Tests für die Endpunkte.

## Bewusst offen oder vereinfacht

- Projekt ist ein Textfeld, keine eigene Entität.
- Zeiten sind `LocalDateTime` ohne Zeitzone. Ein Benutzer, kein Login.
- Kein Ändern oder Löschen von Einträgen, keine Paginierung.
- Bean-Validation-Fehler und fachliche Fehler haben unterschiedliche JSON-Formate.
- Der `date`-Parameter wird von Hand geparst, weil JAX-RS eine fehlgeschlagene Parameter-Konvertierung mit 404 beantwortet.
- Die Überlappungsprüfung läuft im Service (lesen, dann schreiben). Bei zwei gleichzeitigen Requests ist sie nicht
  garantiert; ein Datenbank-Constraint (`EXCLUDE USING gist`) wäre der nächste Schritt.

## Einsatz von KI

Claude Code hat den Code nach einem gemeinsamen Design-Gespräch generiert (Datenmodell, fachliche Regeln, Schichten,
Stack und Tests wurden vorher festgelegt). Die Entscheidungen und Alternativen habe ich im Gespräch getroffen und
anschließend nachvollzogen.
