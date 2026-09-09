package com.sprint.findex.domain.syncjob.entity;

import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.entity.BaseEntity;
import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "sync_jobs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SyncJob extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private IndexInfo indexInfo;

    // null일경우 화면에 '날짜 정보 없음'으로 프론트에서 표시
    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "worker", nullable = false)
    private String worker;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private JobResult result;

    // 작업일시 = createdAt을 사용

    private SyncJob(
            JobType jobType,
            IndexInfo indexInfo,
            LocalDate targetDate,
            String worker,
            JobResult result) {
        this.jobType = jobType;
        this.indexInfo = indexInfo;
        this.targetDate = targetDate;
        this.worker = worker;
        this.result = result;
    }

    public static SyncJob of(
            JobType jobType,
            IndexInfo indexInfo,
            LocalDate targetDate,
            String worker,
            JobResult result) {
        return new SyncJob(jobType, indexInfo, targetDate, worker, result);
    }
}
