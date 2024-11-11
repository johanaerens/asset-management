package be.johanaerens.repository;

import be.johanaerens.domain.Asset;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Asset entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {}
