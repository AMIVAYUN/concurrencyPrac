package com.example.stock.stock.facade;

import com.example.stock.stock.repository.RedisLockRepository;
import com.example.stock.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {
    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;

    public void decrease( Long id, Long quantity ) throws InterruptedException {
        while( !redisLockRepository.lock( id ) ){
            Thread.sleep(100);

        }
        try{
            stockService.decrease(id, quantity);
        }finally {
            redisLockRepository.unLock(id);
        }
    }
}
