# Scrapper

Application for scrapping [nen.nipez.cz](https://nen.nipez.cz/en/profily-zadavatelu-platne), with focus on
public procurements, their suppliers and participants. Application also geocodes addresses of registered companies and contracting authorities.

## Requirements

1. create api tokens:
    1. [profinit geocoding](https://geolokator.profinit.cz/) used for Czech places
    2. [google maps](https://developers.google.com/maps) for foreign places (optional)
2. provide them in *.env* in format `PROFINIT_API_KEY={api_key}` and `GOOGLE_API_KEY={api_key}`

## Local run

1. Set up postgres database and configure application.properties accordingly
    - Set desired modifiers
2. Provide required api tokens for geocoding
3. `./gradlew build` build
4. `./gradlew bootRun` run

## Run tests

- `./gradlew test`

## Modifiers
- `SCHEDULING_CRON={cron_expression}`
  - Scheduled run interval
    as [cron expression](https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm)
  - Default value: `0 0 0 1 * *` (first day of each month at midnight)

- `RUN_ON_STARTUP={boolean}`
  - Determines whether scrapping should start with application run or wait for scheduled run
  - Default value: true
   
- `CORE_POOL_SIZE={integer}`
  - Number of allocated threads
  - Should be somewhere between 1 - 10
  - Default value: 5