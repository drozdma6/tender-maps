import React from 'react';
import ReactDOM from 'react-dom';
import App from './App.jsx';
import {createInstance, MatomoProvider} from "@jonkoops/matomo-tracker-react";

const instance = createInstance({
    urlBase: 'https://matomo.opendatalab.cz/',
    siteId: 7,
    linkTracking: false
});

ReactDOM.render(
    <React.StrictMode>
        <MatomoProvider value={instance}>
            <App />
        </MatomoProvider>
    </React.StrictMode>,
    document.getElementById('root')
);