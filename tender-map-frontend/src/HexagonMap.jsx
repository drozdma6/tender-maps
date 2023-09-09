import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import {AmbientLight, PointLight, LightingEffect} from '@deck.gl/core';
import {HexagonLayer} from '@deck.gl/aggregation-layers';
import DeckGL from '@deck.gl/react';
import Legend from "./Legend.jsx";
import {Slider, Switch, Typography} from "@mui/material";
import {useEffect, useState} from "react";
import Box from "@mui/material/Box";

const ambientLight = new AmbientLight({
    color: [255, 255, 255],
    intensity: 1.0
});

const pointLight1 = new PointLight({
    color: [255, 255, 255],
    intensity: 0.8,
    position: [-0.144528, 49.739968, 80000]
});

const pointLight2 = new PointLight({
    color: [255, 255, 255],
    intensity: 0.8,
    position: [-3.807751, 54.104682, 8000]
});

const lightingEffect = new LightingEffect({ambientLight, pointLight1, pointLight2});

const material = {
    ambient: 0.64,
    diffuse: 0.6,
    shininess: 32,
    specularColor: [51, 51, 51]
};

const INITIAL_VIEW_STATE = {
    longitude: 15.301806,
    latitude: 49.868280,
    zoom: 6.6,
    maxZoom: 16,
    pitch: 25,
    bearing: 0
};

const DATA_PATH = '/procurements';

const colorRange = [
    [1, 152, 189],
    [73, 227, 206],
    [216, 254, 181],
    [254, 237, 177],
    [254, 173, 84],
    [209, 55, 78]
];

function getTooltip({object}) {
    if (!object) {
        return null;
    }
    const companiesCount = object.points.length;
    const totalContractPrice = object.colorValue; //colorValue represents sum of all tenders contract prices

    return `\
    ${companiesCount} Companies
    ${totalContractPrice} CZK total price`;
}

function HexagonMap({
                        fetchData,
                        addFiltersToPath,
                        filterLocations,
                        filterAuthorities,
                        mapStyle,
                        upperPercentile = 100,
                        coverage = 1
                    }) {
    const [sliderValue, setSliderValue] = useState(1000);
    const [data, setData] = useState([]);
    const [showNumOfTendersByElevation, setShowNumOfTendersByElevation] = useState(false);

    useEffect(() => {
        fetchData(addFiltersToPath(DATA_PATH, {"supplierHasExactAddress": true}), setData);
    }, [filterLocations, filterAuthorities]);

    function handleElevationSwitchChange() {
        setShowNumOfTendersByElevation(!showNumOfTendersByElevation);
    }

    const commonLayerConfig = {
        colorRange,
        coverage,
        data,
        elevationRange: [0, 3000],
        elevationScale: data && data.length ? 50 : 0,
        extruded: true,
        getPosition: d => [d.supplier.address.longitude, d.supplier.address.latitude],
        pickable: true,
        radius: sliderValue,
        upperPercentile,
        material,
        transitions: {
            elevationScale: 3000
        }
    };

    const layer = showNumOfTendersByElevation
        ? new HexagonLayer({
            ...commonLayerConfig,
            id: 'heatmap1',
            getColorWeight: d => d.contractPrice,
        })
        : new HexagonLayer({
            ...commonLayerConfig,
            id: 'heatmap2',
            getElevationWeight: d => d.contractPrice,
            getColorWeight: d => d.contractPrice,
        });
    return (
        <div>
            <DeckGL
                layers={layer}
                effects={[lightingEffect]}
                initialViewState={INITIAL_VIEW_STATE}
                controller={true}
                getTooltip={getTooltip}
            >
                <Map reuseMaps mapLib={maplibregl} mapStyle={mapStyle} preventStyleDiffing={true}/>
            </DeckGL>
            <Legend
                title="Hexagon map"
                text="Distribution of suppliers and price of their tenders. Both color and elevation of hexagon show cumulative price of tenders. Elevation can show number of companies in hexagon."
            >
                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    marginTop: '12px'
                }}>
                    <div style={{
                        flex: 1,
                        height: '16px',
                        borderRadius: '8px',
                        background: 'linear-gradient(to right, #1896bb, #4de1ce, #d7feb9, #feeeb6, #fcb060, #d04152)'
                    }}>
                    </div>
                </div>
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    marginTop: '6px',
                    fontSize: '14px',
                    fontWeight: 'bold'
                }}>
                    <span>Lower total price</span>
                    <span>Higher total price</span>
                </div>

                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    marginTop: '12px'
                }}>
                    <Typography variant="body1"> Radius </Typography>
                    <Slider style={{width: '50%'}}
                            defaultValue={1000}
                            step={100}
                            min={100}
                            max={5500}
                            aria-label="Radius"
                            valueLabelFormat={(value) => `Radius: ${value}`}
                            valueLabelDisplay="auto"
                            onChange={(event, newValue) => {
                                setSliderValue(newValue);
                            }}/>
                </div>
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="body1" style={{flex: 1}}>
                        Show number of companies by elevation
                    </Typography>
                    <Switch checked={showNumOfTendersByElevation} onChange={handleElevationSwitchChange}
                            style={{flexShrink: 0}}/>
                </Box>
            </Legend>
        </div>
    );
}

export default HexagonMap;

