import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import {HeatmapLayer} from '@deck.gl/aggregation-layers';
import DeckGL from '@deck.gl/react';


const INITIAL_VIEW_STATE = {
    longitude: 15.301806,
    latitude: 49.868280,
    zoom: 6.6,
    maxZoom: 16,
    pitch: 0,
    bearing: 0
  };

const MAP_STYLE = 'https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json';

function HeatMap({
    data,
    mapStyle = MAP_STYLE,
    intensity = 1,
    threshold = 0.03,
    radiusPixels = 30,}){


    const layers = [
      new HeatmapLayer({
      data,
      id: 'heatmp-layer',
      pickable: false,
      getPosition: d => [d.supplier.addressDto.longitude, d.supplier.addressDto.latitude],
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
            <Map reuseMaps mapLib={maplibregl} mapStyle={mapStyle} preventStyleDiffing={true} />
      </DeckGL>
    </div>

  )
}

export default HeatMap;