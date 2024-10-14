package com.example.stock.stock.repository;

import com.example.stock.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LockRepository extends JpaRepository<Stock, Long> {
    //실제로는 커넥션 풀의 제한때문에 Data Source를 구분할 필요가 있다.
    @Query(value="select get_lock(:key, 3000)",nativeQuery = true)
    void getLock( @Param("key") String key );

    @Query(value="select release_lock(:key)",nativeQuery = true)
    void releaseLock(@Param("key") String key);
}
