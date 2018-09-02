package org.asu.group8.impl;

import org.asu.group8.entity.SbsDevice;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String createDevice(String username) {
        SbsUser sbsUser = userRepository.findByUsername(username);

        if (sbsUser != null) {
            SbsDevice sbsDevice = new SbsDevice();

            String code = "";
            for (int i = 0; i < 100; i++) {
                code += Integer.toString((int) Math.floor(Math.random() * 10));
            }
            sbsDevice.setCode(code);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 7);         // 7 days later
            sbsDevice.setExpiration(calendar.getTime());

            sbsUser.addSbsDevice(sbsDevice);

            userRepository.save(sbsUser);

            return code;
        } else {
            return null;
        }

    }

}
