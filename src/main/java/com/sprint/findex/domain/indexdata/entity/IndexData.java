package com.sprint.findex.domain.indexdata.entity;

import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.entity.BaseEntity;
import com.sprint.findex.global.type.SourceType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(
        name = "index_data",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_index_data_indexInfo_baseDate",
                        columnNames = {"index_info_id", "base_date"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndexData extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfo; // 지수 정보

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate; // 날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType; // 소스타입

    @Column(name = "market_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal marketPrice; // 시가

    @Column(name = "closing_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal closingPrice; // 종가

    @Column(name = "high_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal highPrice; // 고가

    @Column(name = "low_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal lowPrice; // 저가

    @Column(name = "versus", nullable = false, precision = 15, scale = 2)
    private BigDecimal versus; // 대비

    @Column(name = "fluctuation_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal fluctuationRate; // 등락률

    @Column(name = "trading_quantity", nullable = false)
    private Long tradingQuantity; // 거래량

    @Column(name = "trading_price", nullable = false)
    private Long tradingPrice; // 거래대금

    @Column(name = "market_total_amount", nullable = false)
    private Long marketTotalAmount; // 상장 시가 총액

    private IndexData(
            IndexInfo indexInfo,
            LocalDate baseDate,
            SourceType sourceType,
            BigDecimal marketPrice,
            BigDecimal closingPrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal versus,
            BigDecimal fluctuationRate,
            Long tradingQuantity,
            Long tradingPrice,
            Long marketTotalAmount) {
        this.indexInfo = indexInfo;
        this.baseDate = baseDate;
        this.sourceType = sourceType;
        this.marketPrice = marketPrice;
        this.closingPrice = closingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.versus = versus;
        this.fluctuationRate = fluctuationRate;
        this.tradingQuantity = tradingQuantity;
        this.tradingPrice = tradingPrice;
        this.marketTotalAmount = marketTotalAmount;
    }

    public static IndexData of(
            IndexInfo indexInfo,
            LocalDate baseDate,
            SourceType sourceType,
            BigDecimal marketPrice,
            BigDecimal closingPrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal versus,
            BigDecimal fluctuationRate,
            Long tradingQuantity,
            Long tradingPrice,
            Long marketTotalAmount) {
        return new IndexData(
                indexInfo,
                baseDate,
                sourceType,
                marketPrice,
                closingPrice,
                highPrice,
                lowPrice,
                versus,
                fluctuationRate,
                tradingQuantity,
                tradingPrice,
                marketTotalAmount);
    }

    public void update(
            BigDecimal marketPrice,
            BigDecimal closingPrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal versus,
            BigDecimal fluctuationRate,
            Long tradingQuantity,
            Long tradingPrice,
            Long marketTotalAmount) {

        if (marketPrice != null) {
            this.marketPrice = marketPrice;
        }
        if (closingPrice != null) {
            this.closingPrice = closingPrice;
        }
        if (highPrice != null) {
            this.highPrice = highPrice;
        }
        if (lowPrice != null) {
            this.lowPrice = lowPrice;
        }
        if (versus != null) {
            this.versus = versus;
        }
        if (fluctuationRate != null) {
            this.fluctuationRate = fluctuationRate;
        }
        if (tradingQuantity != null) {
            this.tradingQuantity = tradingQuantity;
        }
        if (tradingPrice != null) {
            this.tradingPrice = tradingPrice;
        }
        if (marketTotalAmount != null) {
            this.marketTotalAmount = marketTotalAmount;
        }
    }
}
