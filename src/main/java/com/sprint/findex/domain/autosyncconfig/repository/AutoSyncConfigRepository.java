package com.sprint.findex.domain.autosyncconfig.repository;

import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoSyncConfigRepository
        extends JpaRepository<AutoSyncConfig, Long>, AutoSyncConfigRepositoryCustom {

    boolean existsByIndexInfo(IndexInfo indexInfo);

    @EntityGraph(attributePaths = "indexInfo")
    List<AutoSyncConfig> findByEnabledTrue();
}
