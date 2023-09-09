import {useEffect, useState} from 'react';
import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import DeckGL from '@deck.gl/react';
import {MapView} from '@deck.gl/core';
import IconClusterLayer from './icon-cluster-layer';
import Legend from "./Legend.jsx";
import './styles.css';
import {Switch} from "@mui/material";
import Tooltip from "./Tooltip.jsx";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

const MAP_VIEW = new MapView({repeat: true});

const INITIAL_VIEW_STATE = {
    longitude: 15.301806,
    latitude: 49.868280,
    zoom: 6.6,
    maxZoom: 16,
    pitch: 0,
    bearing: 0
};

const DATA_SUPPLIERS_PATH = '/companies/suppliers';

const DATA_NON_SUPPLIERS_PATH = '/companies/non-suppliers';

function IconMap({
                     fetchData,
                     addFiltersToPath,
                     filterLocations,
                     filterAuthorities,
                     iconMapping = '/data/location-icon-mapping.json',
                     mapStyle
                 }) {
    const [suppliersData, setSuppliersData] = useState([]);
    const [nonSuppliersData, setNonSuppliersData] = useState([]);
    const [hoverInfo, setHoverInfo] = useState({});
    const [suppliedProcurements, setSuppliedProcurements] = useState([]);
    const [companyOffers, setCompanyOffers] = useState([]);
    const [showLayers, setShowLayers] = useState({suppliers: true, nonSuppliers: true});

    useEffect(() => {
        fetchData(addFiltersToPath(DATA_SUPPLIERS_PATH, {"hasExactAddress": true}), setSuppliersData);
        fetchData(addFiltersToPath(DATA_NON_SUPPLIERS_PATH, {"hasExactAddress": true}), setNonSuppliersData);
    }, [filterLocations, filterAuthorities])

    const hideTooltip = () => {
        setHoverInfo({});
    };

    async function fetchOffers(companyId) {
        const path = `/offers/companies/${companyId}`;
        fetchData(path, setCompanyOffers);
    }

    async function fetchSuppliedProcurements(companyId) {
        const path = `/procurements/supplier/${companyId}`;
        fetchData(path, setSuppliedProcurements);
    }

    const expandTooltip = info => {
        setSuppliedProcurements([]);
        setCompanyOffers([]);
        if (info.picked) {
            if (info.objects) {
                setHoverInfo({})
            } else {
                //fetch supplied procurements only if click is on supplier company
                if (info.layer.id === "suppliers") {
                    fetchSuppliedProcurements(info.object.id);
                }
                fetchOffers(info.object.id); //fetch for both suppliers / non-suppliers
                setHoverInfo(info);
            }
        } else {
            setHoverInfo({});
        }
    };

    const layerProps = {
        pickable: true,
        getPosition: d => [d.address.longitude, d.address.latitude],
        iconMapping,
    };

    // Create the IconClusterLayer instances based on the switch status
    const layers = [
        showLayers.suppliers && new IconClusterLayer({
            data: suppliersData,
            iconAtlas: '/data/location-icon-orange.png',
            ...layerProps,
            id: 'suppliers',
            sizeScale: 40
        }),
        showLayers.nonSuppliers && new IconClusterLayer({
            data: nonSuppliersData,
            ...layerProps,
            id: 'nonSuppliers',
            iconAtlas: '/data/location-icon-blue.png',
            sizeScale: 40
        }),
    ].filter(Boolean);

    // Handlers for switch changes
    const handleSwitchChange = (layerType) => () => {
        setShowLayers((prev) => ({...prev, [layerType]: !prev[layerType]}));
    };

    const legendItems = {
        suppliers: {color: '#f09236'},
        nonSuppliers: {color: '#55A2CF'}
    };

    return (
        <div>
            <DeckGL
                layers={layers}
                views={MAP_VIEW}
                initialViewState={INITIAL_VIEW_STATE}
                controller={{dragRotate: false}}
                onViewStateChange={hideTooltip}
                onClick={expandTooltip}
            >
                <Map reuseMaps mapLib={maplibregl} mapStyle={mapStyle} preventStyleDiffing={true}/>
            </DeckGL>

            <Tooltip info={hoverInfo} suppliedProcurements={suppliedProcurements} offers={companyOffers}/>

            <Legend
                title="Icon map"
                text="Data set of all participants in tenders." items={[]}>
                <Box display="flex" justifyContent="space-between" alignItems="center" marginTop={2}>
                    <Box textAlign="center" marginLeft={4}>
                        <Typography variant="body1" fontWeight="bold" style={{color: legendItems.suppliers.color}}>
                            Suppliers
                        </Typography>
                        <Switch checked={showLayers.suppliers} onChange={handleSwitchChange('suppliers')}
                                color="warning"/>
                        <Typography variant="h6" fontWeight="bold" style={{color: legendItems.suppliers.color}}>
                            {suppliersData.length}
                        </Typography>
                    </Box>
                    <Box textAlign="center" marginRight={4}>
                        <Typography variant="body1" fontWeight="bold" style={{color: legendItems.nonSuppliers.color}}>
                            Non-suppliers
                        </Typography>
                        <Switch checked={showLayers.nonSuppliers} onChange={handleSwitchChange('nonSuppliers')}/>
                        <Typography variant="h6" fontWeight="bold" style={{color: legendItems.nonSuppliers.color}}>
                            {nonSuppliersData.length}
                        </Typography>
                    </Box>
                </Box>
            </Legend>
        </div>
    );
}

export default IconMap;