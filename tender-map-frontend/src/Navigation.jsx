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
            <Container maxWidth="xl">
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
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default Navigation;