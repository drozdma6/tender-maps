import Navigation from './Navigation';
import HeatMap from './HeatMap';
import IconMap from "./IconMap.jsx";
import HexagonMap from "./HexagonMap.jsx";
import SideBar from "./SideBar.jsx";
import {Box, CssBaseline} from "@mui/material";
import {useState} from 'react'


function App() {
    const [filterLocations, setFilterLocations] = useState([]);
    const [filterAuthorities, setFilterAuthorities] = useState(new Set());

    const [showSideMenu, setShowSideMenu] = useState(false);
    const [activeMap, setActiveMap] = useState('heatMap');

    const renderActiveComponent = () => {
        switch (activeMap) {
            case 'heatMap':
                return <HeatMap buildDataUrl={buildDataUrl}/>;
            case 'hexagonMap':
                return <HexagonMap buildDataUrl={buildDataUrl}/>;
            case 'iconMap':
                return <IconMap buildDataUrl={buildDataUrl}/>;
            default:
                return null;
        }
    };

    function buildDataUrl(url) {
        const placesOfPerformanceParam = filterLocations.join(',');
        const filterAuthoritiesIDsParam = [...filterAuthorities].map((authority) => authority.id).join(',');

        const params = new URLSearchParams({
            placesOfPerformance: placesOfPerformanceParam,
            contractorAuthorityIds: filterAuthoritiesIDsParam
        });

        return `${url}?${params.toString()}`;
    }

    const handleSideMenuIconClick = () => {
        setShowSideMenu(!showSideMenu);
    }

    return (
        <Box sx={{display: 'flex', flexDirection: 'column'}}>
            <CssBaseline/>
            <Navigation setActiveMap={setActiveMap} onSideMenuClick={handleSideMenuIconClick}/>
            <Box component="main">
                <SideBar opened={showSideMenu}
                         filterLocations={filterLocations}
                         setFilterLocations={setFilterLocations}
                         filterAuthorities={filterAuthorities}
                         setFilterAuthorities={setFilterAuthorities}/>
                {renderActiveComponent()}
            </Box>
        </Box>
    );
}

export default App;