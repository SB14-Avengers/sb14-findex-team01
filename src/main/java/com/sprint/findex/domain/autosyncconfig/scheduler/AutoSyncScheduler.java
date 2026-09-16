package com.sprint.findex.domain.autosyncconfig.scheduler;

import com.sprint.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

    private final AutoSyncConfigService autoSyncConfigService;
    private static final AtomicBoolean running = new AtomicBoolean(false);

    @Scheduled(cron = "${findex.scheduler.auto-sync-cron:0 30 13 * * *}", zone = "Asia/Seoul")
    public void runAutoSync() {
        if (!running.compareAndSet(false, true)) {
            log.warn("[자동연동] 이전 배치가 아직 끝나지 않아 이번 실행을 건너 뜁니다.");
            return;
        }
        try {
            log.info("[자동연동] 배치 실행 시작");
            autoSyncConfigService.executeAutoSync();
            log.info("[자동연동] 배치 실행 종료");
        } finally {
            running.set(false);
        }
    }
}
