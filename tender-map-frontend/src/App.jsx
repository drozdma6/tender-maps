import Navigation from './Navigation';
import Map from "./Map.jsx";
import {Box, CssBaseline} from "@mui/material";
import {useState} from 'react'
import AboutProject from "./AboutProject.jsx";


function App() {
    const [activeNavComponent, setActiveNavComponent] = useState('heatMap');

    const renderActiveComponent = () => {
        switch (activeNavComponent) {
            case 'heatMap':
            case 'hexagonMap':
            case 'iconMap':
                return <Map activeMap={activeNavComponent}/>;
            case 'aboutProject':
                return <AboutProject/>
            default:
                return null;
        }
    };

    return (
        <Box sx={{display: 'flex', flexDirection: 'column'}}>
            <CssBaseline/>
            <Navigation setActiveMap={setActiveNavComponent}/>
            <Box component='main'>
                {renderActiveComponent()}
            </Box>
        </Box>
    );
}

export default App;