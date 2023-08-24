import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import {AmbientLight, PointLight, LightingEffect} from '@deck.gl/core';
import {HexagonLayer} from '@deck.gl/aggregation-layers';
import DeckGL from '@deck.gl/react';
import Legend from "./Legend.jsx";


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

const MAP_STYLE = 'https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json';

const DATA_URL =
    'http://localhost:8081/procurements/suppliers/exact-address';

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
    const lat = object.position[1];
    const lng = object.position[0];
    const companiesCount = object.points.length;
    const totalContractPrice = object.colorValue; //colorValue represents sum of all tenders contract prices

    return `\
    latitude: ${Number.isFinite(lat) ? lat.toFixed(6) : ''}
    longitude: ${Number.isFinite(lng) ? lng.toFixed(6) : ''}
    ${companiesCount} Companies
    ${totalContractPrice} CZK Tenders price`;
}

/* eslint-disable react/no-deprecated */
function HexagonMap({
                        buildDataUrl,
                        mapStyle = MAP_STYLE,
                        radius = 1000,
                        upperPercentile = 100,
                        coverage = 1
                    }) {
    const layers = [
        new HexagonLayer({
            id: 'heatmap',
            colorRange,
            coverage,
            data: buildDataUrl(DATA_URL),
            elevationRange: [0, 3000],
            elevationScale: buildDataUrl(DATA_URL) && buildDataUrl(DATA_URL).length ? 50 : 0,
            extruded: true,
            getPosition: d => [d.supplier.address.longitude, d.supplier.address.latitude],
            getElevationWeight: d => d.contractPrice,
            getColorWeight: d => d.contractPrice,
            pickable: true,
            radius,
            upperPercentile,
            material,

            transitions: {
                elevationScale: 3000
            }
        })
    ];
    const legendItems = [
        {color: '#1896bb', label: 'Low number of tenders'},
        {color: '#4de1ce', label: 'Moderate number of tenders'},
        {color: '#d7feb9', label: 'High number of tenders'},
        {color: '#feeeb6', label: 'Very high number of tenders'},
        {color: '#fcb060', label: 'Extremely high number of tenders'},
        {color: '#d04152', label: 'Highest number of tenders'},
        // Add more legend items as needed
    ];

    return (
        <div>
            <DeckGL
                layers={layers}
                effects={[lightingEffect]}
                initialViewState={INITIAL_VIEW_STATE}
                controller={true}
                getTooltip={getTooltip}
            >
                <Map reuseMaps mapLib={maplibregl} mapStyle={mapStyle} preventStyleDiffing={true}/>
            </DeckGL>
            <Legend
                title="Public Procurements"
                text="Distribution of public procurement contracts."
                items={legendItems}>
                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    marginTop: '8px'
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
                    marginTop: '8px',
                    fontSize: '14px',
                    fontWeight: 'bold'
                }}>
                    <span>More Tenders</span>
                    <span>Fewer Tenders</span>
                </div>
            </Legend>
        </div>
    );
}

export default HexagonMap;

