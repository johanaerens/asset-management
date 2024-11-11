package be.johanaerens.domain;

import static be.johanaerens.domain.AssetHistoryTestSamples.*;
import static be.johanaerens.domain.AssetTestSamples.*;
import static be.johanaerens.domain.EmployeeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import be.johanaerens.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Asset.class);
        Asset asset1 = getAssetSample1();
        Asset asset2 = new Asset();
        assertThat(asset1).isNotEqualTo(asset2);

        asset2.setId(asset1.getId());
        assertThat(asset1).isEqualTo(asset2);

        asset2 = getAssetSample2();
        assertThat(asset1).isNotEqualTo(asset2);
    }

    @Test
    void employeeTest() {
        Asset asset = getAssetRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        asset.setEmployee(employeeBack);
        assertThat(asset.getEmployee()).isEqualTo(employeeBack);

        asset.employee(null);
        assertThat(asset.getEmployee()).isNull();
    }

    @Test
    void assetHistoryTest() {
        Asset asset = getAssetRandomSampleGenerator();
        AssetHistory assetHistoryBack = getAssetHistoryRandomSampleGenerator();

        asset.setAssetHistory(assetHistoryBack);
        assertThat(asset.getAssetHistory()).isEqualTo(assetHistoryBack);
        assertThat(assetHistoryBack.getAsset()).isEqualTo(asset);

        asset.assetHistory(null);
        assertThat(asset.getAssetHistory()).isNull();
        assertThat(assetHistoryBack.getAsset()).isNull();
    }
}
