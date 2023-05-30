# Scrapper

Application for scrapping [nen.nipez.cz](https://nen.nipez.cz/en/profily-zadavatelu-platne), with focus on
public procurements and their suppliers and participants. Application also geocodes addresses of registered companies..

## Requirements

1. create api tokens:
    1. [profinit geocoding](https://geolokator.profinit.cz/) used for Czech places
    2. [google maps](https://developers.google.com/maps) for foreign places (optional)
2. provide them in *.env* in format `PROFINIT_API_KEY={api_key}` and `GOOGLE_API_KEY={api_key}`
3. continue with [docker run](#docker-compose) or [local run](#local-run)

## Docker-compose

- run `docker-compose up` with optional [modifiers](#modifiers)

## Local run

1. Set up postgres database and configure application.properties accordingly
2. `./gradlew build` build
3. `./gradlew bootRun` run

## Run tests

- `./gradlew test`

## Modifiers

- Scheduled run interval
  as [cron expression](https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm)
    - Docker-compose `SCHEDULING_CRON={cron_expression}` (Cron)
    - Default value: 0 0 0 1 * * (first day of each month at midnight)

- Determines whether scrapping should start with application run or wait for scheduled run
    - Docker-compose `RUN_ON_STARTUP={boolean}`
    - Default value: false