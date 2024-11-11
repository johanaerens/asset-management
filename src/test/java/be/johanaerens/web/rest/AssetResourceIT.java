package be.johanaerens.web.rest;

import static be.johanaerens.domain.AssetAsserts.*;
import static be.johanaerens.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.johanaerens.IntegrationTest;
import be.johanaerens.domain.Asset;
import be.johanaerens.domain.enumeration.Status;
import be.johanaerens.repository.AssetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AssetResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AssetResourceIT {

    private static final String DEFAULT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_SERIAL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_PURCHASE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PURCHASE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_WARANT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_WARANT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.IN_USE;
    private static final Status UPDATED_STATUS = Status.SOLD;

    private static final String ENTITY_API_URL = "/api/assets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssetMockMvc;

    private Asset asset;

    private Asset insertedAsset;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Asset createEntity() {
        return new Asset()
            .number(DEFAULT_NUMBER)
            .brand(DEFAULT_BRAND)
            .model(DEFAULT_MODEL)
            .serialNumber(DEFAULT_SERIAL_NUMBER)
            .purchaseDate(DEFAULT_PURCHASE_DATE)
            .warantDate(DEFAULT_WARANT_DATE)
            .comments(DEFAULT_COMMENTS)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Asset createUpdatedEntity() {
        return new Asset()
            .number(UPDATED_NUMBER)
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .purchaseDate(UPDATED_PURCHASE_DATE)
            .warantDate(UPDATED_WARANT_DATE)
            .comments(UPDATED_COMMENTS)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        asset = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAsset != null) {
            assetRepository.delete(insertedAsset);
            insertedAsset = null;
        }
    }

    @Test
    @Transactional
    void createAsset() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Asset
        var returnedAsset = om.readValue(
            restAssetMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asset)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Asset.class
        );

        // Validate the Asset in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAssetUpdatableFieldsEquals(returnedAsset, getPersistedAsset(returnedAsset));

        insertedAsset = returnedAsset;
    }

    @Test
    @Transactional
    void createAssetWithExistingId() throws Exception {
        // Create the Asset with an existing ID
        asset.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asset)))
            .andExpect(status().isBadRequest());

        // Validate the Asset in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAssets() throws Exception {
        // Initialize the database
        insertedAsset = assetRepository.saveAndFlush(asset);

        // Get all the assetList
        restAssetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(asset.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].serialNumber").value(hasItem(DEFAULT_SERIAL_NUMBER)))
            .andExpect(jsonPath("$.[*].purchaseDate").value(hasItem(DEFAULT_PURCHASE_DATE.toString())))
            .andExpect(jsonPath("$.[*].warantDate").value(hasItem(DEFAULT_WARANT_DATE.toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getAsset() throws Exception {
        // Initialize the database
        insertedAsset = assetRepository.saveAndFlush(asset);

        // Get the asset
        restAssetMockMvc
            .perform(get(ENTITY_API_URL_ID, asset.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(asset.getId().intValue()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.serialNumber").value(DEFAULT_SERIAL_NUMBER))
            .andExpect(jsonPath("$.purchaseDate").value(DEFAULT_PURCHASE_DATE.toString()))
            .andExpect(jsonPath("$.warantDate").value(DEFAULT_WARANT_DATE.toString()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAsset() throws Exception {
        // Get the asset
        restAssetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAsset() throws Exception {
        // Initialize the database
        insertedAsset = assetRepository.saveAndFlush(asset);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asset
        Asset updatedAsset = assetRepository.findById(asset.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAsset are not directly saved in db
        em.detach(updatedAsset);
        updatedAsset
            .number(UPDATED_NUMBER)
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .purchaseDate(UPDATED_PURCHASE_DATE)
            .warantDate(UPDATED_WARANT_DATE)
            .comments(UPDATED_COMMENTS)
            .status(UPDATED_STATUS);

        restAssetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAsset.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAsset))
            )
            .andExpect(status().isOk());

        // Validate the Asset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssetToMatchAllProperties(updatedAsset);
    }

    @Test
    @Transactional
    void putNonExistingAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asset.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetMockMvc
            .perform(put(ENTITY_API_URL_ID, asset.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asset)))
            .andExpect(status().isBadRequest());

        // Validate the Asset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asset.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(asset))
            )
            .andExpect(status().isBadRequest());

        // Validate the Asset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asset.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asset)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Asset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssetWithPatch() throws Exception {
        // Initialize the database
        insertedAsset = assetRepository.saveAndFlush(asset);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asset using partial update
        Asset partialUpdatedAsset = new Asset();
        partialUpdatedAsset.setId(asset.getId());

        partialUpdatedAsset
            .number(UPDATED_NUMBER)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .purchaseDate(UPDATED_PURCHASE_DATE)
            .warantDate(UPDATED_WARANT_DATE)
            .comments(UPDATED_COMMENTS);

        restAssetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAsset.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAsset))
            )
            .andExpect(status().isOk());

        // Validate the Asset in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAsset, asset), getPersistedAsset(asset));
    }

    @Test
    @Transactional
    void fullUpdateAssetWithPatch() throws Exception {
        // Initialize the database
        insertedAsset = assetRepository.saveAndFlush(asset);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asset using partial update
        Asset partialUpdatedAsset = new Asset();
        partialUpdatedAsset.setId(asset.getId());

        partialUpdatedAsset
            .number(UPDATED_NUMBER)
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .purchaseDate(UPDATED_PURCHASE_DATE)
            .warantDate(UPDATED_WARANT_DATE)
            .comments(UPDATED_COMMENTS)
            .status(UPDATED_STATUS);

        restAssetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAsset.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAsset))
            )
            .andExpect(status().isOk());

        // Validate the Asset in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetUpdatableFieldsEquals(partialUpdatedAsset, getPersistedAsset(partialUpdatedAsset));
    }

    @Test
    @Transactional
    void patchNonExistingAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asset.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, asset.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(asset))
            )
            .andExpect(status().isBadRequest());

        // Validate the Asset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asset.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(asset))
            )
            .andExpect(status().isBadRequest());

        // Validate the Asset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asset.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(asset)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Asset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAsset() throws Exception {
        // Initialize the database
        insertedAsset = assetRepository.saveAndFlush(asset);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the asset
        restAssetMockMvc
            .perform(delete(ENTITY_API_URL_ID, asset.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assetRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Asset getPersistedAsset(Asset asset) {
        return assetRepository.findById(asset.getId()).orElseThrow();
    }

    protected void assertPersistedAssetToMatchAllProperties(Asset expectedAsset) {
        assertAssetAllPropertiesEquals(expectedAsset, getPersistedAsset(expectedAsset));
    }

    protected void assertPersistedAssetToMatchUpdatableProperties(Asset expectedAsset) {
        assertAssetAllUpdatablePropertiesEquals(expectedAsset, getPersistedAsset(expectedAsset));
    }
}
