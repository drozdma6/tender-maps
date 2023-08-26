import HeatMap from './HeatMap.jsx';
import IconMap from './IconMap.jsx';
import HexagonMap from './HexagonMap.jsx';
import {useState} from 'react'
import FilterSideBar from "./FilterSideBar.jsx";
import IconButton from "@mui/material/IconButton";
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import {useMediaQuery, useTheme} from "@mui/material";
import Footer from "./Footer.jsx";

function Map({activeMap}) {
    const [filterLocations, setFilterLocations] = useState([]);
    const [filterAuthorities, setFilterAuthorities] = useState(new Set());
    const [showFilterMenu, setShowFilterMenu] = useState(false);

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const renderActiveMap = () => {
        switch (activeMap) {
            case 'HEATMAP':
                return <HeatMap buildDataUrl={buildDataUrl}/>;
            case 'HEXAGONMAP':
                return <HexagonMap buildDataUrl={buildDataUrl}/>;
            case 'ICONMAP':
                return <IconMap buildDataUrl={buildDataUrl}/>;
            default:
                return null;
        }
    };
    
    const handleFilterMenuIconClick = () => {
        setShowFilterMenu(!showFilterMenu);
    }

    function buildDataUrl(url) {
        const placesOfPerformanceParam = filterLocations.join(',');
        const filterAuthoritiesIDsParam = [...filterAuthorities].map((authority) => authority.id).join(',');

        const params = new URLSearchParams({
            placesOfPerformance: placesOfPerformanceParam,
            contractorAuthorityIds: filterAuthoritiesIDsParam
        });

        return `${url}?${params.toString()}`;
    }

    return (
        <div>
            {renderActiveMap()}
            <FilterSideBar opened={showFilterMenu}
                           filterLocations={filterLocations}
                           setFilterLocations={setFilterLocations}
                           filterAuthorities={filterAuthorities}
                           setFilterAuthorities={setFilterAuthorities}
                           setShowFilterMenu={setShowFilterMenu}/>
            <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleFilterMenuIconClick}
                        edge="start"
                        style={{
                            marginLeft: 10,
                            marginTop: 'var(--app-bar-height)', //height of appbar
                            backgroundColor: 'white',
                            borderRadius: '50%',
                            padding: '13px',
                            boxShadow: '0px 8px 12px rgba(0, 0, 0, 0.3)',
                            position: 'fixed',
                            bottom: isMobile ? 10 : 'auto', // Only apply "bottom" when isMobile is true
                            top: isMobile ? 'auto' : 10,     // Only apply "top" when isMobile is false
                        }}
                        className="filter-button"
                    >
                        <FilterAltIcon/>
            </IconButton>
            <Footer/>
        </div>
    );
}

export default Map;