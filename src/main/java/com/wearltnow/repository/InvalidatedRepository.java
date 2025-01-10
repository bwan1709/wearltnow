package com.wearltnow.repository;

import com.wearltnow.model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidatedRepository extends JpaRepository<InvalidatedToken, String> {
    void deleteByExpiryTimeBefore(Date expiryTime);
}
