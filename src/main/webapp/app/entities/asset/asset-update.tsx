import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { Status } from 'app/shared/model/enumerations/status.model';
import { createEntity, getEntity, reset, updateEntity } from './asset.reducer';

export const AssetUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const employees = useAppSelector(state => state.employee.entities);
  const assetEntity = useAppSelector(state => state.asset.entity);
  const loading = useAppSelector(state => state.asset.loading);
  const updating = useAppSelector(state => state.asset.updating);
  const updateSuccess = useAppSelector(state => state.asset.updateSuccess);
  const statusValues = Object.keys(Status);

  const handleClose = () => {
    navigate('/asset');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    values.purchaseDate = convertDateTimeToServer(values.purchaseDate);
    values.warantDate = convertDateTimeToServer(values.warantDate);

    const entity = {
      ...assetEntity,
      ...values,
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
          purchaseDate: displayDefaultDateTime(),
          warantDate: displayDefaultDateTime(),
        }
      : {
          status: 'IN_USE',
          ...assetEntity,
          purchaseDate: convertDateTimeFromServer(assetEntity.purchaseDate),
          warantDate: convertDateTimeFromServer(assetEntity.warantDate),
          employee: assetEntity?.employee?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="assetManagementApp.asset.home.createOrEditLabel" data-cy="AssetCreateUpdateHeading">
            Create or edit a Asset
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="asset-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Number" id="asset-number" name="number" data-cy="number" type="text" />
              <ValidatedField label="Brand" id="asset-brand" name="brand" data-cy="brand" type="text" />
              <ValidatedField label="Model" id="asset-model" name="model" data-cy="model" type="text" />
              <ValidatedField label="Serial Number" id="asset-serialNumber" name="serialNumber" data-cy="serialNumber" type="text" />
              <ValidatedField
                label="Purchase Date"
                id="asset-purchaseDate"
                name="purchaseDate"
                data-cy="purchaseDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Warant Date"
                id="asset-warantDate"
                name="warantDate"
                data-cy="warantDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Comments" id="asset-comments" name="comments" data-cy="comments" type="text" />
              <ValidatedField label="Status" id="asset-status" name="status" data-cy="status" type="select">
                {statusValues.map(status => (
                  <option value={status} key={status}>
                    {status}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="asset-employee" name="employee" data-cy="employee" label="Employee" type="select">
                <option value="" key="0" />
                {employees
                  ? employees.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/asset" replace color="info">
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

export default AssetUpdate;
