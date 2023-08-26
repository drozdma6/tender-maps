import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

function Footer() {
    return (
        <Box
            component="footer"
            sx={{
                textAlign: 'right',
                padding: '0.01rem',
                position: 'fixed',
                bottom: 0,
                backgroundColor: 'rgba(245, 245, 245, 0.4)',
            }}
        >
            <Typography variant="body2" color="textSecondary" sx={{ fontSize: '0.8rem' }}>
                2023, The operator is in no way responsible for the accuracy of the data and statistics presented in the Tender Maps.
            </Typography>
        </Box>
    );
}

export default Footer;
