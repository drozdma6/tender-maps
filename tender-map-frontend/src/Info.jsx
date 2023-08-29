import {Paper} from "@mui/material";
import ReactMarkdown from "react-markdown";


function Info({apiBaseUrl}) {
    const apiDocs = apiBaseUrl + "-docs";

    const markdownContent = `
# Tender Maps

This project was developed as a bachelor's thesis for [CTU FIT](https://fit.cvut.cz/en) in collaboration with [OpenDataLab](https://opendatalab.cz/).
The primary objective was to create an application capable of web scraping, storing, providing, and visualizing data related to public procurements in the Czech Republic. 

## Data Information
The tender data for this research was gathered from the Czech Republic's public procurement portal, [nen.nipez.cz](https://nen.nipez.cz/en/). The project included the development of a REST API to seamlessly integrate the dataset into various applications and projects. A publicly accessible
 [API Swagger documentation](${apiDocs}) is provided in order to simplify integration for other developers.

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
as well as view all offers submitted by each company 

### Hexagon Map
The hexagon map serves as a visual representation of the geographical distribution of suppliers involved in public procurement.
 By mapping company headquarters' addresses, and using color and elevation to reflect the total contract prices of won procurements.
Unlike the heat map, the hexagon map has the advantage of displaying more information simultaneously. Each hexagon's elevation can convey 
one set of information, while its color may represent another. Furthermore, users have the option to click on individual hexagons
  to access detailed data about the companies located within that specific area (hexagon).

## Contact Information
  - Author Name: Marek Drozdik
  - Author Email: [drozdma6@cvut.cz](mailto:drozdma6@cvut.cz)
  - [Github](https://github.com/opendatalabcz/analyzer_public_contracts)
`;


    return (
        <Paper style={{
            paddingBottom: "var(--app-bar-height)",
            maxHeight: "100vh",
            overflow: 'auto',
            paddingLeft: 30,
            paddingRight: 30
        }}>
            <ReactMarkdown>{markdownContent}</ReactMarkdown>
        </Paper>
    );
}

export default Info;
