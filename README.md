# Public procurements analyzer

This project was developed as a bachelor's thesis for [CTU FIT](https://fit.cvut.cz/en) in collaboration with [OpenDataLab](https://opendatalab.cz/).
The primary objective was to create an application capable of web scraping, storing, providing, and visualizing data related to public procurements in the Czech Republic.

## Set up
1. Download project
2. `cd analyzer_public_contracts`
3. Set environment properties:
 - `cp .env.example .env`
 - Edit *.env* 
```
PROFINIT_API_KEY=profinit_api_key
GOOGLE_API_KEY=google_api_key
DB_PASSWORD=password
VITE_API_URL=api_url
FIXER_API_KEY=fixer_api_key
```
 - Optionally add GOOGLE_API_KEY, for geocoding foreign countries (do not include it in *.env* if you do not have it)
 - Optionally change [scrapper modifiers](#scrapper-modifiers) in *docker-compose.yml*.
4. `$ docker-compose up`
5. Frontend is running on `http://localhost:3001/`

### Scrapper Modifiers
- `CORE_POOL_SIZE={integer}`
    - Number of allocated threads
    - Should be somewhere between 1 - 10
    - Default value: 5


## Data Information
The tender data for this research was gathered from the Czech Republic's public procurement portal, [nen.nipez.cz](https://nen.nipez.cz/en/). The project included the development of a REST API to seamlessly integrate the dataset into various applications and projects. A publicly accessible
API Swagger documentation (VITE_API_URL + /api-docs) is provided in order to simplify integration for other developers.

## Maps Information
Applying filters based on the place where procurements are executed and the organization (contracting authority) responsible for initiating a particular procurement is possible for every map type.

### Heat Map
This map visually illustrates the distribution of suppliers involved in public procurements across different locations. By utilizing the
addresses of companies for geolocation and assigning colors based on the cumulative contract prices of won procurements,
the heat map provides an intuitive insight into identifying significant areas of economic engagement within the realm of public procurement
in the Czech Republic.

### Icon Map
The icon map is designed to showcase companies participating in public procurement. Suppliers are marked with orange icons, while
non-suppliers are indicated by blue icons. Users can interact with these icons to access lists of both won and supplied procurements,
as well as view all offers submitted by each company.

### Hexagon Map
The hexagon map serves as a visual representation of the geographical distribution of suppliers involved in public procurement.
By mapping company headquarters' addresses, and using color and elevation to reflect the total contract prices of won procurements.
Unlike the heat map, the hexagon map has the advantage of displaying more information simultaneously. Each hexagon's elevation can convey
one set of information, while its color may represent another. Furthermore, users have the option to click on individual hexagons
to access detailed data about the companies located within that specific area (hexagon).

## Contact Information
- Author Name: Marek Drozdik
- [Github](https://github.com/opendatalabcz/analyzer_public_contracts)
- [LinkedIn](https://www.linkedin.com/in/marek-drozd%C3%ADk-07828b218)