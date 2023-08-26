import {useEffect, useState} from 'react';
import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import DeckGL from '@deck.gl/react';
import {MapView} from '@deck.gl/core';
import IconClusterLayer from './icon-cluster-layer';
import Legend from "./Legend.jsx";
import './styles.css';
import {FormControlLabel, FormGroup, Switch} from "@mui/material";
import Tooltip from "./Tooltip.jsx";

const MAP_VIEW = new MapView({repeat: true});

const INITIAL_VIEW_STATE = {
    longitude: 15.301806,
    latitude: 49.868280,
    zoom: 6.6,
    maxZoom: 16,
    pitch: 0,
    bearing: 0
};
const MAP_STYLE = 'https://basemaps.cartocdn.com/gl/dark-matter-nolabels-gl-style/style.json';

const DATA_SUPPLIERS_PATH = 'companies/suppliers';

const DATA_NON_SUPPLIERS_PATH = 'companies/non-suppliers';

function IconMap({
                     fetchData,
                     addFiltersToPath,
                     filterLocations,
                     filterAuthorities,
                     iconMapping = '/data/location-icon-mapping.json',
                     mapStyle = MAP_STYLE
                 }) {
    const [suppliersData, setSuppliersData] = useState([]);
    const [nonSuppliersData, setNonSuppliersData] = useState([]);
    const [hoverInfo, setHoverInfo] = useState({});
    const [suppliedProcurements, setSuppliedProcurements] = useState([]);
    const [companyOffers, setCompanyOffers] = useState([]);
    const [showLayers, setShowLayers] = useState({suppliers: true, nonSuppliers: true});

    useEffect(() => {
        fetchData(addFiltersToPath(DATA_SUPPLIERS_PATH), setSuppliersData);
        fetchData(addFiltersToPath(DATA_NON_SUPPLIERS_PATH), setNonSuppliersData);
    }, [filterLocations, filterAuthorities])

    const hideTooltip = () => {
        setHoverInfo({});
    };

    async function fetchOffers(companyId) {
        const path = `offers/companies/${companyId}`;
        fetchData(path, setCompanyOffers);
    }

    async function fetchSuppliedProcurements(companyId) {
        const path = `procurements/supplier/${companyId}`;
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

    const legendItems = [
        {color: '#f09236', label: 'Cluster'},
    ];

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
                title="Icon map of suppliers"
                text="Suppliers of public procurements" items={legendItems}>
                <FormGroup>
                    {/* Supplier Switch */}
                    <FormControlLabel
                        control={<Switch checked={showLayers.suppliers} onChange={handleSwitchChange('suppliers')}
                                         color="warning"/>}
                        label="Show suppliers"
                    />

                    {/* Non-Supplier Switch */}
                    <FormControlLabel
                        control={<Switch checked={showLayers.nonSuppliers}
                                         onChange={handleSwitchChange('nonSuppliers')}/>}
                        label="Show non-suppliers"
                    />
                </FormGroup>
            </Legend>
        </div>
    );
}

export default IconMap;