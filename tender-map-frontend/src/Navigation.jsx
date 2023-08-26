import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Button from '@mui/material/Button';
import MenuItem from '@mui/material/MenuItem';
import {useState} from "react";

const pages = ['HEATMAP', 'ICONMAP', 'HEXAGONMAP', 'INFO'];

function Navigation({ onPageChange }) {
    const [anchorElNav, setAnchorElNav] = useState(null);

    const handleOpenNavMenu = (event) => {
        setAnchorElNav(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

    return (
        <AppBar position="static" style={{
            backgroundColor: 'white',
            color: 'black',
            zIndex: 1400,
            maxHeight: 'var(--app-bar-height)'
        }}>
            <Container maxWidth="xl">
                <Toolbar disableGutters>
                    <Typography
                        variant="h6"
                        noWrap
                        component="a"
                        href="/"
                        sx={{
                            mr: 2,
                            display: { xs: 'none', md: 'flex' },
                            fontFamily: 'monospace',
                            fontWeight: 700,
                            letterSpacing: '.3rem',
                            color: 'inherit',
                            textDecoration: 'none',
                        }}
                    >
                        <img src="/map_icon.svg" alt="Map Icon" style={{marginRight: '10px', marginLeft: '10px', width: '24px'}}/>
                        TENDER MAPS
                    </Typography>

                    <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
                        <IconButton
                            size="large"
                            aria-label="account of current user"
                            aria-controls="menu-appbar"
                            aria-haspopup="true"
                            onClick={handleOpenNavMenu}
                            color="inherit"
                        >
                            <MenuIcon />
                        </IconButton>
                        <Menu
                            id="menu-appbar"
                            anchorEl={anchorElNav}
                            anchorOrigin={{
                                vertical: 'bottom',
                                horizontal: 'left',
                            }}
                            keepMounted
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'left',
                            }}
                            open={Boolean(anchorElNav)}
                            onClose={handleCloseNavMenu}
                            sx={{
                                display: { xs: 'block', md: 'none' },
                            }}
                        >
                            {pages.map((page) => (
                                <MenuItem key={page} onClick={() => onPageChange(page)}>
                                    <Typography textAlign="center">{page}</Typography>
                                </MenuItem>
                            ))}
                        </Menu>
                    </Box>
                    <Typography
                        variant="h5"
                        noWrap
                        component="a"
                        href="/"
                        sx={{
                            mr: 2,
                            display: { xs: 'flex', md: 'none' },
                            flexGrow: 1,
                            fontFamily: 'monospace',
                            fontWeight: 700,
                            letterSpacing: '.3rem',
                            color: 'inherit',
                            textDecoration: 'none',
                        }}
                    >
                        <img src="/map_icon.svg" alt="Map Icon" style={{marginRight: '10px', marginLeft: '10px', width: '24px'}}/>
                        TENDER MAPS
                    </Typography>
                    <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                        {pages.map((page) => (
                            <Button
                                key={page}
                                onClick={() => onPageChange(page)}
                                sx={{ my: 2, color: 'black', display: 'block' }}
                            >
                                {page}
                            </Button>
                        ))}
                        <Box className="navbarLogoLinks">
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
                        </Box>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}
export default Navigation;