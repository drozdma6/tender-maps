import {useEffect, useState} from "react";
import {
    IconButton,
    useTheme,
    useMediaQuery,
    Typography,
    Box,
} from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";

function Legend({title, text, items, children}) {
    const [minimized, setMinimized] = useState(false);
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    useEffect(() => {
        if (!isMobile) {
            setMinimized(false);
        }
    }, [isMobile]);

    const toggleMinimized = () => {
        setMinimized((prevMinimized) => !prevMinimized);
    };

    return (
        <Box className={`container ${isMobile ? "container-mobile" : ""}`}>
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
                    <Typography variant="body1">{text}</Typography>
                    {items.map((item, index) => (
                        <div key={index} className="legend-item">
                            <span
                                className="legend-color"
                                style={{
                                    backgroundColor: item.color,
                                }}
                            ></span>
                            <Typography variant="body2">{item.label}</Typography>
                        </div>
                    ))}
                    {children}
                </>
            )}
        </Box>
    );
}

export default Legend;
