package com.sprint.findex.domain.indexdata.repository;

import com.sprint.findex.domain.indexdata.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDataRepository extends JpaRepository<IndexData, Long> {
    List<IndexData> findByIndexInfoIdOrderByBaseDateAsc(Long indexInfoId);

    boolean existsByIndexInfoIdAndBaseDate(Long indexInfoId, LocalDate baseDate);
}
