import React from 'react';
import { Route } from 'react-router-dom';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Asset from './asset';
import AssetHistory from './asset-history';
import Employee from './employee';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="asset/*" element={<Asset />} />
        <Route path="asset-history/*" element={<AssetHistory />} />
        <Route path="employee/*" element={<Employee />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
