import Navigation from './Navigation';
import Map from "./Map.jsx";
import {Box, CssBaseline} from "@mui/material";
import {useState} from 'react'
import Info from "./Info.jsx";


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
                return <Map activeMap={selectedPage}/>;
            case 'INFO':
                return <Info/>
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