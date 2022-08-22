package com.seniorsystems.logisticsapi.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.seniorsystems.logisticsapi.dtos.OrderItemsDto;
import com.seniorsystems.logisticsapi.models.Item;
import com.seniorsystems.logisticsapi.models.Order;
import com.seniorsystems.logisticsapi.models.OrderItems;
import com.seniorsystems.logisticsapi.services.ItemService;
import com.seniorsystems.logisticsapi.services.OrderItemsService;
import com.seniorsystems.logisticsapi.services.OrderService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/orders")
public class OrderItemsController {

    @Autowired
    private OrderItemsService orderItemsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Object> create(@PathVariable(value = "orderId") UUID orderId,
            @RequestBody @Valid OrderItemsDto orderItemsDto) {

        Optional<Order> orderOptional = orderService.findById(orderId);
        Optional<Item> itemOptional = itemService.findById(orderItemsDto.getItemId());

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } else if (!itemOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found.");
        } else if (orderItemsDto.getQuantity() < 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: percentualDiscount must be positive.");
        }

        OrderItems orderItems = new OrderItems();
        BeanUtils.copyProperties(orderItemsDto, orderItems);

        orderItems.getOrder().setId(orderId);
        orderItems.getItem().setId(orderItemsDto.getItemId());

        Optional<Item> item = itemService.findById(orderItemsDto.getItemId());
        orderItems.setTotalValue(orderItems.getQuantity() * item.get().getValue());

        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemsService.save(orderItems));
    }

    @GetMapping("/{orderId}/items/{orderItemsId}")
    public ResponseEntity<Object> read(@PathVariable(value = "orderId") UUID orderId,
            @PathVariable(value = "orderItemsId") UUID orderItemsId) {
        Optional<OrderItems> orderItemsOptional = orderItemsService.findByIdAndOrderId(orderItemsId, orderId);

        if (!orderItemsOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Oder Items not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(orderItemsOptional.get());
    }

    @PutMapping("/{orderId}/items/{orderItemsId}")
    public ResponseEntity<Object> update(@PathVariable(value = "orderId") UUID orderId,
            @PathVariable(value = "orderItemsId") UUID orderItemsId,
            @RequestBody @Valid OrderItemsDto orderItemsDto) {

        Optional<Order> orderOptional = orderService.findById(orderId);
        Optional<Item> itemOptional = itemService.findById(orderItemsDto.getItemId());
        Optional<OrderItems> orderItemsOptional = orderItemsService.findByIdAndOrderId(orderItemsId, orderId);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } else if (!itemOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found.");
        } else if (!orderItemsOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order Items not found.");
        } else if (orderItemsDto.getQuantity() < 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: percentualDiscount must be positive.");
        }

        OrderItems orderItems = new OrderItems();
        BeanUtils.copyProperties(orderItemsDto, orderItems);

        orderItems.setId(orderItemsOptional.get().getId());
        orderItems.getOrder().setId(orderItemsOptional.get().getOrder().getId());
        orderItems.getItem().setId(orderItemsDto.getItemId());

        Optional<Item> item = itemService.findById(orderItemsDto.getItemId());
        orderItems.setTotalValue(orderItems.getQuantity() * item.get().getValue());

        return ResponseEntity.status(HttpStatus.OK).body(orderItemsService.save(orderItems));
    }

    @DeleteMapping("/{orderId}/items/{orderItemsId}")
    public ResponseEntity<Object> delete(@PathVariable(value = "orderId") UUID orderId,
            @PathVariable(value = "orderItemsId") UUID orderItemsId) {
        Optional<OrderItems> orderItemsOptional = orderItemsService.findByIdAndOrderId(orderItemsId, orderId);

        if (!orderItemsOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order Items not found.");
        }

        orderItemsService.delete(orderItemsOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body("Order Items successfully deleted.");
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<Object> list(@PathVariable(value = "orderId") UUID orderId) {
        List<OrderItems> orderItems = orderItemsService.findAllByOrderId(orderId);

        if (orderItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no order items registered.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(orderItems);
    }

}
