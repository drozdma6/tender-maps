import {useState} from 'react';
import {Map} from 'react-map-gl';
import maplibregl from 'maplibre-gl';
import DeckGL from '@deck.gl/react';
import {MapView} from '@deck.gl/core';
import {IconLayer} from '@deck.gl/layers';

import IconClusterLayer from './icon-cluster-layer';

const DATA_URL =
    'http://localhost:8081/procurements/exact-address-supplier';

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

function renderTooltip(info) {
    const {object, x, y} = info;

    if (info.objects) {
        return (
            <div className="tooltip interactive" style={{left: x, top: y}}>
                {info.objects.map(({name, year, mass, class: meteorClass}) => {
                    return (
                        <div key={name}>
                            <h5>{name}</h5>
                            <div>Year: {year || 'unknown'}</div>
                            <div>Class: {meteorClass}</div>
                            <div>Mass: {mass}g</div>
                        </div>
                    );
                })}
            </div>
        );
    }

    if (!object) {
        return null;
    }

    return object.cluster ? (
        <div className="tooltip" style={{left: x, top: y}}>
            {object.point_count} records
        </div>
    ) : (
        <div className="tooltip" style={{left: x, top: y}}>
            {object.name} {object.year ? `(${object.year})` : ''}
        </div>
    );
}

/* eslint-disable react/no-deprecated */
function IconMap({
                                data = DATA_URL,
                                iconMapping = '/data/location-icon-mapping.json',
                                iconAtlas = '/data/location-icon-atlas.png',
                                showCluster = true,
                                mapStyle = MAP_STYLE
                            }) {
    const [hoverInfo, setHoverInfo] = useState({});

    const hideTooltip = () => {
        setHoverInfo({});
    };
    const expandTooltip = info => {
        if (info.picked && showCluster) {
            setHoverInfo(info);
        } else {
            setHoverInfo({});
        }
    };

    const layerProps = {
        data,
        pickable: true,
        getPosition: d => [d.supplier.addressDto.longitude, d.supplier.addressDto.latitude],
        iconAtlas,
        iconMapping,
        onHover: !hoverInfo.objects && setHoverInfo
    };

    const layer = showCluster
        ? new IconClusterLayer({...layerProps, id: 'icon-cluster', sizeScale: 40})
        : new IconLayer({
            ...layerProps,
            id: 'icon',
            getIcon: 'marker',
            sizeUnits: 'meters',
            sizeScale: 2000,
            sizeMinPixels: 6
        });

    return (
        <DeckGL
            layers={[layer]}
            views={MAP_VIEW}
            initialViewState={INITIAL_VIEW_STATE}
            controller={{dragRotate: false}}
            onViewStateChange={hideTooltip}
            onClick={expandTooltip}
        >
            <Map reuseMaps mapLib={maplibregl} mapStyle={mapStyle} preventStyleDiffing={true} />

            {renderTooltip(hoverInfo)}
        </DeckGL>
    );
}

export default IconMap;
