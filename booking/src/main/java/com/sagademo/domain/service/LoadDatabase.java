package com.sagademo.domain.service;

import java.util.logging.Logger;

import com.sagademo.domain.entity.Room;
import com.sagademo.domain.repository.RoomRepository;
import com.sagademo.domain.vo.RoomSituation;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @Bean
    CommandLineRunner initDatabase(RoomRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Room(100, null, 1000.0, RoomSituation.FREE)));
            log.info("Preloading " + repository.save(new Room(200, null, 1000.0, RoomSituation.FREE)));
            log.info("Preloading " + repository.save(new Room(300, null, 1000.0, RoomSituation.FREE)));
            log.info("Preloading " + repository.save(new Room(400, null, 1000.0, RoomSituation.FREE)));
            log.info("Preloading " + repository.save(new Room(500, null, 2000.0, RoomSituation.FREE)));
        };
    }

}