package org.nastation.module.pub.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Random;

@Service
public class FluxService {

    public Flux<BigDecimal> delayBlockHeight(String height) {
        Random random = new Random();
        return Flux
                .<BigDecimal>generate(
                        sink -> {
                            sink.next(BigDecimal.valueOf(random.nextInt(10000), 2));
                        }
                )
                .delayElements(Duration.ofMillis(2000))
                .take(1000);
    }
}
