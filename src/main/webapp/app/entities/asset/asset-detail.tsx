import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './asset.reducer';

export const AssetDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const assetEntity = useAppSelector(state => state.asset.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assetDetailsHeading">Asset</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{assetEntity.id}</dd>
          <dt>
            <span id="number">Number</span>
          </dt>
          <dd>{assetEntity.number}</dd>
          <dt>
            <span id="brand">Brand</span>
          </dt>
          <dd>{assetEntity.brand}</dd>
          <dt>
            <span id="model">Model</span>
          </dt>
          <dd>{assetEntity.model}</dd>
          <dt>
            <span id="serialNumber">Serial Number</span>
          </dt>
          <dd>{assetEntity.serialNumber}</dd>
          <dt>
            <span id="purchaseDate">Purchase Date</span>
          </dt>
          <dd>{assetEntity.purchaseDate ? <TextFormat value={assetEntity.purchaseDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="warantDate">Warant Date</span>
          </dt>
          <dd>{assetEntity.warantDate ? <TextFormat value={assetEntity.warantDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="comments">Comments</span>
          </dt>
          <dd>{assetEntity.comments}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{assetEntity.status}</dd>
          <dt>Employee</dt>
          <dd>{assetEntity.employee ? assetEntity.employee.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/asset" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/asset/${assetEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssetDetail;
