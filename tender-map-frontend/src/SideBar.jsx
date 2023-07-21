import { useState } from 'react';
import {
    Box,
    Drawer,
    List,
    ListItem,
    ListItemButton,
    Divider,
    FormControlLabel,
    Checkbox,
    Fab
} from '@mui/material';

const drawerWidth = 350;

function SideBar(props) {
    const [checkedItems, setCheckedItems] = useState([]);

    const handleCheckboxToggle = (text) => () => {
        const currentIndex = checkedItems.indexOf(text);
        const newCheckedItems = [...checkedItems];

        if (currentIndex === -1) {
            newCheckedItems.push(text);
        } else {
            newCheckedItems.splice(currentIndex, 1);
        }

        setCheckedItems(newCheckedItems);
    };

    const handleFilterClick = () => {
        console.log('Checked Items:', checkedItems);
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
                <Box sx={{ overflow: 'auto', height: '100%', marginTop: 8, marginBottom: 5 }}>
                    <List>
                        {['Hlavní město Praha', 'Středočeský kraj', 'Jihočeský kraj', 'Plzeňský kraj', 'Karlovarský kraj', 'Ústecký kraj', 'Liberecký kraj', 'Královéhradecký kraj', 'Pardubický kraj', 'Kraj Vysočina', 'Jihomoravský kraj', 'Olomoucký kraj', 'Moravskoslezský kraj', 'Zlínský kraj'].map((text) => (
                            <ListItem key={text} disablePadding>
                                <label style={{ display: 'block', width: '100%', height: '100%' }}>
                                    <ListItemButton>
                                        <FormControlLabel
                                            control={<Checkbox size="small" checked={checkedItems.includes(text)} onChange={handleCheckboxToggle(text)} />}
                                            label={text}
                                        />
                                    </ListItemButton>
                                </label>
                            </ListItem>
                        ))}
                    </List>
                    <div style={{ position: 'absolute', bottom: '10px', left: '50%', transform: 'translateX(-50%)', width: '100%' }}>
                        <Fab variant="extended" color="primary" aria-label="add" style={{ width: '100%' }} onClick={handleFilterClick}>
                            Filter
                        </Fab>
                    </div>
                    <Divider />
                </Box>
            </Drawer>
        </Box>
    );
}

export default SideBar;
