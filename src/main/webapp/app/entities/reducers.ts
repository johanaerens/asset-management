import asset from 'app/entities/asset/asset.reducer';
import assetHistory from 'app/entities/asset-history/asset-history.reducer';
import employee from 'app/entities/employee/employee.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  asset,
  assetHistory,
  employee,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
