package com.example.stock.stock.facade;


import com.example.stock.stock.service.StockService;
import io.lettuce.core.RedisClient;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedissonLockFacade {

    private final RedissonClient redissonClient;

    private final StockService stockService;

    public void decrease( Long id, Long quantity ){
        RLock lock = redissonClient.getLock(id.toString());

        try{
            boolean available = lock.tryLock( 15, 1, TimeUnit.SECONDS );
            if( !available ){
                log.warn("lock 획득 실패");
                return;
            }
            stockService.decrease(id, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
