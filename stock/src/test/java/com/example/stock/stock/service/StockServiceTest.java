package com.example.stock.stock.service;

import com.example.stock.stock.entity.Stock;
import com.example.stock.stock.repository.StockJpaRepository;
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
class StockServiceTest {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockJpaRepository stockJpaRepository;

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
    public void 동시에_100개() throws InterruptedException{
        int threadMax = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadMax);

        for( int i = 0; i < threadMax; i ++ ){
            executorService.submit(() -> {
                try{
                    stockService.decrease(1L, 1L );
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Stock stock = stockJpaRepository.findById(1L).orElseThrow();
        assertEquals(0, stock.getQuantity() );
    }

    @Test
    public void 동시에_100개_sync() throws InterruptedException{
        /**
         *
         * 이렇게 syncronized를 넣은 메소드를 넣는다고 해결되지 않는다.
         * @Transactional 어노테이션 동작 방식 때문
         * transaction.start()
         * 비즈니스 로직
         * transaction.commit()
         *
         * 그러므로 Transactional 어노테이션을 주석처리하고 실행해봄 -> 성공
         *
         * syncronized는 서버가 한대일 때는 문제가 없다. 허나 서버가 두 대, 세 대 즉 여러 서버를 운영하게 되면 이는
         * 여러 서버가 운영될 때 정답이 될 수 없다.
         *
         * Pessimistic Lock
         *  - 실제 데이터 Lock을 걸어서 정합성을 맞추는 방법 exclusive lock을 걸면 다른 트랜잭션은 접근할 수 없기에 데드락 가능성이 있다.
         * Optimistic Lock
         *  - 실제 Lock이 아닌 버전을 교환하여 정합성을 맞추는 방법. 데이터를 읽은 후 update를 수행할 때, 내가 읽은
         *  버전이 맞는지 확인해서 업데이트가 일어남
         *  - 만약 Exception 발생시 내가 직접 처리해야 한다.
         *  Named Lock
         *  - 이름을 가진 metadata locking이다. 이름을 가진 lock을 획득한 후에 해제할 때 까지 다른 세션은 이 lock을
         *  획득할 수 없다.
         *  - 이는 transaction 종료 시, lock이 자동으로 해제되지 않기에 직접 처리해주어야 한다.
         *
         */
        int threadMax = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadMax);

        for( int i = 0; i < threadMax; i ++ ){
            executorService.submit(() -> {
                try{
                    stockService.decreasesync(1L, 1L );
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Stock stock = stockJpaRepository.findById(1L).orElseThrow();
        assertEquals(0, stock.getQuantity() );
    }
    @Test
    public void 동시에_100개_pessimistic_lock() throws InterruptedException{
        int threadMax = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadMax);

        for( int i = 0; i < threadMax; i ++ ){
            executorService.submit(() -> {
                try{
                    stockService.decreasePessimistic(1L, 1L );
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Stock stock = stockJpaRepository.findById(1L).orElseThrow();
        assertEquals(0, stock.getQuantity() );
    }




}