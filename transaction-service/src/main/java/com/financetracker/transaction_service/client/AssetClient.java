package com.financetracker.transaction_service.client;

import com.financetracker.transaction_service.entity.Asset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class AssetClient {

    private final WebClient webClient;

    @Autowired
    public AssetClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://asset-service").build();
    }

    public Asset getCashAssetByUserId(Long userId, String type) {
        List<Asset> list = webClient.get()
                .uri("/api/assets/{userId}/{type}", userId, type)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Asset>>() {})  // Correct way to handle generics
                .block();  // Using block() for simplicity, but consider using reactive handling

        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }


    public void updateAsset(Asset asset) {
        this.webClient.put()
            .uri("/api/assets/{id}", asset.getId())
            .bodyValue(asset)
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}
