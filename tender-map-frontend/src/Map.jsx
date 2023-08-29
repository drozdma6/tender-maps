import HeatMap from './HeatMap.jsx';
import IconMap from './IconMap.jsx';
import HexagonMap from './HexagonMap.jsx';
import {useState} from 'react'
import FilterSideBar from "./FilterSideBar.jsx";
import IconButton from "@mui/material/IconButton";
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import {useMediaQuery, useTheme} from "@mui/material";
import Footer from "./Footer.jsx";
import {toast, ToastContainer} from "react-toastify";
import axios from "axios";
import 'react-toastify/dist/ReactToastify.css';


function Map({activeMap, apiBaseUrl}) {
    const [filterLocations, setFilterLocations] = useState([]);
    const [filterAuthorities, setFilterAuthorities] = useState(new Set());
    const [showFilterMenu, setShowFilterMenu] = useState(false);

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const renderActiveMap = () => {
        const props = {
            fetchData: fetchData,
            addFiltersToPath: addFiltersToPath,
            filterLocations: filterLocations,
            filterAuthorities: filterAuthorities
        };
        switch (activeMap) {
            case 'HEATMAP':
                return <HeatMap {...props}/>;
            case 'HEXAGONMAP':
                return <HexagonMap {...props}/>;
            case 'ICONMAP':
                return <IconMap {...props}/>;
            default:
                return null;
        }
    };

    const handleFilterMenuIconClick = () => {
        setShowFilterMenu(!showFilterMenu);
    }

    async function fetchData(path, setFetchedData) {
        try {
            const url = apiBaseUrl + path;
            const response = await axios.get(url);
            setFetchedData(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
            toast.error("Failed to fetch data.", {
                position: "top-center",
                autoClose: 1500,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: false,
                draggable: true,
                progress: undefined,
                theme: "light",
            });
        }
    }

    function addFiltersToPath(path, additionalParams) {
        const placesOfPerformanceParam = filterLocations.join(',');
        const filterAuthoritiesIDsParam = [...filterAuthorities].map((authority) => authority.id).join(',');

        const params = new URLSearchParams({
            ...additionalParams,
            placesOfPerformance: placesOfPerformanceParam,
            contractorAuthorityIds: filterAuthoritiesIDsParam
        });
        return `${path}?${params.toString()}`;
    }

    return (
        <div>
            {renderActiveMap()}
            <Footer/>
            <FilterSideBar
                apiBaseUrl={apiBaseUrl}
                opened={showFilterMenu}
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
            <ToastContainer
                position="top-center"
                autoClose={1500}
                limit={2}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss={false}
                draggable
                pauseOnHover={false}
                theme="light"
            />
        </div>
    );
}

export default Map;