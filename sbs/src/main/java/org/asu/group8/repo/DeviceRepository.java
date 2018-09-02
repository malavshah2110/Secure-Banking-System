package org.asu.group8.repo;

import org.asu.group8.entity.SbsDevice;
import org.asu.group8.entity.SbsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface DeviceRepository extends JpaRepository<SbsDevice, Long> {

    @Query("SELECT d FROM SbsDevice d WHERE d.sbsUser = :sbsUser AND d.code = :code AND d.expiration > :expiration")
    SbsDevice findByCode(@Param("sbsUser") SbsUser sbsUser, @Param("code") String code, @Param("expiration") Date expiration);

}
