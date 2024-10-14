package com.example.stock.stock.facade;

import com.example.stock.stock.entity.Stock;
import com.example.stock.stock.repository.StockJpaRepository;
import com.example.stock.stock.service.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class OptimisticLockFacadeTest {
    @Autowired
    private StockService stockService;
    @Autowired
    private StockJpaRepository stockJpaRepository;
    @Autowired
    private OptimisticLockFacade optimisticLockFacade;

    @BeforeEach
    public void before(){
        stockJpaRepository.saveAndFlush(new Stock(1L, 100L));
    }
    @AfterEach
    public void after(){
        stockJpaRepository.deleteAll();
    }

    @Test
    public void 재고감소(){
        stockService.decrease(1L, 1L);
        Stock stock = stockJpaRepository.findById(1l).get();
        assertEquals(99,stock.getQuantity() );
    }
    @Test
    public void 동시에_100개_optimistic_lock() throws InterruptedException{
        int threadMax = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadMax);

        for( int i = 0; i < threadMax; i ++ ){
            executorService.submit(() -> {
                try{
                    optimisticLockFacade.decrease(1L, 1L );
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Stock stock = stockJpaRepository.findById(1L).orElseThrow();
        assertEquals(0, stock.getQuantity() );
    }
}