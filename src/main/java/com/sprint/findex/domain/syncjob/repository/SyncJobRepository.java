package com.sprint.findex.domain.syncjob.repository;

import com.sprint.findex.domain.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncJobRepository extends JpaRepository<SyncJob, Long> {}
