import {useEffect, useState} from "react";
import {
    Drawer,
    List,
    ListItem,
    ListItemButton,
    Divider,
    FormControlLabel,
    Checkbox,
    Autocomplete,
    TextField,
    useMediaQuery,
    useTheme,
    Box,
    IconButton,
} from "@mui/material";
import axios from "axios";
import CloseIcon from "@mui/icons-material/Clear";
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import {CONTRACTING_AUTHORITIES_PATH, CZECH_REGIONS} from "./constants.js";
import Typography from "@mui/material/Typography";

function FilterSideBar({
                           apiBaseUrl,
                           filterLocations,
                           setFilterLocations,
                           filterAuthorities,
                           setFilterAuthorities,
                           opened,
                           setShowFilterMenu
                       }) {
    const [authoritiesData, setAuthoritiesData] = useState();
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const drawerWidth = isMobile ? "100%" : 350;

    const handleCheckboxToggle = (text) => () => {
        const currentIndex = filterLocations.indexOf(text);
        const newCheckedItems = [...filterLocations];

        if (currentIndex === -1) {
            newCheckedItems.push(text);
        } else {
            newCheckedItems.splice(currentIndex, 1);
        }

        setFilterLocations(newCheckedItems);
    };

    useEffect(() => {
        fetchAuthorities();
    }, []);

    async function fetchAuthorities() {
        const url = apiBaseUrl + CONTRACTING_AUTHORITIES_PATH;
        const response = await axios.get(url);
        setAuthoritiesData(response.data);
    }

    const handleAuthorityChange = (event, newValue) => {
        if (newValue) {
            setFilterAuthorities((prevSelectedAuthorities) =>
                new Set([...prevSelectedAuthorities, newValue])
            );
        }
    };

    const handleAuthorityRemove = (authority) => {
        setFilterAuthorities((prevSelectedAuthorities) => {
            const newSelectedAuthorities = new Set(prevSelectedAuthorities);
            newSelectedAuthorities.delete(authority);
            return newSelectedAuthorities;
        });
    };

    const handleCloseFilterMenuButton = () => {
        setShowFilterMenu(false);
    }

    return (
        <Box>
            <Drawer
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    '& .MuiDrawer-paper': {
                        width: drawerWidth,
                    },
                }}
                variant="persistent"
                anchor="left"
                open={opened}
            >
                <Box sx={{overflow: "auto", marginTop: 8}}>
                    <Box display="flex" justifyContent="space-between" alignItems="center" p={2}>
                        <Autocomplete
                            disablePortal
                            id="combo-box-demo"
                            options={authoritiesData ? authoritiesData : []}
                            getOptionLabel={(option) => option.name || ""}
                            sx={{flexGrow: 1, marginRight: 2}}
                            renderInput={(params) => (
                                <TextField {...params} label="Contracting Authority"/>
                            )}
                            onChange={handleAuthorityChange}
                            value={filterAuthorities.name}
                        />
                        <IconButton onClick={handleCloseFilterMenuButton}>
                            <ArrowBackIosNewIcon/>
                        </IconButton>
                    </Box>

                    {Array.from(filterAuthorities).map((authority) => (
                        <Box
                            key={authority.name}
                            sx={{
                                alignItems: "center",
                            }}
                        >
                            <IconButton
                                onClick={() => handleAuthorityRemove(authority)}
                            >
                                <CloseIcon fontSize="medium"/>
                            </IconButton>
                            <span style={{wordBreak: "break-all"}}>
                                {authority.name}
                            </span>
                        </Box>
                    ))}

                    <Divider/>
                    <Typography
                        sx={{ml: 2}}
                        color="text.secondary"
                        display="block"
                        variant="caption"
                    >
                        Place of performance
                    </Typography>

                    <List>
                        {CZECH_REGIONS.map((text) => (
                            <ListItem key={text} disablePadding>
                                <label
                                    style={{
                                        display: "block",
                                        width: "100%",
                                        height: "100%",
                                    }}
                                >
                                    <ListItemButton>
                                        <FormControlLabel
                                            control={
                                                <Checkbox
                                                    size="small"
                                                    checked={filterLocations.includes(text)}
                                                    onChange={handleCheckboxToggle(text)}
                                                />
                                            }
                                            label={text}
                                        />
                                    </ListItemButton>
                                </label>
                            </ListItem>
                        ))}
                    </List>
                </Box>
            </Drawer>
        </Box>
    );
}

export default FilterSideBar;
