import {
    Box,
    Drawer,
    List,
    ListItem,
    ListItemButton,
    Divider,
    FormControlLabel,
    Checkbox,
    Fab,
    Autocomplete,
    TextField
} from '@mui/material';
import axios from "axios";
import ClearIcon from '@mui/icons-material/Clear';
import {useEffect, useState} from "react";
import { IconButton } from '@mui/material';



const drawerWidth = 350;

function SideBar(props) {
    const [authoritiesData, setAuthoritiesData] = useState();

    const handleCheckboxToggle = (text) => () => {
        const currentIndex = props.filterLocations.indexOf(text);
        const newCheckedItems = [...props.filterLocations];

        if (currentIndex === -1) {
            newCheckedItems.push(text);
        } else {
            newCheckedItems.splice(currentIndex, 1);
        }

        props.setFilterLocations(newCheckedItems);
    };

    const handleFilterClick = () => {
        props.onFilterButtonClick();
    };

    useEffect(() => {
        fetchContractorAuthority();
    }, []);

    async function fetchContractorAuthority(){
        const url = 'http://localhost:8081/authorities';
        const response = await axios.get(url)
        setAuthoritiesData(response.data)
    }

    const handleAuthorityChange = (event, newValue) => {
        if (newValue) {
            props.setFilterAuthorities((prevSelectedAuthorities) =>
                new Set([...prevSelectedAuthorities, newValue])
            );
        }
    };

    const handleAuthorityRemove = (authority) => {
        props.setFilterAuthorities((prevSelectedAuthorities) => {
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
                open={props.opened}
            >
                <Box sx={{overflow: 'auto', height: '100%', marginTop: 8, marginBottom: 5}}>
                    <Autocomplete
                        disablePortal
                        id="combo-box-demo"
                        // options={authoritiesData ? authoritiesData.map(item => item.name) : []}
                        options={authoritiesData ? authoritiesData : []}
                        getOptionLabel={(option) => option.name || ""}
                        sx={{width: 300, margin: 3}}
                        renderInput={(params) =>
                            <TextField {...params} label="Contractor Authority"/>}
                        onChange={handleAuthorityChange}
                        value={props.filterAuthorities.name} // Use the value prop to control the input value
                    />
                        {
                            Array.from(props.filterAuthorities).map((authority) => (
                                <div key={authority.name}
                                    style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    marginLeft: 8
                                }}>
                                    <IconButton onClick={() => handleAuthorityRemove(authority)}>
                                        <ClearIcon></ClearIcon>
                                    </IconButton>
                                    <span  style={{ wordBreak: 'break-all' }}>{authority.name}</span>
                                </div>
                            ))
                        }

                    <Divider/>

                    <List>
                        {['Hlavní město Praha', 'Středočeský kraj', 'Jihočeský kraj', 'Plzeňský kraj', 'Karlovarský kraj', 'Ústecký kraj', 'Liberecký kraj', 'Královéhradecký kraj', 'Pardubický kraj', 'Kraj Vysočina', 'Jihomoravský kraj', 'Olomoucký kraj', 'Moravskoslezský kraj', 'Zlínský kraj'].map((text) => (
                            <ListItem key={text} disablePadding>
                                <label style={{display: 'block', width: '100%', height: '100%'}}>
                                    <ListItemButton>
                                        <FormControlLabel
                                            control={<Checkbox size="small"
                                                               checked={props.filterLocations.includes(text)}
                                                               onChange={handleCheckboxToggle(text)}/>}
                                            label={text}
                                        />
                                    </ListItemButton>
                                </label>
                            </ListItem>
                        ))}
                    </List>
                    <div style={{
                        position: 'absolute',
                        bottom: '10px',
                        left: '50%',
                        transform: 'translateX(-50%)',
                        width: '100%'
                    }}>
                        <Fab variant="extended" color="primary" aria-label="add" style={{width: '100%'}}
                             onClick={handleFilterClick}>
                            Filter
                        </Fab>
                    </div>
                    <Divider/>
                </Box>
            </Drawer>
        </Box>
    );
}

export default SideBar;
