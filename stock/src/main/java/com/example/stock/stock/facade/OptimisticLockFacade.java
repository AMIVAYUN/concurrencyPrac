package com.example.stock.stock.facade;

import com.example.stock.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class OptimisticLockFacade {

    private final StockService stockService;

    public void decrease( Long id, Long quantity ) throws InterruptedException {
        while( true ){
            try{
                stockService.decreaseOptimistic(id, quantity);
                break;
            }catch (Exception e ){
                Thread.sleep(50);
            }
        }
    }

}
