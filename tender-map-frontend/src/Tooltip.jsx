import {
    Divider,
    Link,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@mui/material";

function truncate(str, n) {
    return (str.length > n) ? str.slice(0, n - 1) + '…' : str;
}

function getProcurementUrl(procurementSystemNumber) {
    return "https://nen.nipez.cz/verejne-zakazky/detail-zakazky/" + procurementSystemNumber.replaceAll("/", "-");
}

function Tooltip({info, suppliedProcurements, offers}) {
    const {object, x, y} = info;

    if (!object) {
        return null;
    }

    return (
        <div className="tooltip" style={{left: x, top: y}}>
            <h3>{object.name}</h3>
            <TableContainer className="custom-table">
                {suppliedProcurements.length !== 0 ? (
                    <div>
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
                                            {row.contractPrice} CZK
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                        <Divider sx={{borderBottomWidth: 4}}/>
                    </div>
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
                        {offers.map((row) => (
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
                                    {row.price} CZK
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
}

export default Tooltip;