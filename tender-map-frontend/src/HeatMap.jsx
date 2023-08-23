import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import {HeatmapLayer} from '@deck.gl/aggregation-layers';
import DeckGL from '@deck.gl/react';
import Legend from "./Legend.jsx";

const INITIAL_VIEW_STATE = {
    longitude: 15.301806,
    latitude: 49.868280,
    zoom: 6.6,
    maxZoom: 16,
    pitch: 0,
    bearing: 0
};

const MAP_STYLE = 'https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json';

const DATA_URL =
    'http://localhost:8081/procurements/suppliers/exact-address';

function HeatMap({
                     buildDataUrl,
                     mapStyle = MAP_STYLE,
                     intensity = 1,
                     threshold = 0.03,
                     radiusPixels = 30,
                 }) {

    // Sample data for the legend items
    const legendItems = [
        {color: '#ffffae', label: 'Low number of tenders'},
        {color: '#bf0020', label: 'High number of tenders'},
        // Add more legend items as needed
    ];

    const layers = [
        new HeatmapLayer({
            data: buildDataUrl(DATA_URL),
            id: 'heatmp-layer',
            pickable: false,
            getPosition: d => [d.supplier.address.longitude, d.supplier.address.latitude],
            getWeight: d => d.contractPrice,
            radiusPixels,
            intensity,
            threshold
        })
    ];
    return (
        <div>
            <DeckGL
                initialViewState={INITIAL_VIEW_STATE}
                controller={true}
                layers={layers}>
                <Map reuseMaps mapLib={maplibregl} mapStyle={mapStyle} preventStyleDiffing={true}/>
            </DeckGL>
            <Legend title="Public Procurements"
                    text="Distribution of public procurement contracts."
                    items={legendItems}
            >
                <div style={{display: 'flex', alignItems: 'center', marginTop: '8px'}}>
                    <div style={{
                        flex: 1,
                        height: '16px',
                        borderRadius: '8px',
                        background: 'linear-gradient(to right, #ffffae, #bf0020)'
                    }}></div>
                </div>
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    marginTop: '8px',
                    fontSize: '14px',
                    fontWeight: 'bold'
                }}>
                    <span>Low</span>
                    <span>High</span>
                </div>
            </Legend>
        </div>

    )
}

export default HeatMap;