package be.johanaerens.web.rest;

import static be.johanaerens.domain.AssetHistoryAsserts.*;
import static be.johanaerens.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.johanaerens.IntegrationTest;
import be.johanaerens.domain.AssetHistory;
import be.johanaerens.repository.AssetHistoryRepository;
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
 * Integration tests for the {@link AssetHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AssetHistoryResourceIT {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/asset-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssetHistoryMockMvc;

    private AssetHistory assetHistory;

    private AssetHistory insertedAssetHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetHistory createEntity() {
        return new AssetHistory().startDate(DEFAULT_START_DATE).endDate(DEFAULT_END_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetHistory createUpdatedEntity() {
        return new AssetHistory().startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);
    }

    @BeforeEach
    public void initTest() {
        assetHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAssetHistory != null) {
            assetHistoryRepository.delete(insertedAssetHistory);
            insertedAssetHistory = null;
        }
    }

    @Test
    @Transactional
    void createAssetHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssetHistory
        var returnedAssetHistory = om.readValue(
            restAssetHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetHistory)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssetHistory.class
        );

        // Validate the AssetHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAssetHistoryUpdatableFieldsEquals(returnedAssetHistory, getPersistedAssetHistory(returnedAssetHistory));

        insertedAssetHistory = returnedAssetHistory;
    }

    @Test
    @Transactional
    void createAssetHistoryWithExistingId() throws Exception {
        // Create the AssetHistory with an existing ID
        assetHistory.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssetHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetHistory)))
            .andExpect(status().isBadRequest());

        // Validate the AssetHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAssetHistories() throws Exception {
        // Initialize the database
        insertedAssetHistory = assetHistoryRepository.saveAndFlush(assetHistory);

        // Get all the assetHistoryList
        restAssetHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    void getAssetHistory() throws Exception {
        // Initialize the database
        insertedAssetHistory = assetHistoryRepository.saveAndFlush(assetHistory);

        // Get the assetHistory
        restAssetHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, assetHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assetHistory.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAssetHistory() throws Exception {
        // Get the assetHistory
        restAssetHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssetHistory() throws Exception {
        // Initialize the database
        insertedAssetHistory = assetHistoryRepository.saveAndFlush(assetHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetHistory
        AssetHistory updatedAssetHistory = assetHistoryRepository.findById(assetHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssetHistory are not directly saved in db
        em.detach(updatedAssetHistory);
        updatedAssetHistory.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restAssetHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAssetHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAssetHistory))
            )
            .andExpect(status().isOk());

        // Validate the AssetHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssetHistoryToMatchAllProperties(updatedAssetHistory);
    }

    @Test
    @Transactional
    void putNonExistingAssetHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssetHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssetHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssetHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedAssetHistory = assetHistoryRepository.saveAndFlush(assetHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetHistory using partial update
        AssetHistory partialUpdatedAssetHistory = new AssetHistory();
        partialUpdatedAssetHistory.setId(assetHistory.getId());

        partialUpdatedAssetHistory.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restAssetHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetHistory))
            )
            .andExpect(status().isOk());

        // Validate the AssetHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssetHistory, assetHistory),
            getPersistedAssetHistory(assetHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssetHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedAssetHistory = assetHistoryRepository.saveAndFlush(assetHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetHistory using partial update
        AssetHistory partialUpdatedAssetHistory = new AssetHistory();
        partialUpdatedAssetHistory.setId(assetHistory.getId());

        partialUpdatedAssetHistory.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restAssetHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetHistory))
            )
            .andExpect(status().isOk());

        // Validate the AssetHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetHistoryUpdatableFieldsEquals(partialUpdatedAssetHistory, getPersistedAssetHistory(partialUpdatedAssetHistory));
    }

    @Test
    @Transactional
    void patchNonExistingAssetHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assetHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssetHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssetHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assetHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssetHistory() throws Exception {
        // Initialize the database
        insertedAssetHistory = assetHistoryRepository.saveAndFlush(assetHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assetHistory
        restAssetHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, assetHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assetHistoryRepository.count();
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

    protected AssetHistory getPersistedAssetHistory(AssetHistory assetHistory) {
        return assetHistoryRepository.findById(assetHistory.getId()).orElseThrow();
    }

    protected void assertPersistedAssetHistoryToMatchAllProperties(AssetHistory expectedAssetHistory) {
        assertAssetHistoryAllPropertiesEquals(expectedAssetHistory, getPersistedAssetHistory(expectedAssetHistory));
    }

    protected void assertPersistedAssetHistoryToMatchUpdatableProperties(AssetHistory expectedAssetHistory) {
        assertAssetHistoryAllUpdatablePropertiesEquals(expectedAssetHistory, getPersistedAssetHistory(expectedAssetHistory));
    }
}
