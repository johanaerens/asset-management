import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAssets } from 'app/entities/asset/asset.reducer';
import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { createEntity, getEntity, reset, updateEntity } from './asset-history.reducer';

export const AssetHistoryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const assets = useAppSelector(state => state.asset.entities);
  const employees = useAppSelector(state => state.employee.entities);
  const assetHistoryEntity = useAppSelector(state => state.assetHistory.entity);
  const loading = useAppSelector(state => state.assetHistory.loading);
  const updating = useAppSelector(state => state.assetHistory.updating);
  const updateSuccess = useAppSelector(state => state.assetHistory.updateSuccess);

  const handleClose = () => {
    navigate('/asset-history');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAssets({}));
    dispatch(getEmployees({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.startDate = convertDateTimeToServer(values.startDate);
    values.endDate = convertDateTimeToServer(values.endDate);

    const entity = {
      ...assetHistoryEntity,
      ...values,
      asset: assets.find(it => it.id.toString() === values.asset?.toString()),
      employee: employees.find(it => it.id.toString() === values.employee?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          startDate: displayDefaultDateTime(),
          endDate: displayDefaultDateTime(),
        }
      : {
          ...assetHistoryEntity,
          startDate: convertDateTimeFromServer(assetHistoryEntity.startDate),
          endDate: convertDateTimeFromServer(assetHistoryEntity.endDate),
          asset: assetHistoryEntity?.asset?.id,
          employee: assetHistoryEntity?.employee?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="assetManagementApp.assetHistory.home.createOrEditLabel" data-cy="AssetHistoryCreateUpdateHeading">
            Create or edit a Asset History
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="asset-history-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Start Date"
                id="asset-history-startDate"
                name="startDate"
                data-cy="startDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="End Date"
                id="asset-history-endDate"
                name="endDate"
                data-cy="endDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="asset-history-asset" name="asset" data-cy="asset" label="Asset" type="select">
                <option value="" key="0" />
                {assets
                  ? assets.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="asset-history-employee" name="employee" data-cy="employee" label="Employee" type="select">
                <option value="" key="0" />
                {employees
                  ? employees.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/asset-history" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default AssetHistoryUpdate;
