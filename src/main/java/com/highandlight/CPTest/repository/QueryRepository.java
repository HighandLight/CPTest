package com.highandlight.CPTest.repository;

import com.highandlight.CPTest.entity.SampleData;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class QueryRepository {

    private final SampleDataRepository sampleDataRepository;
    private final DataSource dataSource;

    public QueryRepository(SampleDataRepository sampleDataRepository, DataSource dataSource) {
        this.sampleDataRepository = sampleDataRepository;
        this.dataSource = dataSource;
    }


    //단순 SELECT : 디폴트
    public List<SampleData> findAll() {
        return sampleDataRepository.findAll();
    }

    //슬로우 쿼리 : slow query cascade
    public void slowQuery(int sleepSeconds) {
        sampleDataRepository.sleep(sleepSeconds);
    }

    //connection leak
    //JPA는 트랜젝션 종료 시, HikariCP 커넥션 자동 반환.. -> datasource에서 직접 불러옴
    public void leakConnection() throws Exception {
        Connection connection = dataSource.getConnection();
        connection.createStatement().executeQuery("SELECT 1");
        // connection.close() 생략 -> leakDetectionThreshold 트리거
    }

    //DB 커넥션 현황 — 실험 관측용
    public List<Map<String, Object>> showProcessList() {
        return sampleDataRepository.showProcessList().stream()
                .map(row -> Map.of(
                        "id",      row[0],
                        "user",    row[1],
                        "host",    row[2],
                        "db",      row[3] != null ? row[3] : "",
                        "command", row[4],
                        "time",    row[5],
                        "state",   row[6] != null ? row[6] : ""
                ))
                .toList();
    }
}