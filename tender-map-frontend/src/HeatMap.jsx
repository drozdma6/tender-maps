import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import {HeatmapLayer} from '@deck.gl/aggregation-layers';
import DeckGL from '@deck.gl/react';
import Legend from "./Legend.jsx";
import {useEffect, useState} from "react";

const INITIAL_VIEW_STATE = {
    longitude: 15.301806,
    latitude: 49.868280,
    zoom: 6.6,
    maxZoom: 16,
    pitch: 0,
    bearing: 0
};

const DATA_PATH = '/procurements';

const LEGEND_TEXT = "This map shows the geographical distribution of suppliers in tenders and uses colours to indicate\n" +
    "                     the total value of contracts won. Darker colours indicate areas of significant economic growth."

function HeatMap({
                     fetchData,
                     addFiltersToPath,
                     filterLocations,
                     filterAuthorities,
                     mapStyle,
                     intensity = 1,
                     threshold = 0.03,
                     radiusPixels = 30,
                     changePageToInfo,
                 }) {
    const [data, setData] = useState([]);

    useEffect(() => {
        fetchData(addFiltersToPath(DATA_PATH, {"supplierHasExactAddress": true}), setData);
    }, [filterLocations, filterAuthorities]);

    const layers = [
        new HeatmapLayer({
            data: data,
            id: 'heatmp-layer',
            pickable: false,
            getPosition: d => [d.supplier.address.longitude, d.supplier.address.latitude],
            getWeight: d => d.contractPrice,
            aggregation: 'SUM',
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
                <Map reuseMaps mapLib={maplibregl} mapStyle={mapStyle}/>
            </DeckGL>
            <Legend title="Heat map"
                    text={LEGEND_TEXT}
                    changePageToInfo={changePageToInfo}
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