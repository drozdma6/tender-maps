import {Paper} from "@mui/material";
import ReactMarkdown from "react-markdown";
import rehypeRaw from "rehype-raw";

const renderInfo = (apiDocs) =>  `
# Tender Maps

This project was developed as a bachelor's thesis for [CTU FIT](https://fit.cvut.cz/en) in collaboration with [OpenDataLab](https://opendatalab.cz/).
The primary objective was to create an application capable of web scraping, storing, providing, and visualizing data related to public procurements in the Czech Republic. 

## Data Information
The tender data for this research was gathered from the Czech Republic's public procurement portal, [nen.nipez.cz](https://nen.nipez.cz/en/). The project included the development of a REST API to seamlessly integrate the dataset into various applications and projects. A publicly accessible
 [API Swagger documentation](${apiDocs}) is provided in order to simplify integration for other developers.

## Maps Information
Applying filters based on the place where procurements are executed and the organization (contracting authority) responsible for initiating a particular procurement is possible for every map type.

### <div id='heatMap'>Heat Map</div>
This map visually illustrates the distribution of suppliers involved in public procurements across different locations. By utilizing the
 addresses of companies for geolocation and assigning colors based on the cumulative contract prices of won procurements, 
 the heat map provides an intuitive insight into identifying significant areas of economic engagement within the realm of public procurement 
 in the Czech Republic.

### <div id='iconMap'>Icon Map</div>
The icon map is designed to showcase companies participating in public procurement. Suppliers are marked with orange icons, while 
non-suppliers are indicated by blue icons. Users can interact with these icons to access lists of both won and supplied procurements, 
as well as view all offers submitted by each company. 

### <div id='hexagonMap'>Hexagon Map</div>
The hexagon map serves as a visual representation of the geographical distribution of suppliers involved in public procurement.
 By mapping company headquarters' addresses, and using color and elevation to reflect the total contract prices of won procurements.
Unlike the heat map, the hexagon map has the advantage of displaying more information simultaneously. Each hexagon's elevation can convey 
one set of information, while its color may represent another. Furthermore, users have the option to click on individual hexagons
  to access detailed data about the companies located within that specific area (hexagon).

## Project information
   -  Author: Marek Drozdik <a href="https://www.linkedin.com/in/marek-drozd%C3%ADk-07828b218" target="_blank" rel="noopener noreferrer">
                <img src="/data/LinkedIn-icon.png" alt="LinkedIn" style="width: 62px; margin-left: 3px;"/>
              </a>
   -  Repository: <a href="https://github.com/opendatalabcz/analyzer_public_contracts" target="_blank" rel="noopener noreferrer">
                <img src="/data/github-logo-text-black.svg" alt="GitHub" style="width: 55px; margin-left: 3px; ...imgStyle"/>
              </a>
`;

function Info({apiBaseUrl}) {
    const apiDocs = apiBaseUrl + "-docs";

    return (
        <Paper style={{
            paddingBottom: "var(--app-bar-height)",
            maxHeight: "100vh",
            overflow: 'auto',
            paddingLeft: 30,
            paddingRight: 30
        }}>
            <div className="custom-markdown">
                <ReactMarkdown rehypePlugins={[rehypeRaw]}>
                    {renderInfo(apiDocs)}
                </ReactMarkdown>
            </div>
        </Paper>
    );
}

export default Info;