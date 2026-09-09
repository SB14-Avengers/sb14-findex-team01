package com.sprint.findex.domain.syncjob.service.impl;

import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.domain.syncjob.service.SyncJobService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SyncJobServiceImpl implements SyncJobService {
    @Override
    public List<SyncJobDto> indexInfoSync() {
        // 지수 정보 가져와서 연동하는 로직 구현
        return null;
    }

    @Override
    public List<SyncJobDto> indexDataSync() {
        // 지수 데이터 가져와서 연동하는 로직 구현
        return null;
    }
}
