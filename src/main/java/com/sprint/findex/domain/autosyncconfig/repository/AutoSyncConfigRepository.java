package com.sprint.findex.domain.autosyncconfig.repository;

import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long> {

    boolean existsByIndexInfo(IndexInfo indexInfo);

    // indexInfo를 같이 미리 로딩해서, 트랜잭션 없이 배치를 도는 executeAutoSync()에서
    // LazyInitializationException 걱정 자체를 원천봉쇄함
    @EntityGraph(attributePaths = "indexInfo")
    List<AutoSyncConfig> findByEnabledTrue();
}
