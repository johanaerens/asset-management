import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { Language } from 'app/shared/model/enumerations/language.model';
import { createEntity, getEntity, reset, updateEntity } from './employee.reducer';

export const EmployeeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const employeeEntity = useAppSelector(state => state.employee.entity);
  const loading = useAppSelector(state => state.employee.loading);
  const updating = useAppSelector(state => state.employee.updating);
  const updateSuccess = useAppSelector(state => state.employee.updateSuccess);
  const languageValues = Object.keys(Language);

  const handleClose = () => {
    navigate('/employee');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    values.hireDate = convertDateTimeToServer(values.hireDate);

    const entity = {
      ...employeeEntity,
      ...values,
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
          hireDate: displayDefaultDateTime(),
        }
      : {
          language: 'FRENCH',
          ...employeeEntity,
          hireDate: convertDateTimeFromServer(employeeEntity.hireDate),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="assetManagementApp.employee.home.createOrEditLabel" data-cy="EmployeeCreateUpdateHeading">
            Create or edit a Employee
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="employee-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="First Name" id="employee-firstName" name="firstName" data-cy="firstName" type="text" />
              <ValidatedField label="Last Name" id="employee-lastName" name="lastName" data-cy="lastName" type="text" />
              <ValidatedField label="Email" id="employee-email" name="email" data-cy="email" type="text" />
              <ValidatedField
                label="Employee Number"
                id="employee-employeeNumber"
                name="employeeNumber"
                data-cy="employeeNumber"
                type="text"
              />
              <ValidatedField label="Phone Number" id="employee-phoneNumber" name="phoneNumber" data-cy="phoneNumber" type="text" />
              <ValidatedField
                label="Hire Date"
                id="employee-hireDate"
                name="hireDate"
                data-cy="hireDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Language" id="employee-language" name="language" data-cy="language" type="select">
                {languageValues.map(language => (
                  <option value={language} key={language}>
                    {language}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/employee" replace color="info">
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

export default EmployeeUpdate;
