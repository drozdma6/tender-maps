import './styles.css';
import {useEffect, useState} from "react";
import {IconButton} from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';
import {useMediaQuery} from "@mui/material";
import {MOBILE_BREAKPOINT} from './constants';

function Legend({title, text, items, children}) {
    const [minimized, setMinimized] = useState(false);
    const isMobile = useMediaQuery(`(max-width: ${MOBILE_BREAKPOINT}px)`);

    useEffect(() => {
        if (!isMobile) {
            setMinimized(false);
        }
    }, [isMobile]);

    const toggleMinimized = () => {
        setMinimized(prevMinimized => !prevMinimized);
    };

    return (
        <div className={`container ${minimized ? "minimized" : ''}`}>
            <div className="legend-header">
                <h3>
                    {title}
                    {isMobile && <IconButton className="minimize-button" onClick={toggleMinimized}>
                        <InfoIcon/>
                    </IconButton>}
                </h3>
            </div>

            {!minimized && (
                <>
                    <p>{text}</p>
                    {items.map((item, index) => (
                        <div key={index} className="legend-item">
                            <span style={{backgroundColor: item.color}} className="legend-color"></span>
                            <span className="legend-label">{item.label}</span>
                        </div>
                    ))}
                    {children}
                </>
            )}
        </div>
    );
}

export default Legend;