package com.jp.inventoryservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    @Autowired
    InventoryRepository inventoryRepo;

    @Autowired
    CustomerService customerService;

    private static final Logger log = LoggerFactory.getLogger(MainRestController.class);

    @GetMapping("/test")
    public ResponseEntity<?> testInventoryService() {
        return ResponseEntity.ok("Inventory Service running fine");
    }

    @PostMapping("/inventory/add")
    public ResponseEntity<?> addInventory(@RequestBody Inventory inventory,@RequestHeader("Authorization") String token)
    {
        log.info("Received request to add product inventory : {}", inventory);

        if(customerService.validateToken(token)){
            inventoryRepo.save(inventory);
            return ResponseEntity.ok("New Product Inventory Added.");
        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @GetMapping("/inventory/view/{productId}")
    public ResponseEntity<?> viewProduct(@PathVariable Long productId, @RequestHeader("Authorization") String token){
        log.info("Received request to view inventory with product id : {} ",productId);

        if(customerService.validateToken(token)){
            Optional<Inventory> inventoryObj = inventoryRepo.findById(productId);

            if(inventoryObj.isPresent()){
                return ResponseEntity.ok(inventoryObj);
            }else{
                return  ResponseEntity.ok("No inventory found with given id : "+productId);
            }
        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @PostMapping("/inventory/block")
    public ResponseEntity<?> blockInventory(@RequestBody Inventory inventoryParam, @RequestHeader("Authorization") String token){
        log.info("Received request to block inventory with data : {} ",inventoryParam);

        if(customerService.validateToken(token)){
            Inventory inventoryObj = inventoryRepo.findById(inventoryParam.getProductId()).get();

            if(inventoryObj.getQuantities() < inventoryParam.getQuantities()){
                return ResponseEntity.ok("Insufficient Quantities");
            }

            int remaining = inventoryObj.getQuantities() - inventoryParam.getQuantities();
            inventoryObj.setQuantities(remaining);
            inventoryRepo.save(inventoryObj);
            return ResponseEntity.ok("Inventory quantities blocked as requested for : "+inventoryParam);

        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @PostMapping("/inventory/release")
    public ResponseEntity<?> releaseInventory(@RequestBody Inventory inventoryParam, @RequestHeader("Authorization") String token){
        log.info("Received request to release inventory with data : {} ",inventoryParam);

        if(customerService.validateToken(token)){
            Inventory inventoryObj = inventoryRepo.findById(inventoryParam.getProductId()).get();

            int total = inventoryObj.getQuantities() + inventoryParam.getQuantities();
            inventoryObj.setQuantities(total);
            inventoryRepo.save(inventoryObj);
            return ResponseEntity.ok("Inventory quantities released as requested for : "+inventoryParam);

        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @PostMapping("/inventory/delete/{productId}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long productId, @RequestHeader("Authorization") String token){
        log.info("Received request to delete product inventory with product id : {} ",productId);

        if(customerService.validateToken(token)){
            Optional<Inventory> inventoryObj = inventoryRepo.findById(productId);
            if(inventoryObj.isPresent()){
                inventoryRepo.deleteById(productId);
                return ResponseEntity.ok("Product Inventory data deleted successfully.");
            }else{
                return  ResponseEntity.ok("No product inventory found with given id : "+productId);
            }
        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @GetMapping("inventory/viewAll")
    public ResponseEntity<?> viewAllProducts(){
        log.info("Received request to view all inventory");
        return ResponseEntity.ok(inventoryRepo.findAll());
    }


}
