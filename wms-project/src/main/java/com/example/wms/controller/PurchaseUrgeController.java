package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.PurchaseUrge;
import com.example.wms.service.PurchaseUrgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase-urge")
public class PurchaseUrgeController {

    @Autowired
    private PurchaseUrgeService purchaseUrgeService;
    
    @GetMapping("/list/{orderId}")
    public Result<List<PurchaseUrge>> getUrgeListByOrderId(@PathVariable Long orderId) {
        return purchaseUrgeService.getUrgeListByOrderId(orderId);
    }
    
    @GetMapping("/pending/{orderId}")
    public Result<List<PurchaseUrge>> getPendingUrgesByOrderId(@PathVariable Long orderId) {
        return purchaseUrgeService.getPendingUrgesByOrderId(orderId);
    }
    
    @PostMapping("/create")
    public Result<PurchaseUrge> createUrge(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String urgeContent = (String) request.get("urgeContent");
        return purchaseUrgeService.createUrge(orderId, urgeContent);
    }
    
    @PostMapping("/respond/{urgeId}")
    public Result<Void> respondToUrge(@PathVariable Long urgeId, @RequestBody Map<String, String> request) {
        String responseContent = request.get("responseContent");
        return purchaseUrgeService.respondToUrge(urgeId, responseContent);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteUrge(@PathVariable Long id) {
        return purchaseUrgeService.deleteUrge(id);
    }
}
