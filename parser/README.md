# Scrapper

Application for scrapping [nen.nipez.cz](https://nen.nipez.cz/en/profily-zadavatelu-platne). Scrapping is focused on
public
procurements and their suppliers (winners) or participants.

## Requirements

1. create your own [google maps](https://developers.google.com/maps) api key to enable geocoding functionality
2. provide it in *.env* file in format `apiKey={your_google_api_key}`
3. continue with [docker run](#docker-compose) or [local run](#local-run)

## Docker-compose

- run `docker-compose up` with optional [modifiers](#modifiers)

## Local run

1. Set up postgres database and configure application.properties
2. `./gradlew build` build
3. `./gradlew bootRun` run

## Run tests

- `./gradlew test`

## Modifiers

- All modifiers are optional
- Change the starting date from which public orders are to be scrapped
    - Docker-compose `PROCUREMENT_FIRST_DATE_OF_PUBLICATION=YYYY-MM-DD`
    - Command line argument (running locally) `--procurement.first.date.of.publication=YYYY-MM-DD`
    - Default value: 2022-01-01

- Change the number of fetched pages of procurements per one fetched html
    - Docker-compose `PROCUREMENT_PAGES_PER_FETCH={positive_integer}`
    - Command line argument (running locally) `--procurement.pages.per.fetch={positive_integer}`
    - Default value: 5

- Determines whether fetching should stop at first already stored procurement or not (from single contractor)
    - Docker-compose `PROCUREMENT_SCRAPE_ALL={boolean}`
    - Command line argument (running locally)`--procurement.scrape.all={boolean}`
    - Default value: false