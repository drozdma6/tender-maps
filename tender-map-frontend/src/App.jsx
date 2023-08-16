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

    useEffect(() => {
        console.log('Log pri inite appky');
        fetchData();
    }, []);

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
        <Box sx={{display: 'flex'}}>
            <CssBaseline/>
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
                    marginTop: '64px',
                }}
            >
                {showHeatMap && <HeatMap data={data}/>}
                {showIconMap && <IconMap/>}
                {showHexagonMap && <HexagonMap data={data}/>}
            </Box>
            <SideBar opened={showSideMenu}
                     filterLocations={filterLocations}
                     setFilterLocations={setFilterLocations}
                     filterAuthorities={filterAuthorities}
                     setFilterAuthorities={setFilterAuthorities}/>
        </Box>
    );
}

export default App;