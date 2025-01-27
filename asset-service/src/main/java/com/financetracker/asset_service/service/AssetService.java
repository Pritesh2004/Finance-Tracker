package com.financetracker.asset_service.service;

import com.financetracker.asset_service.entity.Asset;
import com.financetracker.asset_service.repo.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    // Create a new asset
    public Asset createAsset(Asset asset) {
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    // Get assets for a user
    public List<Asset> getAssetsByUser(Long userId) {
        return assetRepository.findByUserId(userId);
    }

    // Update an existing asset
    public Asset updateAsset(Long assetId, Asset assetDetails) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        asset.setCurrentValue(assetDetails.getCurrentValue());
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }
    public Asset getCashAssetByUserId(Long userId) {
        return assetRepository.findByUserIdAndType(userId, "Cash");
    }


    // Delete an asset
    public void deleteAsset(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        asset.setIsActive(false);
        asset.setUpdatedAt(LocalDateTime.now());
        assetRepository.save(asset); // or delete asset as per business needs
    }
}
