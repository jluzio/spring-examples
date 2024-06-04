package com.example.spring.modulith.order;

import com.example.spring.modulith.inventory.InventoryManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderManagement {

  private final InventoryManagement inventoryManagement;

}
