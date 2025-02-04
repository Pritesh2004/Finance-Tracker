package com.financetracker.asset_service.controller;

import com.financetracker.asset_service.client.UserClient;
import com.financetracker.asset_service.entity.Asset;
import com.financetracker.asset_service.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserClient userClient;

    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset, @RequestHeader("Authorization") String authorizationHeader) {

        Asset createdAsset = assetService.createAsset(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAsset);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Asset>> getAssetsByUser(@PathVariable Long userId) {
        List<Asset> assets = assetService.getAssetsByUser(userId);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{userId}/{type}")
    public ResponseEntity<List<Asset>> getAssetByUserIdAndType(@PathVariable Long userId, @PathVariable String type) {
        List<Asset> asset = assetService.getAssetByUserIdAndType(userId, type);
        if (asset == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(asset);
    }

    @PutMapping("/{assetId}")
    public ResponseEntity<Asset> updateAsset(@PathVariable Long assetId, @RequestBody Asset assetDetails) {
        Asset updatedAsset = assetService.updateAsset(assetId, assetDetails);
        return ResponseEntity.ok(updatedAsset);
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long assetId) {
        assetService.deleteAsset(assetId);
        return ResponseEntity.noContent().build();
    }
}
