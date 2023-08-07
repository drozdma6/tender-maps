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
                        <MenuIcon/>
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
                    <div className="navbarLogoLinks">
                        <a href="https://github.com/opendatalabcz/" target="_blank" rel="noopener noreferrer">
                            <img
                                className="imageLogo"
                                src="/data/opendatalab-logo.png"
                                alt="Open Data Lab"
                            />
                        </a>
                        <a href="https://fit.cvut.cz/en" target="_blank" rel="noopener noreferrer">
                            <img
                                className="imageLogo"
                                src="/data/fit-cvut-logo-en.svg"
                                alt="CTU FIT"
                            />
                        </a>

                        <a href="https://github.com/opendatalabcz/analyzer_public_contracts" target="_blank"
                           rel="noopener noreferrer">
                            <img
                                className="imageLogo"
                                src="/data/github-logo.svg"
                                alt="github"
                            />
                        </a>
                    </div>
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default Navigation;