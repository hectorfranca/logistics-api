package com.seniorsystems.logisticsapi.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.seniorsystems.logisticsapi.models.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

}
