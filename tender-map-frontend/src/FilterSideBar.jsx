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
    const [authoritiesData, setAuthoritiesData] = useState([]);
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const drawerWidth = isMobile ? "100%" : 350;

    useEffect(() => {
        fetchAuthorities();
    }, []);

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

    async function fetchAuthorities() {
        const url = apiBaseUrl + CONTRACTING_AUTHORITIES_PATH;
        const response = await axios.get(url);
        setAuthoritiesData(response.data);
    }

    const handleAuthorityChange = (_, newValue) => {
        setFilterAuthorities(newValue);
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
                            disableClearable
                            multiple
                            id="combo-box-demo"
                            options={authoritiesData ? authoritiesData : []}
                            getOptionLabel={(option) => option.name || ""}
                            renderInput={(params) => (
                                <TextField {...params} label="Contracting Authority"/>
                            )}
                            filterSelectedOptions
                            onChange={handleAuthorityChange}
                            value={filterAuthorities.name}
                            isOptionEqualToValue={(option, value) => option.id === value.id}
                            style={{paddingTop: 8, width: 500, overflow: 'hidden' }}
                        >
                        </Autocomplete>
                        <IconButton onClick={handleCloseFilterMenuButton} style={{paddingTop: 15, marginLeft: 2}}>
                            <ArrowBackIosNewIcon/>
                        </IconButton>
                    </Box>
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
