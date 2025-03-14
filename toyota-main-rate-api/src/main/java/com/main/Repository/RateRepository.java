package com.main.Repository;

import com.main.Entity.RateEntity;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("rateRepository")
public interface RateRepository extends JpaRepository<RateEntity,Long> {

}
