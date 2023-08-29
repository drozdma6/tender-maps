import Navigation from './Navigation';
import Map from "./Map.jsx";
import {Box, CssBaseline} from "@mui/material";
import {useState} from 'react'
import Info from "./Info.jsx";

const API_URL = "http://localhost:8081" + "/api"

function App() {
    const [selectedPage, setSelectedPage] = useState('HEATMAP');

    const handlePageChange = (page) => {
        setSelectedPage(page);
    };

    const renderActiveComponent = () => {
        switch (selectedPage) {
            case 'HEATMAP':
            case 'HEXAGONMAP':
            case 'ICONMAP':
                return <Map activeMap={selectedPage} apiBaseUrl={API_URL}/>;
            case 'INFO':
                return <Info apiBaseUrl={API_URL}/>
            default:
                return null;
        }
    };

    return (
        <Box sx={{display: 'flex', flexDirection: 'column'}}>
            <CssBaseline/>
            <Navigation onPageChange={handlePageChange}/>
            <Box component='main'>
                {renderActiveComponent()}
            </Box>
        </Box>
    );
}

export default App;