package com.highandlight.CPTest.repository;

import com.highandlight.CPTest.entity.SampleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SampleDataRepository extends JpaRepository<SampleData, Long> {

    //커넥션을 seconds초간 점유하여 풀 고갈 유발
    @Query(value = "SELECT SLEEP(:seconds)", nativeQuery = true)
    void sleep(@Param("seconds") int seconds);

    //DB 커넥션 현황
    @Query(value = """
        SELECT ID, USER, HOST, DB, COMMAND, TIME, STATE 
        FROM information_schema.PROCESSLIST 
        WHERE COMMAND != 'Sleep' OR TIME > 1 
        ORDER BY TIME DESC """, nativeQuery = true)
    List<Object[]> showProcessList();
}
