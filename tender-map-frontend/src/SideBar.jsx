import {
    Box,
    Drawer,
    List,
    ListItem,
    ListItemButton,
    Divider,
    FormControlLabel,
    Checkbox,
    Autocomplete,
    TextField, useMediaQuery
} from '@mui/material';
import axios from "axios";
import ClearIcon from '@mui/icons-material/Clear';
import {useEffect, useState} from "react";
import {IconButton} from '@mui/material';
import {CZECH_REGIONS, MOBILE_BREAKPOINT} from "./constants.js";

function SideBar({filterLocations, setFilterLocations, filterAuthorities, setFilterAuthorities, opened}) {
    const [authoritiesData, setAuthoritiesData] = useState();

    const isMobile = useMediaQuery(`(max-width: ${MOBILE_BREAKPOINT}px)`);

    const drawerWidth = isMobile ? '100%' : 350;

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
        fetchContractorAuthority();
    }, []);

    async function fetchContractorAuthority() {
        const url = 'http://localhost:8081/authorities';
        const response = await axios.get(url)
        setAuthoritiesData(response.data)
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
        })
    };

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
                <Box className='sidebar'>
                    <Autocomplete
                        disablePortal
                        id="combo-box-demo"
                        options={authoritiesData ? authoritiesData : []}
                        getOptionLabel={(option) => option.name || ""}
                        sx={{width: 300, margin: 3}}
                        renderInput={(params) =>
                            <TextField {...params} label="Contractor Authority"/>}
                        onChange={handleAuthorityChange}
                        value={filterAuthorities.name}
                    />
                    {
                        Array.from(filterAuthorities).map((authority) => (
                            <div key={authority.name}
                                 style={{
                                     alignItems: 'center',
                                     marginLeft: 8
                                 }}>
                                <IconButton onClick={() => handleAuthorityRemove(authority)}>
                                    <ClearIcon></ClearIcon>
                                </IconButton>
                                <span style={{wordBreak: 'break-all'}}>{authority.name}</span>
                            </div>
                        ))
                    }

                    <Divider/>

                    <List>
                        {CZECH_REGIONS.map((text) => (
                            <ListItem key={text} disablePadding>
                                <label style={{display: 'block', width: '100%', height: '100%'}}>
                                    <ListItemButton>
                                        <FormControlLabel
                                            control={<Checkbox size="small"
                                                               checked={filterLocations.includes(text)}
                                                               onChange={handleCheckboxToggle(text)}/>}
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

export default SideBar;
