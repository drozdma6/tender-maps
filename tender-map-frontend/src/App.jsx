import { useState } from 'react';
import Navigation from './Navigation';
import HeatMap from './HeatMap';
import IconMap from "./IconMap.jsx";
import HexagonMap from "./HexagonMap.jsx";
import SideBar from "./SideBar.jsx";
import {Box, CssBaseline} from "@mui/material";

function App() {
    const [showHeatMap, setShowHeatMap] = useState(true);
    const [showIconMap, setShowIconMap] = useState(false);
    const [showHexagonMap, setShowHexagonMap] = useState(false);
    const [showSideMenu, setShowSideMenu] = useState(false);

    const handleHeatMapClick = () => {
        setShowHeatMap(true);
        setShowIconMap(false);
        setShowHexagonMap(false);
    };

    const handleIconMapClick = () => {
        setShowHeatMap(false);
        setShowIconMap(true);
        setShowHexagonMap(false);
    };

    const handleHexagonMapClick = () => {
        setShowHeatMap(false);
        setShowIconMap(false);
        setShowHexagonMap(true);
    };

    const handleSideMenuIconClick = () => {
        setShowSideMenu(!showSideMenu);
    }

    return (
        <Box sx={{ display: 'flex' }}>
            <CssBaseline />
            <Navigation
                onHeatMapClick={handleHeatMapClick}
                onIconMapClick={handleIconMapClick}
                onHexagonMapClick={handleHexagonMapClick}
                onSideMenuClick={handleSideMenuIconClick}
            />
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    p: 3,
                    marginTop: '64px', // Assuming the AppBar has a height of 64px
                }}
            >
                {showHeatMap && <HeatMap />}
                {showIconMap && <IconMap />}
                {showHexagonMap && <HexagonMap />}
            </Box>
            <SideBar opened={showSideMenu} />
        </Box>
    );
}

export default App;