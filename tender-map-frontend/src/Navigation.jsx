import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import Button from '@mui/material/Button';
import IconButton from "@mui/material/IconButton";
import MenuIcon from '@mui/icons-material/Menu';

function Navigation({onHeatMapClick, onIconMapClick, onHexagonMapClick, onSideMenuClick}) {
    const handleHeatMapButtonClick = () => {
        onHeatMapClick();
    };

    const handleIconMapButtonClick = () => {
        onIconMapClick();
    };

    const handleHexagonMapButtonClick = () => {
        onHexagonMapClick();
    };

    const handleSideMenuOpener = () => {
        onSideMenuClick();
    };

    return (
        <AppBar position="fixed" style={{
            backgroundColor: 'white',
            color: 'black',
            zIndex: 1400,
        }}>
            <Container maxWidth="false">
                <Toolbar disableGutters>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleSideMenuOpener}
                        edge="start"
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography
                        variant="h6"
                        noWrap
                        component="a"
                        href="/"
                        sx={{
                            mr: 2,
                            display: {xs: 'none', md: 'flex'},
                            fontFamily: 'monospace',
                            fontWeight: 700,
                            letterSpacing: '.3rem',
                            color: 'inherit',
                            textDecoration: 'none',
                        }}
                    >
                        Tender Maps
                    </Typography>
                    <Button
                        onClick={handleHeatMapButtonClick}
                        sx={{my: 2, color: 'inherit', display: 'block'}}
                    >
                        Heat Map
                    </Button>
                    <Button
                        onClick={handleIconMapButtonClick}
                        sx={{my: 2, color: 'inherit', display: 'block'}}
                    >
                        Icon Map
                    </Button>
                    <Button
                        onClick={handleHexagonMapButtonClick}
                        sx={{my: 2, color: 'inherit', display: 'block'}}
                    >
                        Hexagon Map
                    </Button>
                    <a href="https://github.com/opendatalabcz/analyzer_public_contracts" target="_blank" rel="noopener noreferrer"
                       className="navbar__item navbar__link">GitHub
                        <svg width="13.5" height="13.5" aria-hidden="true" viewBox="0 0 24 24"
                             className="iconExternalLink">
                            <path fill="currentColor"
                                  d="M21 13v10h-21v-19h12v2h-10v15h17v-8h2zm3-12h-10.988l4.035 4-6.977 7.07 2.828 2.828 6.977-7.07 4.125 4.172v-11z"></path>
                        </svg>
                    </a>
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default Navigation;