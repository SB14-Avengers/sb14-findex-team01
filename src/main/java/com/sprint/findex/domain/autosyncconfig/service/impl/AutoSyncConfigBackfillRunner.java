package com.sprint.findex.domain.autosyncconfig.service.impl;

import com.sprint.findex.domain.autosyncconfig.repository.AutoSyncConfigRepository;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// 자동 연동 설정이 누락된 기존 지수를 앱 기동 시점에 한 번 보정한다.
// (#105 이전에 Open API 연동으로 생성된 지수는 자동 연동 설정이 없어서, 그 데이터를 보정하는 용도)
@Component
@RequiredArgsConstructor
@Slf4j
public class AutoSyncConfigBackfillRunner implements ApplicationRunner {

    private final IndexInfoRepository indexInfoRepository;
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final AutoSyncConfigInitializer autoSyncConfigInitializer;

    @Override
    public void run(ApplicationArguments args) {
        List<IndexInfo> missing =
                indexInfoRepository.findAll().stream()
                        .filter(info -> !autoSyncConfigRepository.existsByIndexInfo(info))
                        .toList();

        if (missing.isEmpty()) {
            log.info("[자동연동설정 보정] 누락된 지수 없음");
            return;
        }

        log.info("[자동연동설정 보정] 누락된 지수 {}건 발견, 생성 시작", missing.size());
        for (IndexInfo info : missing) {
            autoSyncConfigInitializer.initializeFor(info);
        }
        log.info("[자동연동설정 보정] 완료");
    }
}
