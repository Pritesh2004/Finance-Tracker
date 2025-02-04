package com.financetracker.asset_service.repo;

import com.financetracker.asset_service.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByUserId(Long userId);
    List<Asset> findByUserIdAndType(Long userId, String type);

}
