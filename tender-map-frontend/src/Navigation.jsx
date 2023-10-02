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
import {styled, Switch, useTheme} from "@mui/material";

const pages = ['HEATMAP', 'ICONMAP', 'HEXAGONMAP', 'INFO'];

const MaterialUISwitch = styled(Switch)(() => {
    const theme = useTheme();
    const thumbColor = theme.palette.background.default;
    return {
        width: 62,
        height: 34,
        padding: 7,
        '& .MuiSwitch-switchBase': {
            margin: 1,
            padding: 0,
            transform: 'translateX(6px)',
            '&.Mui-checked': {
                color: '#fff',
                transform: 'translateX(22px)',
                '& .MuiSwitch-thumb:before': {
                    backgroundImage: `url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" height="20" width="20" viewBox="0 0 20 20"><path fill="${encodeURIComponent(
                        '#fff',
                    )}" d="M4.2 2.5l-.7 1.8-1.8.7 1.8.7.7 1.8.6-1.8L6.7 5l-1.9-.7-.6-1.8zm15 8.3a6.7 6.7 0 11-6.6-6.6 5.8 5.8 0 006.6 6.6z"/></svg>')`,
                },
                '& + .MuiSwitch-track': {
                    opacity: 1,
                    backgroundColor: '#fff',
                },
            },
        },
        '& .MuiSwitch-thumb': {
            backgroundColor: thumbColor,
            width: 32,
            height: 32,
            '&:before': {
                content: "''",
                position: 'absolute',
                width: '100%',
                height: '100%',
                left: 0,
                top: 0,
                backgroundRepeat: 'no-repeat',
                backgroundPosition: 'center',
                backgroundImage: `url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" height="20" width="20" viewBox="0 0 20 20"><path fill="${encodeURIComponent(
                    '#555',
                )}" d="M9.305 1.667V3.75h1.389V1.667h-1.39zm-4.707 1.95l-.982.982L5.09 6.072l.982-.982-1.473-1.473zm10.802 0L13.927 5.09l.982.982 1.473-1.473-.982-.982zM10 5.139a4.872 4.872 0 00-4.862 4.86A4.872 4.872 0 0010 14.862 4.872 4.872 0 0014.86 10 4.872 4.872 0 0010 5.139zm0 1.389A3.462 3.462 0 0113.471 10a3.462 3.462 0 01-3.473 3.472A3.462 3.462 0 016.527 10 3.462 3.462 0 0110 6.528zM1.665 9.305v1.39h2.083v-1.39H1.666zm14.583 0v1.39h2.084v-1.39h-2.084zM5.09 13.928L3.616 15.4l.982.982 1.473-1.473-.982-.982zm9.82 0l-.982.982 1.473 1.473.982-.982-1.473-1.473zM9.305 16.25v2.083h1.389V16.25h-1.39z"/></svg>')`,
            },
        },
        '& .MuiSwitch-track': {
            opacity: 1,
            backgroundColor: '#000',
            borderRadius: 20 / 2,
        },
    }
});

function Navigation({onPageChange, themeToggle, setThemeToggle}) {
    const [anchorElNav, setAnchorElNav] = useState(null);
    const theme = useTheme();

    const handleOpenNavMenu = (event) => {
        setAnchorElNav(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

    function handleDarkModeToggle() {
        setThemeToggle(!themeToggle);
    }

    const imgStyle = theme.palette.mode === 'light'
        ? { filter: 'invert(0)' } // Original color when light theme
        : { filter: 'invert(1)' }; // Inverted color when dark theme

    return (
        <AppBar position="static" color="default" style={{
            zIndex: 1400,
            maxHeight: 'var(--app-bar-height)'
        }}>
            <Container maxWidth="false">
                <Toolbar disableGutters>
                    <Typography
                        variant="h6"
                        noWrap
                        component="a"
                        href="/"
                        color="inherit"
                        sx={{
                            mr: 2,
                            display: {xs: 'none', md: 'flex'},
                            fontFamily: 'monospace',
                            fontWeight: 700,
                            letterSpacing: '.3rem',
                            textDecoration: 'none',
                            color: theme.palette.text.primary,
                        }}
                    >
                        <img src="/map_icon_black.svg" alt="Map Icon"
                             style={{
                                 marginRight: '10px',
                                 marginLeft: '10px',
                                 width: '24px',
                                 ...imgStyle,
                             }}/>
                        TENDER MAPS
                    </Typography>

                    <Box sx={{flexGrow: 1, display: {xs: 'flex', md: 'none'}}}>
                        <IconButton
                            size="large"
                            aria-label="account of current user"
                            aria-controls="menu-appbar"
                            aria-haspopup="true"
                            onClick={handleOpenNavMenu}
                            color="inherit"
                        >
                            <MenuIcon/>
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
                                display: {xs: 'block', md: 'none'},
                            }}
                        >
                            {pages.map((page) => (
                                <MenuItem key={page} onClick={() => onPageChange(page)}>
                                    <Typography variant="body1" textAlign="center">{page}</Typography>
                                </MenuItem>
                            ))}
                        </Menu>
                    </Box>
                    <Typography
                        variant="h5"
                        noWrap
                        component="a"
                        href="/"
                        color="inherit"
                        sx={{
                            mr: 2,
                            display: {xs: 'flex', md: 'none'},
                            flexGrow: 1,
                            fontFamily: 'monospace',
                            fontWeight: 700,
                            letterSpacing: '.3rem',
                            textDecoration: 'none',
                        }}
                    >
                        <img src="/map_icon_black.svg" alt="Map Icon"
                             style={{
                                 marginRight: '10px',
                                 marginLeft: '10px',
                                 width: '24px',
                                 ...imgStyle,
                             }}/>
                        TENDER MAPS
                    </Typography>
                    <Box sx={{flexGrow: 1, display: {xs: 'none', md: 'flex'}}}>
                        {pages.map((page) => (
                            <Button
                                key={page}
                                color="inherit"
                                onClick={() => onPageChange(page)}
                                sx={{
                                    my: 2,
                                    display: 'block'
                                }}
                            >
                                {page}
                            </Button>
                        ))}
                        <Box className="navbarLogoLinks">
                            <a href="https://fit.cvut.cz/en" target="_blank" rel="noopener noreferrer">
                                <img
                                    className="imageLogo"
                                    src="/data/fit-cvut-logo-en.svg"
                                    alt="CTU FIT"
                                    style={{
                                        width: "90px"
                                    }}
                                />
                            </a>
                            <a href="https://github.com/opendatalabcz/" target="_blank" rel="noopener noreferrer">
                                <img
                                    className="imageLogo"
                                    src="/data/opendatalab-logo.png"
                                    alt="Open Data Lab"
                                />
                            </a>
                            <a href="https://github.com/opendatalabcz/analyzer_public_contracts" target="_blank"
                               rel="noopener noreferrer">
                                <img
                                    className="imageLogo"
                                    src="/data/github-logo-black.svg"
                                    alt="github"
                                    style={imgStyle}
                                />
                            </a>
                        </Box>
                    </Box>
                    <Box marginBottom={1}>
                        <MaterialUISwitch
                            onChange={handleDarkModeToggle}
                            name="nightShift"
                            inputProps={{'aria-label': 'secondary checkbox'}}
                            checked={themeToggle}
                        />
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default Navigation;