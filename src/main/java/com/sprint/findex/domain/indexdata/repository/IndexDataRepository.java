package com.sprint.findex.domain.indexdata.repository;

import com.sprint.findex.domain.indexdata.entity.IndexData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IndexDataRepository extends JpaRepository<IndexData, Long> {
    List<IndexData> findByIndexInfoIdOrderByBaseDateAsc(Long indexInfoId);

    boolean existsByIndexInfoIdAndBaseDate(Long indexInfoId, LocalDate baseDate);

    List<IndexData> findByIndexInfoIdAndBaseDateGreaterThanEqualOrderByBaseDate(
            Long indexInfoId, LocalDate baseDate);

    @Query(
            "select d from IndexData d \n"
                    + "where d.indexInfo.id = :indexInfoId \n"
                    + "and d.baseDate >= :startDate \n"
                    + "order by d.baseDate asc")
    List<IndexData> findChartData(
            @Param("indexInfoId") Long id, @Param("startDate") LocalDate data);

    List<IndexData> findByIndexInfoId(@Param("indexInfo") Long indexInfoId);

    @Query(
            "select d.closingPrice from IndexData d \n"
                    + "where d.indexInfo.id = :indexInfoId\n"
                    + "order by d.baseDate desc \n"
                    + "limit 1")
    BigDecimal findByCurrentPrice(@Param("indexInfoId") Long indexInfoId);

    @Query(
            "select d.closingPrice from IndexData d \n"
                    + "where d.indexInfo.id = :indexInfoId \n"
                    + "and d.baseDate < :startDate \n"
                    + "order by d.baseDate Desc \n"
                    + "limit 1")
    BigDecimal findByBeforePrice(
            @Param("startDate") LocalDate startDate, @Param("indexInfoId") Long indexInfoId);
}
