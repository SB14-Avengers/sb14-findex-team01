package com.sprint.findex.domain.autosyncconfig.scheduler;

import com.sprint.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

    private final AutoSyncConfigService autoSyncConfigService;

    @Scheduled(cron = "${findex.scheduler.auto-sync-cron}")
    public void runAutoSync() {
        log.info("[자동연동] 배치 실행 시작");
        autoSyncConfigService.executeAutoSync();
        log.info("[자동연동] 배치 실행 종료");
    }
}
