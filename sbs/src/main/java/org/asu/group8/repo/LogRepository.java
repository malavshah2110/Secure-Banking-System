package org.asu.group8.repo;

import org.asu.group8.entity.SbsLog;
import org.asu.group8.entity.SbsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<SbsLog, Long> {

    @Query(value="SELECT * FROM sbs_log ORDER BY time DESC LIMIT 1000", nativeQuery = true)
    List<SbsLog> findTopLogs();

}
