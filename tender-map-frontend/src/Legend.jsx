import {useEffect, useState} from "react";
import {
    IconButton,
    useTheme,
    useMediaQuery,
    Typography,
    Box,
} from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import {makeStyles} from "@mui/styles";

const useStyles = makeStyles((theme) => ({
    container: {
        position: 'absolute',
        right: '2vw',
        marginTop: '2vw',
        padding: '2vw',
        backgroundColor: theme.palette.background.paper,
    },
    containerMobile: {
        left: '4vw',
        right: '4vw',
        marginTop: '4vw',
    },
}));

function Legend({title, text, children}) {
    const [minimized, setMinimized] = useState(false);
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const classes = useStyles();

    useEffect(() => {
        if (!isMobile) {
            setMinimized(false);
        }
    }, [isMobile]);

    const toggleMinimized = () => {
        setMinimized((prevMinimized) => !prevMinimized);
    };

    return (
        <Box className={`${classes.container} ${isMobile ? classes.containerMobile : ""}`}>
            <Box className="legend-header">
                <Typography variant="h6" fontWeight="bold">
                    {title}
                </Typography>
                {isMobile && (
                    <IconButton
                        className="minimize-button"
                        onClick={toggleMinimized}
                    >
                        <InfoIcon/>
                    </IconButton>
                )}
            </Box>

            {!minimized && (
                <>
                    <Box maxWidth={380} textAlign={"justify"}>
                        <Typography variant="body1" >{text}</Typography>
                    </Box>
                    {children}
                </>
            )}
        </Box>
    );
}

export default Legend;
