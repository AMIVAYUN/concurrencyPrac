package com.example.stock.stock.service;


import com.example.stock.stock.entity.Stock;
import com.example.stock.stock.repository.StockJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockJpaRepository stockJpaRepository;

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    /**
     *
     * NamedLockFacade의 트랜잭션을 따라가게 되면 만약 감소를 못하는 ( ex 재고 부족 ) 상황시 잠금 해제가 불가할 수 있다.
     */
    @Transactional
    public void decrease( Long id, Long quantity ){

        Stock stock = stockJpaRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( id + "에 해당하는 제품을 찾을 수 없습니다." ) );
        stock.decrease(quantity);
        stockJpaRepository.saveAndFlush(stock);
    }
//    @Transactional
    public synchronized void decreasesync( Long id, Long quantity ){
        Stock stock = stockJpaRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( id + "에 해당하는 제품을 찾을 수 없습니다." ) );
        stock.decrease(quantity);
        stockJpaRepository.saveAndFlush(stock);
    }

    @Transactional
    public void decreasePessimistic( Long id, Long quantity ){
        Stock stock = stockJpaRepository.findByPessmistic( id );
        stock.decrease( quantity );
        stockJpaRepository.save( stock );
    }

    @Transactional
    public void decreaseOptimistic( Long id, Long quantity ){
        Stock stock = stockJpaRepository.findByOptimistic( id );
        stock.decrease( quantity );
        stockJpaRepository.save( stock );
    }

}
