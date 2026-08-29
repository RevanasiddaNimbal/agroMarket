package com.agri.market.admin.service;

import com.agri.market.inventory.dto.InventoryResponseDto;
import com.agri.market.inventory.dto.InventoryUpdateRequestDto;
import com.agri.market.inventory.dto.StockAdjustmentRequestDto;

public interface AdminInventoryService {

    InventoryResponseDto getInventory(String productId);

    InventoryResponseDto updateInventory(
            String productId,
            InventoryUpdateRequestDto request
    );

    InventoryResponseDto addStock(
            String productId,
            StockAdjustmentRequestDto request
    );

    InventoryResponseDto removeStock(
            String productId,
            StockAdjustmentRequestDto request
    );
}