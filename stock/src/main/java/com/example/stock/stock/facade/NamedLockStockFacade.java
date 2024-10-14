package com.example.stock.stock.facade;

import com.example.stock.stock.repository.LockRepository;
import com.example.stock.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class NamedLockStockFacade {
    private final LockRepository lockRepository;
    private final StockService stockService;

    @Transactional
    public void decrease(Long id, Long quantity){
        try{
            lockRepository.getLock(id.toString());
            log.info( "id :" + id + " lock get" );
            stockService.decrease(id,quantity);
        }finally {
            lockRepository.releaseLock(id.toString());
            log.info( "id :" + id + " lock releasez" );
        }
    }

}
