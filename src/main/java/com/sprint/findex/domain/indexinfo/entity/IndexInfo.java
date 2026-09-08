package com.sprint.findex.domain.indexinfo.entity;

import com.sprint.findex.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SourceType;

@Entity
@Table(name = "index_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndexInfo extends BaseEntity {

    @Column(nullable = false)
    private String indexClassification; // 지수 분류명

    @Column(nullable = false, unique = true)
    private String indexName; // 지수명

    @Column(nullable = false)
    private int employedItemsCount; // 채용 종목 수

    @Column(nullable = false)
    private LocalDate baseDate; // 기준 시점

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal baseIndex; // 기준 지수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType sourceType; // 소스 타입

    @Column(nullable = false)
    private boolean favorite; // 즐겨찾기

    private IndexInfo(
            String indexClassification,
            String indexName,
            int employedItemsCount,
            LocalDate baseDate,
            BigDecimal baseIndex,
            SourceType sourceType) {
        this.indexClassification = indexClassification;
        this.indexName = indexName;
        this.employedItemsCount = employedItemsCount;
        this.baseDate = baseDate;
        this.baseIndex = baseIndex;
        this.sourceType = sourceType;
        this.favorite = false;
    }

    public static IndexInfo of(
            String indexClassification,
            String indexName,
            int employedItemsCount,
            LocalDate baseDate,
            BigDecimal baseIndex,
            SourceType sourceType) {
        return new IndexInfo(
                indexClassification,
                indexName,
                employedItemsCount,
                baseDate,
                baseIndex,
                sourceType);
    }

    public void updateFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
