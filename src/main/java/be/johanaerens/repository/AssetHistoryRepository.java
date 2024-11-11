package be.johanaerens.repository;

import be.johanaerens.domain.AssetHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AssetHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {}
