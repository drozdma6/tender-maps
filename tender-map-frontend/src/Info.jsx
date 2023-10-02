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
This map provides a visual representation of the distribution of suppliers participating in public tenders across the Czech Republic. 
Utilising the addresses of company headquarters for geolocation and assigning colours based on the cumulative contract prices won, the heat map
 offers an intuitive way of identifying significant areas of economic activity in public tenders in the Czech Republic.

### <div id='iconMap'>Icon Map</div>
The Icon Map displays companies that have participated in public tenders. Suppliers are represented by orange icons and
 non-suppliers (companies that participated but did not win) are represented by blue icons. Users can click on these 
 icons to access lists of both won and supplied procurements and to view all bids submitted by each company. Furthermore, 
 users can click on the company name to access the government's official public register of companies, where they can view 
 even more information.
### <div id='hexagonMap'>Hexagon Map</div>
The hexagon map functions as a clear illustration of the geographical spread of suppliers participating in public tenders.
This is accomplished by mapping the locations of company headquarters and utilising colour and elevation to indicate the total
 value of contracts won. Unlike the heat map, the hexagon map offers the advantage of displaying more information concurrently.
   The elevation may indicate one set of data, while the colour may indicate another. Additionally, users can access detailed information about the
   businesses situated in a specific hexagon by clicking on the corresponding hexagon.
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