import {useState} from 'react';
import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import DeckGL from '@deck.gl/react';
import {MapView} from '@deck.gl/core';
import IconClusterLayer from './icon-cluster-layer';
import Legend from "./Legend.jsx";
import axios from "axios";
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

const DATA_SUPPLIERS_URL = 'http://localhost:8081/companies/suppliers';

const DATA_NON_SUPPLIERS_URL = 'http://localhost:8081/companies/non-suppliers';

function IconMap({
                     buildDataUrl,
                     iconMapping = '/data/location-icon-mapping.json',
                     mapStyle = MAP_STYLE
                 }) {
    const [hoverInfo, setHoverInfo] = useState({});
    const [suppliedProcurements, setSuppliedProcurements] = useState([]);
    const [companyOffers, setCompanyOffers] = useState([]);
    const [showLayers, setShowLayers] = useState({suppliers: true, nonSuppliers: true});

    const hideTooltip = () => {
        setHoverInfo({});
    };

    async function fetchOffers(companyId) {
        try {
            const procurementsDataUrl = `http://localhost:8081/offers/companies/${companyId}`;
            const response = await axios.get(procurementsDataUrl);
            setCompanyOffers(response.data);
        } catch (error) {
            console.error(`Error fetching offers of ${companyId}:`, error);
        }
    }

    async function fetchSuppliedProcurements(companyId) {
        try {
            const procurementsDataUrl = `http://localhost:8081/procurements/supplier/${companyId}`;
            const response = await axios.get(procurementsDataUrl);
            setSuppliedProcurements(response.data);
        } catch (error) {
            console.error(`Error fetching supplied procurements of ${companyId}:`, error);
        }
    }

    const expandTooltip = info => {
        if (info.picked) {
            if (info.objects) {
                setHoverInfo({})
            } else {
                fetchOffers(info.object.id);
                fetchSuppliedProcurements(info.object.id);
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
            data: buildDataUrl(DATA_SUPPLIERS_URL),
            iconAtlas: '/data/location-icon-orange.png',
            ...layerProps,
            id: 'suppliers',
            sizeScale: 40
        }),
        showLayers.nonSuppliers && new IconClusterLayer({
            data: buildDataUrl(DATA_NON_SUPPLIERS_URL),
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

            <div style={{
                position: 'absolute',
                right: '10px',
                backgroundColor: '#fff',
                padding: '16px',
                borderRadius: '8px',
                boxShadow: '0px 2px 6px rgba(0, 0, 0, 0.2)'
            }}>
                <Legend
                    title="Icon map of suppliers"
                    text="Suppliers of public procurements" items={legendItems}
                />
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
            </div>
        </div>
    );
}

export default IconMap;