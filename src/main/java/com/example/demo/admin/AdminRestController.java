package com.example.demo.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/v1")
public class AdminRestController {
    private static final String CUSTOMER_SEED_TASK = "CUSTOMER_SEEDING";

    @Autowired
    private CustomerSeedService seedService;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private CacheManager cacheManager;

    @PostMapping("/customer/seed")
    @ResponseBody
    @Operation(summary = "Seed the customer table")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evicted",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = {
                    @ExampleObject(value = """
                            {
                                "status": "seeding started"
                            }
                            """)
                }
            )
            }
        )
    })
    public ResponseEntity<?> seedCustomers() {
        Map<String, Object> responseMap = new HashMap<>();

        if (taskManager.isTaskRunning(CUSTOMER_SEED_TASK)) {
            responseMap.put("status", "already seeding");
        } else {
            taskManager.startTask(CUSTOMER_SEED_TASK, seedService.seedCustomers());
            responseMap.put("status", "seeding started");
        }

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/customer/seed")
    @ResponseBody
    @Operation(summary = "Check if the customer table is currently seeding")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evicted",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = {
                    @ExampleObject(value = """
                            {
                                "status": "not currently seeding"
                            }
                            """)
                }
            )
            }
        )
    })
    public ResponseEntity<?> isSeedingCustomers() {
        Map<String, Object> responseMap = new HashMap<>();

        if (taskManager.isTaskRunning(CUSTOMER_SEED_TASK)) {
            responseMap.put("status", "currently seeding");
        } else {
            responseMap.put("status", "not currently seeding");
        }

        return ResponseEntity.ok(responseMap);
    }

    @DeleteMapping("/customer/seed")
    @ResponseBody
    @Operation(summary = "Cancel customer table seeding")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evicted",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = {
                    @ExampleObject(value = """
                            {
                                "status": "cancelled"
                            }
                            """)
                }
            )
            }
        )
    })
    public ResponseEntity<?> cancelSeedCustomers() {
        Map<String, Object> responseMap = new HashMap<>();

        if (taskManager.isTaskRunning(CUSTOMER_SEED_TASK)) {
            taskManager.cancelTask(CUSTOMER_SEED_TASK);
            // A bit of an assumption, but ok for a demo
            responseMap.put("status", "cancelled");
        } else {
            responseMap.put("status", "not currently seeding");
        }

        return ResponseEntity.ok(responseMap);
    }

    @DeleteMapping("/customer/cache")
    @ResponseBody
    @Operation(summary = "Evict the entire customer cache")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evicted",
            content = {@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = {
                        @ExampleObject(value = """
                            {
                                "status": "evicted"
                            }
                            """)
                    }
                )
            }
        )
    })
    public ResponseEntity<?> evictCachedCustomers() {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            cacheManager.getCache("customer").clear();
            responseMap.put("status", "evicted");
        } catch (NullPointerException npe) {
            log.error("NullPointerException caught while evicting customer cache: " + npe.getMessage());
            responseMap.put("status", "already empty");
        }

        return ResponseEntity.ok(responseMap);
    }

    @DeleteMapping("/customer/cache/{customerId}")
    @ResponseBody
    @Operation(summary = "Evict the customer cache for a single customer ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evicted",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = {
                        @ExampleObject(value = """
                            {
                                "status": "evicted"
                            }
                            """)
                    }
                )
            }
        )
    })
    public ResponseEntity<?> evictSingleCachedCustomers(
        @Parameter(description = "Customer ID to evict") @PathVariable String customerId
    ) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            cacheManager.getCache("customer").evict(customerId);
            responseMap.put("status", "evicted");
        } catch (NullPointerException npe) {
            log.error("NullPointerException caught while evicting single customer cache: " + npe.getMessage());
            responseMap.put("status", "not cached");
        }

        return ResponseEntity.ok(responseMap);
    }
}
