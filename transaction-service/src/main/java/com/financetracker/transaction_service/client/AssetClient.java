package com.financetracker.transaction_service.client;

import com.financetracker.transaction_service.entity.Asset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AssetClient {

    private final WebClient webClient;

    @Autowired
    public AssetClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://asset-service-url").build();
    }

    public Asset getCashAssetByUserId(Long userId) {
        return this.webClient.get()
            .uri("/assets/cash/{userId}", userId)
            .retrieve()
            .bodyToMono(Asset.class)
            .block();  // Using block() for simplicity, but consider using reactive handling
    }

    public void updateAsset(Asset asset) {
        this.webClient.put()
            .uri("/assets/{id}", asset.getId())
            .bodyValue(asset)
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}
