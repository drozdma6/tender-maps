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

- `CORE_POOL_SIZE={integer}`
  - Number of allocated threads
  - Should be somewhere between 1 - 10
  - Default value: 5