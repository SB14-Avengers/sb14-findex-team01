package com.sprint.findex.domain.syncjob.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.domain.syncjob.entity.SyncJob;
import com.sprint.findex.domain.syncjob.mapper.SyncJobMapper;
import com.sprint.findex.domain.syncjob.repository.SyncJobRepository;
import com.sprint.findex.domain.syncjob.service.SyncJobService;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.SyncJobErrorCode;
import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import com.sprint.findex.global.type.SourceType;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncJobServiceImpl implements SyncJobService {
    private static final DateTimeFormatter BAS_DT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;
    private final SyncJobMapper syncJobMapper;

    // TODO: OpenApi 만들어지기 전에 임시 사용 코드
    @Value("${findex.openapi.uri}")
    private String baseUrl;

    @Value("${PUBLIC_API_SERVICE_KEY}")
    private String serviceKey;

    private JsonNode fetch(LocalDate baseDate) {
        try {
            String url =
                    baseUrl
                            + "?serviceKey="
                            + URLEncoder.encode(serviceKey, StandardCharsets.UTF_8)
                            + "&resultType=json&pageNo=1&numOfRows=1000"
                            + "&basDt="
                            + baseDate.format(BAS_DT);

            String json =
                    RestClient.create().get().uri(URI.create(url)).retrieve().body(String.class);

            return new ObjectMapper()
                    .readTree(json)
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");
        } catch (Exception e) {
            log.error("[OpenApi] 호출 실패 basDt={}", baseDate, e);
            throw new BusinessException(SyncJobErrorCode.OPEN_API_CALL_FAILED);
        }
    }

    @Override
    @Transactional
    public List<SyncJobDto> indexInfoSync() {
        // TODO: OpenApi로 교체
        List<SyncJobDto> syncJobs = new ArrayList<>();
        JsonNode items = fetch(LocalDate.of(2026, 9, 8));

        for (JsonNode item : items) {
            if (!item.hasNonNull("basPntm")
                    || !item.hasNonNull("basIdx")
                    || !item.hasNonNull("epyItmsCnt")) {
                log.warn(
                        "[연동] 필수값 누락으로 건너뜀: {} / {}",
                        item.path("idxCsf").asText(),
                        item.path("idxNm").asText());
                continue;
            }
            String indexClassification = item.path("idxCsf").asText();
            String indexName = item.path("idxNm").asText();
            int employedItemsCount = item.path("epyItmsCnt").asInt();
            LocalDate baseDate = LocalDate.parse(item.get("basPntm").asText(), BAS_DT);
            BigDecimal baseIndex = new BigDecimal(item.get("basIdx").asText());

            // TODO: indexInfo 존재하면 업데이트, 미존재하면 생성 - 업데이트(employedItemsCount, baseDate, baseIndex)
            // TODO: 나중에 findByIndexClassificationAndIndexName 필요
            IndexInfo indexInfo =
                    indexInfoRepository.save(
                            IndexInfo.of(
                                    indexClassification,
                                    indexName,
                                    employedItemsCount,
                                    baseDate,
                                    baseIndex,
                                    SourceType.OPEN_API));

            // TODO: worker 수정
            SyncJob created =
                    syncJobRepository.save(
                            SyncJob.of(
                                    JobType.INDEX_INFO, indexInfo, null, "kyj", JobResult.SUCCESS));

            syncJobs.add(syncJobMapper.toDto(created));
        }

        return syncJobs;
    }

    @Override
    public List<SyncJobDto> indexDataSync() {
        // TODO: OpenApi로 교체
        return null;
    }
}
