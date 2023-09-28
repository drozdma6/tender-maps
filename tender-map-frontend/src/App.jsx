import Navigation from './Navigation';
import Map from "./Map.jsx";
import {Box, CssBaseline, ThemeProvider} from "@mui/material";
import {useEffect, useState} from 'react'
import Info from "./Info.jsx";
import { createTheme } from '@mui/material/styles';

const API_URL = import.meta.env.VITE_API_URL;

function App() {
    // Initialize page from localStorage or default to heatmap
    const [selectedPage, setSelectedPage] = useState(() => {
        const storedPage = localStorage.getItem('page');
        return storedPage !== null ? JSON.parse(storedPage) : 'HEATMAP';
    });
    // Initialize theme from localStorage or default to false
    const [isDarkMode, setIsDarkMode] = useState(() => {
        const storedTheme = localStorage.getItem('darkTheme');
        return storedTheme !== null ? JSON.parse(storedTheme) : false;
    });

    useEffect(() => {
        localStorage.setItem('darkTheme', JSON.stringify(isDarkMode));
    }, [isDarkMode]);

    useEffect(() => {
        localStorage.setItem('page', JSON.stringify(selectedPage));
    }, [selectedPage]);

    const darkTheme = createTheme({
        palette: {
            mode: isDarkMode ? 'dark' : 'light',
            ...(isDarkMode ? {
                background: {
                    default: '#303030',
                    paper: '#303030',
                },
            } : {}),
        },
    });

    const handlePageChange = (page) => {
        setSelectedPage(page);
    };

    const renderActiveComponent = () => {
        switch (selectedPage) {
            case 'HEATMAP':
            case 'HEXAGONMAP':
            case 'ICONMAP':
                return <Map activeMap={selectedPage} apiBaseUrl={API_URL} isDarkMode={isDarkMode}
                            changePageToInfo={() => handlePageChange('INFO')}/>;
            case 'INFO':
                return <Info apiBaseUrl={API_URL} scrollTo={"hexagonMap"}/>
            default:
                return null;
        }
    };

    return (
        <ThemeProvider theme={darkTheme}>
            <CssBaseline/>
            <Box sx={{display: 'flex', flexDirection: 'column'}}>
                <Navigation onPageChange={handlePageChange} themeToggle={isDarkMode} setThemeToggle={setIsDarkMode}/>
                <Box component='main'>
                    {renderActiveComponent()}
                </Box>
            </Box>
        </ThemeProvider>
    );
}

export default App;