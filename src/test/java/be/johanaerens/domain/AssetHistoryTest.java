package be.johanaerens.domain;

import static be.johanaerens.domain.AssetHistoryTestSamples.*;
import static be.johanaerens.domain.AssetTestSamples.*;
import static be.johanaerens.domain.EmployeeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import be.johanaerens.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetHistory.class);
        AssetHistory assetHistory1 = getAssetHistorySample1();
        AssetHistory assetHistory2 = new AssetHistory();
        assertThat(assetHistory1).isNotEqualTo(assetHistory2);

        assetHistory2.setId(assetHistory1.getId());
        assertThat(assetHistory1).isEqualTo(assetHistory2);

        assetHistory2 = getAssetHistorySample2();
        assertThat(assetHistory1).isNotEqualTo(assetHistory2);
    }

    @Test
    void assetTest() {
        AssetHistory assetHistory = getAssetHistoryRandomSampleGenerator();
        Asset assetBack = getAssetRandomSampleGenerator();

        assetHistory.setAsset(assetBack);
        assertThat(assetHistory.getAsset()).isEqualTo(assetBack);

        assetHistory.asset(null);
        assertThat(assetHistory.getAsset()).isNull();
    }

    @Test
    void employeeTest() {
        AssetHistory assetHistory = getAssetHistoryRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        assetHistory.setEmployee(employeeBack);
        assertThat(assetHistory.getEmployee()).isEqualTo(employeeBack);

        assetHistory.employee(null);
        assertThat(assetHistory.getEmployee()).isNull();
    }
}
