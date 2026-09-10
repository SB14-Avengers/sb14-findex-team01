package com.sprint.findex.domain.indexdata.repository;

import com.sprint.findex.domain.indexdata.entity.IndexData;
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
                    + "and d.baseDate >= :baseDate \n"
                    + "order by d.baseDate asc")
    List<IndexData> findChartData(@Param("indexInfoId") Long id, @Param("baseDate") LocalDate data);
}
