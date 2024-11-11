package be.johanaerens.domain;

import static be.johanaerens.domain.AssetHistoryTestSamples.*;
import static be.johanaerens.domain.AssetTestSamples.*;
import static be.johanaerens.domain.EmployeeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import be.johanaerens.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = getEmployeeSample1();
        Employee employee2 = new Employee();
        assertThat(employee1).isNotEqualTo(employee2);

        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);

        employee2 = getEmployeeSample2();
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    void assetTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        Asset assetBack = getAssetRandomSampleGenerator();

        employee.addAsset(assetBack);
        assertThat(employee.getAssets()).containsOnly(assetBack);
        assertThat(assetBack.getEmployee()).isEqualTo(employee);

        employee.removeAsset(assetBack);
        assertThat(employee.getAssets()).doesNotContain(assetBack);
        assertThat(assetBack.getEmployee()).isNull();

        employee.assets(new HashSet<>(Set.of(assetBack)));
        assertThat(employee.getAssets()).containsOnly(assetBack);
        assertThat(assetBack.getEmployee()).isEqualTo(employee);

        employee.setAssets(new HashSet<>());
        assertThat(employee.getAssets()).doesNotContain(assetBack);
        assertThat(assetBack.getEmployee()).isNull();
    }

    @Test
    void assetHistoryTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        AssetHistory assetHistoryBack = getAssetHistoryRandomSampleGenerator();

        employee.setAssetHistory(assetHistoryBack);
        assertThat(employee.getAssetHistory()).isEqualTo(assetHistoryBack);
        assertThat(assetHistoryBack.getEmployee()).isEqualTo(employee);

        employee.assetHistory(null);
        assertThat(employee.getAssetHistory()).isNull();
        assertThat(assetHistoryBack.getEmployee()).isNull();
    }
}
