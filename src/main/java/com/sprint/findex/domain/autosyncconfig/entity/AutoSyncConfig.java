package com.sprint.findex.domain.autosyncconfig.entity;

import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoSyncConfig extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private IndexInfo indexInfo;

    @Column(nullable = false)
    private boolean enabled;

    private AutoSyncConfig(IndexInfo indexInfo, boolean enabled) {
        this.indexInfo = indexInfo;
        this.enabled = enabled;
    }

    public static AutoSyncConfig from(IndexInfo indexInfo) {
        return new AutoSyncConfig(indexInfo, false);
    }

    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
