package com.trip.tripshorts;

import com.trip.tripshorts.video.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TripShortsBeApplicationTests {

	@Autowired
	S3Service s3Service;

	@Test
	void contextLoads() {
	}

}
