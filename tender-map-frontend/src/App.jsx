import Navigation from './Navigation';
import HeatMap from './HeatMap';
import IconMap from "./IconMap.jsx";
import HexagonMap from "./HexagonMap.jsx";
import SideBar from "./SideBar.jsx";
import {Box, CssBaseline} from "@mui/material";
import axios from "axios";
import {useState, useEffect} from 'react'

const DATA_URL =
    'http://localhost:8081/procurements/exact-address-supplier';

function App() {
    const [data, setData] = useState();
    const [filterLocations, setFilterLocations] = useState([]);
    const [filterAuthorities, setFilterAuthorities] = useState(new Set());

    const [showHeatMap, setShowHeatMap] = useState(true);
    const [showIconMap, setShowIconMap] = useState(false);
    const [showHexagonMap, setShowHexagonMap] = useState(false);
    const [showSideMenu, setShowSideMenu] = useState(false);

    useEffect(() => {
        console.log('Log pri inite appky');
        fetchData();
    }, []);

    async function fetchData() {
        const placesOfPerformanceParam = filterLocations.join(',');
        //make array from set, transform into array of ids and join into string
        const filterAuthoritiesIDsParam = [...filterAuthorities].map((authority) => authority.id).join(',');
        const response = await axios.get(DATA_URL, {
            params: {
                placesOfPerformance: placesOfPerformanceParam,
                contractorAuthorityIds: filterAuthoritiesIDsParam
            }
        });
        setData(response.data);
    }

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

    const handleFilterButtonClick = () => {
        fetchData()
        console.log('Checked Items:', filterLocations);
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
                    marginTop: '64px', // Assuming the AppBar has a height of 64px
                }}
            >
                {showHeatMap && <HeatMap data={data}/>}
                {showIconMap && <IconMap/>}
                {showHexagonMap && <HexagonMap data={data}/>}
            </Box>
            <SideBar opened={showSideMenu}
                     filterLocations={filterLocations}
                     setFilterLocations={setFilterLocations}
                     onFilterButtonClick={handleFilterButtonClick}
                     filterAuthorities={filterAuthorities}
                     setFilterAuthorities={setFilterAuthorities}/>
        </Box>
    );
}

export default App;