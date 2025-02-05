package com.financetracker.asset_service.service;

import com.financetracker.asset_service.entity.Asset;
import com.financetracker.asset_service.repo.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    // Create a new asset
    public Asset createAsset(Asset asset) {

        Asset cashAsset = getAssetByUserIdAndType(asset.getUserId(), "Cash").get(0);
        if(cashAsset!=null && asset.getType()=="Cash"){
            Asset updatedAsset = updateCash(cashAsset, asset);
            return updatedAsset;
        }
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    public Asset updateCash(Asset assetDetails, Asset asset) {
        assetDetails.setCurrentValue(assetDetails.getCurrentValue().add(asset.getCurrentValue()));
        assetDetails.setAcquiredValue(assetDetails.getCurrentValue());
        assetDetails.setDescription(asset.getDescription());
        assetDetails.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(assetDetails);
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
    public List<Asset> getAssetByUserIdAndType(Long userId, String type) {
        return assetRepository.findByUserIdAndType(userId, type);
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
