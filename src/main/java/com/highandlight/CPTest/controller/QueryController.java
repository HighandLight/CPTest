package com.highandlight.CPTest.controller;

import com.highandlight.CPTest.entity.SampleData;
import com.highandlight.CPTest.repository.QueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QueryController {

    private static final Logger log = LoggerFactory.getLogger(QueryController.class);

    private final QueryRepository queryRepository;

    public QueryController(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    //단순 SELECT 쿼리
    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> query() {
        long start = System.currentTimeMillis();
        List<SampleData> rows = queryRepository.findAll();
        long duration = System.currentTimeMillis() - start;

        return ResponseEntity.ok(Map.of(
                "timestamp", Instant.now().toString(),
                "duration_ms", duration,
                "rows", rows
        ));
    }

    // 커넥션 장시간 점유
    @GetMapping("/slow")
    public ResponseEntity<Map<String, Object>> slow(
            @RequestParam(defaultValue = "3") int sleep) {
        long start = System.currentTimeMillis();
        log.warn("[SLOW] {}초 동안 DB 커넥션 점유", sleep);
        queryRepository.slowQuery(sleep);
        long duration = System.currentTimeMillis() - start;

        return ResponseEntity.ok(Map.of(
                "timestamp", Instant.now().toString(),
                "sleep_sec", sleep,
                "duration_ms", duration,
                "message", sleep + "초 동안 DB 커넥션 점유"
        ));
    }

    //close() 누락으로 커넥션 누수 유발
    @GetMapping("/leak")
    public ResponseEntity<Map<String, Object>> leak() {
        log.warn("[LEAK] 의도적인 DB 커넥션 누수 ");
        try {
            queryRepository.leakConnection();
        } catch (Exception e) {
            log.error("[LEAK] 커넥션 누수 실험 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of(
                "timestamp", Instant.now().toString(),
                "message", "커넥션 누수 발생"
        ));
    }

    //DB 커넥션 조회
    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> dbStatus() {
        List<Map<String, Object>> processes = queryRepository.showProcessList();
        return ResponseEntity.ok(Map.of(
                "timestamp", Instant.now().toString(),
                "active_queries", processes.size(),
                "processes", processes
        ));
    }
}