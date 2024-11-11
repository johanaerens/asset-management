import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AssetHistory from './asset-history';
import AssetHistoryDetail from './asset-history-detail';
import AssetHistoryUpdate from './asset-history-update';
import AssetHistoryDeleteDialog from './asset-history-delete-dialog';

const AssetHistoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AssetHistory />} />
    <Route path="new" element={<AssetHistoryUpdate />} />
    <Route path=":id">
      <Route index element={<AssetHistoryDetail />} />
      <Route path="edit" element={<AssetHistoryUpdate />} />
      <Route path="delete" element={<AssetHistoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AssetHistoryRoutes;
