import {
    Divider,
    Link,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow, useTheme
} from "@mui/material";
import Box from "@mui/material/Box";
import {ArrowLeft, ArrowRight} from "@mui/icons-material";
import IconButton from "@mui/material/IconButton";
import {useEffect, useState} from "react";
import {OFFERS_PATH, PROCUREMENTS_PATH} from "./constants.js";

function truncate(str, n) {
    return (str.length > n) ? str.slice(0, n - 1) + '…' : str;
}

function getProcurementUrl(procurementSystemNumber) {
    return "https://nen.nipez.cz/verejne-zakazky/detail-zakazky/" + procurementSystemNumber.replaceAll("/", "-");
}

function renderPrice(price) {
    if (price === null || price === 'undefined') {
        return "N/A CZK";
    } else {
        return price.toString() + " CZK";
    }
}

function buildLinkToCompanyDetail(organisationId) {
    return "https://or.justice.cz/ias/ui/rejstrik-$firma?ico=" + organisationId;
}

function Tooltip({
                     companyName,
                     companyId,
                     organisationId,
                     x,
                     y,
                     fetchData,
                     addFiltersToPath,
                     layerId,
                     curCompanyIndex,
                     setCurCompanyIndex,
                     totalNumberOfCompanies
                 }) {
    const [suppliedProcurements, setSuppliedProcurements] = useState([]);
    const [companyOffers, setCompanyOffers] = useState([]);
    const theme = useTheme();

    useEffect(() => {
        if (layerId === "suppliers") {
            fetchData(addFiltersToPath(PROCUREMENTS_PATH, {"supplierId": companyId}), setSuppliedProcurements);
        }
        fetchData(addFiltersToPath(OFFERS_PATH, {"companyId": companyId}), setCompanyOffers);
    }, [curCompanyIndex, companyId, layerId, fetchData, addFiltersToPath]);

    const handleLeftButtonClick = () => {
        const newIndex = curCompanyIndex === 0 ? totalNumberOfCompanies - 1 : curCompanyIndex - 1;
        setCurCompanyIndex(newIndex);
    }

    const handleRightButtonClick = () => {
        const newIndex = curCompanyIndex === totalNumberOfCompanies - 1 ? 0 : curCompanyIndex + 1;
        setCurCompanyIndex(newIndex);
    }
    return (
        <Box backgroundColor={theme.palette.background.paper} sx={{
            position: 'absolute',
            z: 9,
            fontSize: 12,
            padding: 1.5,
            minWidth: 200,
            pointerEvents: 'all',
            overflowY: 'auto',
            maxHeight: 300,
            left: x,
            top: y
        }}>
            <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
            }}>
                <Link href={buildLinkToCompanyDetail(organisationId)} variant={"subtitle1"} target="_blank">
                    {truncate(companyName, 30)}
                </Link>
                {
                    totalNumberOfCompanies > 1 &&
                    <Box>
                        <IconButton onClick={handleLeftButtonClick} disabled={curCompanyIndex === 0}>
                            <ArrowLeft/>
                        </IconButton>
                        <IconButton onClick={handleRightButtonClick}
                                    disabled={curCompanyIndex === totalNumberOfCompanies - 1}>
                            <ArrowRight/>
                        </IconButton>
                    </Box>
                }
            </Box>
            <TableContainer className="custom-table">
                {suppliedProcurements.length !== 0 ? (
                    <Box>
                        <Table stickyHeader aria-label="sticky table" size="small">
                            <TableHead>
                                <TableRow>
                                    <TableCell>Supplied Tenders</TableCell>
                                    <TableCell align="right">Price</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {suppliedProcurements.map((row) => (
                                    <TableRow key={row.id} sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                                        <TableCell component="th" scope="row">
                                            <Link href={getProcurementUrl(row.systemNumber)} target="_blank">
                                                {truncate(row.name, 30)}
                                            </Link>
                                        </TableCell>
                                        <TableCell align="right">
                                            {renderPrice(row.contractPrice)}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                        <Divider sx={{borderBottomWidth: 4}}/>
                    </Box>
                ) : (
                    <div/>
                )}

                <Table stickyHeader aria-label="sticky table" size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>Offers</TableCell>
                            <TableCell align="right">Price</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {companyOffers.map((row) => (
                            <TableRow
                                key={row.id}
                                sx={{'&:last-child td, &:last-child th': {border: 0}}}
                            >
                                <TableCell component="th" scope="row">
                                    <Link href={getProcurementUrl(row.procurement.systemNumber)} target="_blank">
                                        {truncate(row.procurement.name, 30)}
                                    </Link>
                                </TableCell>
                                <TableCell align="right">
                                    {renderPrice(row.price)}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
}

export default Tooltip;