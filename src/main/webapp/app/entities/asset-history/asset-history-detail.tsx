import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './asset-history.reducer';

export const AssetHistoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const assetHistoryEntity = useAppSelector(state => state.assetHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assetHistoryDetailsHeading">Asset History</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{assetHistoryEntity.id}</dd>
          <dt>
            <span id="startDate">Start Date</span>
          </dt>
          <dd>
            {assetHistoryEntity.startDate ? <TextFormat value={assetHistoryEntity.startDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endDate">End Date</span>
          </dt>
          <dd>
            {assetHistoryEntity.endDate ? <TextFormat value={assetHistoryEntity.endDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>Asset</dt>
          <dd>{assetHistoryEntity.asset ? assetHistoryEntity.asset.id : ''}</dd>
          <dt>Employee</dt>
          <dd>{assetHistoryEntity.employee ? assetHistoryEntity.employee.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/asset-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/asset-history/${assetHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssetHistoryDetail;
