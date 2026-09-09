package com.sprint.findex.domain.autosyncconfig.repository;

import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long> {}
