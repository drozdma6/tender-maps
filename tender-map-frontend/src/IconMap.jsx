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
import {COMPANIES_PATH} from "./constants.js";

const MAP_VIEW = new MapView({repeat: true});

const LEGEND_TEXT = "This map shows the participants in tenders and distinguishes between suppliers and non-suppliers " +
    "(companies that have participated but not yet won). To view information about the tenders related to each participant, simply click on the icon.";

function IconMap({
                     fetchData,
                     addFiltersToPath,
                     filterLocations,
                     filterAuthorities,
                     iconMapping = '/data/location-icon-mapping.json',
                     mapStyle,
                     changePageToInfo,
                     viewState,
                     setViewState
                 }) {
    const [suppliersData, setSuppliersData] = useState([]);
    const [nonSuppliersData, setNonSuppliersData] = useState([]);
    const [selectedIconData, setSelectedIconData] = useState({});
    const [curCompanyIndex, setCurCompanyIndex] = useState(0); //index of current company in tooltip
    const [showLayers, setShowLayers] = useState({suppliers: true, nonSuppliers: true});
    const [hoveredLayerId, setHoveredLayerId] = useState(null);

    useEffect(() => {
        fetchData(addFiltersToPath(COMPANIES_PATH, {"hasExactAddress": true, "isSupplier": true}), setSuppliersData);
        fetchData(addFiltersToPath(COMPANIES_PATH, {
            "hasExactAddress": true,
            "isSupplier": false
        }), setNonSuppliersData);
    }, [filterLocations, filterAuthorities, addFiltersToPath, fetchData])

    const renderTooltip = info => {
        const {object, x, y} = info
        if (!info.object) {
            return null;
        }

        if (info.objects) {
            return (
                <Tooltip companyName={info.objects[curCompanyIndex].name}
                         companyId={info.objects[curCompanyIndex].id}
                         organisationId={info.objects[curCompanyIndex].organisationId}
                         x={x}
                         y={y}
                         fetchData={fetchData}
                         addFiltersToPath={addFiltersToPath}
                         layerId={info.layer.id}
                         curCompanyIndex={curCompanyIndex}
                         setCurCompanyIndex={setCurCompanyIndex}
                         totalNumberOfCompanies={info.objects.length}>
                </Tooltip>
            )
        }
        return (
            <Tooltip companyName={object.name}
                     companyId={object.id}
                     organisationId={object.organisationId}
                     x={x}
                     y={y}
                     fetchData={fetchData}
                     addFiltersToPath={addFiltersToPath}
                     layerId={info.layer.id}
                     curCompanyIndex={curCompanyIndex}
                     setCurCompanyIndex={setCurCompanyIndex}
                     totalNumberOfCompanies={1}>
            </Tooltip>

        );
    }


    const expandTooltip = info => {
        if (info.picked) {
            setSelectedIconData(info);
        } else {
            setSelectedIconData({});
        }
    }

    const handleHover = info => {
        if (info && info.object) {
            setHoveredLayerId(info.layer.id);
        } else {
            setHoveredLayerId(null);
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

    // Rearrange layers based on hover state to ensure hovered layer is rendered on top
    if (hoveredLayerId) {
        const hoveredLayerIndex = layers.findIndex(layer => layer.id === hoveredLayerId);
        if (hoveredLayerIndex > -1) {
            const [hoveredLayer] = layers.splice(hoveredLayerIndex, 1);
            layers.push(hoveredLayer);
        }
    }

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
                controller={{dragRotate: false}}
                onViewStateChange={(e) => {
                    setSelectedIconData({})
                    setViewState(e.viewState)
                }}
                viewState={viewState}
                onClick={expandTooltip}
                onHover={handleHover}
            >
                <Map reuseMaps mapLib={maplibregl} mapStyle={mapStyle} preventStyleDiffing={true}/>
            </DeckGL>
            <Legend
                title="Icon map"
                text={LEGEND_TEXT}
                changePageToInfo={changePageToInfo}>
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
            {renderTooltip(selectedIconData)}
        </div>
    );
}

export default IconMap;