package be.johanaerens.web.rest;

import be.johanaerens.domain.AssetHistory;
import be.johanaerens.repository.AssetHistoryRepository;
import be.johanaerens.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link be.johanaerens.domain.AssetHistory}.
 */
@RestController
@RequestMapping("/api/asset-histories")
@Transactional
public class AssetHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetHistoryResource.class);

    private static final String ENTITY_NAME = "assetHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssetHistoryRepository assetHistoryRepository;

    public AssetHistoryResource(AssetHistoryRepository assetHistoryRepository) {
        this.assetHistoryRepository = assetHistoryRepository;
    }

    /**
     * {@code POST  /asset-histories} : Create a new assetHistory.
     *
     * @param assetHistory the assetHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assetHistory, or with status {@code 400 (Bad Request)} if the assetHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssetHistory> createAssetHistory(@RequestBody AssetHistory assetHistory) throws URISyntaxException {
        LOG.debug("REST request to save AssetHistory : {}", assetHistory);
        if (assetHistory.getId() != null) {
            throw new BadRequestAlertException("A new assetHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assetHistory = assetHistoryRepository.save(assetHistory);
        return ResponseEntity.created(new URI("/api/asset-histories/" + assetHistory.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, assetHistory.getId().toString()))
            .body(assetHistory);
    }

    /**
     * {@code PUT  /asset-histories/:id} : Updates an existing assetHistory.
     *
     * @param id the id of the assetHistory to save.
     * @param assetHistory the assetHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetHistory,
     * or with status {@code 400 (Bad Request)} if the assetHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assetHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssetHistory> updateAssetHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AssetHistory assetHistory
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssetHistory : {}, {}", id, assetHistory);
        if (assetHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assetHistory = assetHistoryRepository.save(assetHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, assetHistory.getId().toString()))
            .body(assetHistory);
    }

    /**
     * {@code PATCH  /asset-histories/:id} : Partial updates given fields of an existing assetHistory, field will ignore if it is null
     *
     * @param id the id of the assetHistory to save.
     * @param assetHistory the assetHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetHistory,
     * or with status {@code 400 (Bad Request)} if the assetHistory is not valid,
     * or with status {@code 404 (Not Found)} if the assetHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the assetHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssetHistory> partialUpdateAssetHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AssetHistory assetHistory
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AssetHistory partially : {}, {}", id, assetHistory);
        if (assetHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssetHistory> result = assetHistoryRepository
            .findById(assetHistory.getId())
            .map(existingAssetHistory -> {
                if (assetHistory.getStartDate() != null) {
                    existingAssetHistory.setStartDate(assetHistory.getStartDate());
                }
                if (assetHistory.getEndDate() != null) {
                    existingAssetHistory.setEndDate(assetHistory.getEndDate());
                }

                return existingAssetHistory;
            })
            .map(assetHistoryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, assetHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /asset-histories} : get all the assetHistories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assetHistories in body.
     */
    @GetMapping("")
    public List<AssetHistory> getAllAssetHistories() {
        LOG.debug("REST request to get all AssetHistories");
        return assetHistoryRepository.findAll();
    }

    /**
     * {@code GET  /asset-histories/:id} : get the "id" assetHistory.
     *
     * @param id the id of the assetHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assetHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetHistory> getAssetHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssetHistory : {}", id);
        Optional<AssetHistory> assetHistory = assetHistoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(assetHistory);
    }

    /**
     * {@code DELETE  /asset-histories/:id} : delete the "id" assetHistory.
     *
     * @param id the id of the assetHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssetHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssetHistory : {}", id);
        assetHistoryRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
