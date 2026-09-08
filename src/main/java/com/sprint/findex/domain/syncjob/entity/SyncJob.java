package com.sprint.findex.domain.syncjob.entity;

import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.entity.BaseEntity;
import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class SyncJob extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfo;

    // null일경우 화면에 '날짜 정보 없음'으로 프론트에서 표시
    @Column(name = "target_date")
    private Instant targetDate;

    @Column(name = "worker", nullable = false)
    private String worker;

    // 작업일시 = createdAt을 사용

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private JobResult result;
}
