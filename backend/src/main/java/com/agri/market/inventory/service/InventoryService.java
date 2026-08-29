package com.agri.market.inventory.service;

import com.agri.market.inventory.dto.InventoryResponseDto;
import com.agri.market.inventory.dto.InventoryUpdateRequestDto;
import com.agri.market.inventory.dto.StockAdjustmentRequestDto;

import java.util.List;

public interface InventoryService {

    InventoryResponseDto getAvailability(String productId);

    InventoryResponseDto getInventory(String productId);

    List<InventoryResponseDto> getMyInventory(String farmerId);

    InventoryResponseDto updateInventory(
            String productId,
            String farmerId,
            InventoryUpdateRequestDto request
    );

    InventoryResponseDto addStock(
            String productId,
            String farmerId,
            StockAdjustmentRequestDto request
    );

    InventoryResponseDto removeStock(
            String productId,
            String farmerId,
            StockAdjustmentRequestDto request
    );
}