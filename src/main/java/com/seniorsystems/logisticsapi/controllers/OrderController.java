package com.seniorsystems.logisticsapi.controllers;

import java.sql.Timestamp;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.seniorsystems.logisticsapi.dtos.OrderDto;
import com.seniorsystems.logisticsapi.models.Order;
import com.seniorsystems.logisticsapi.models.OrderItems;
import com.seniorsystems.logisticsapi.services.OrderItemsService;
import com.seniorsystems.logisticsapi.services.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemsService orderItemsService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid OrderDto orderDto) {
        if (orderDto.getNumber() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: number must be positive.");
        } else if (orderDto.getDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: date must be less than the current date.");
        } else if (orderDto.getPercentualDiscount() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: percentualDiscount must be positive.");
        } else if (orderDto.getTotalValue() != null && orderDto.getTotalValue() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: totalValue must be positive.");
        }

        Order order = new Order();
        BeanUtils.copyProperties(orderDto, order);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.save(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> read(@PathVariable(value = "id") UUID id) {
        Optional<Order> orderOptional = orderService.findById(id);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(orderOptional.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable(value = "id") UUID id,
            @RequestBody @Valid OrderDto orderDto) {
        if (orderDto.getNumber() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: number must be positive.");
        } else if (orderDto.getDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: date must be less than the current date.");
        } else if (orderDto.getPercentualDiscount() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: percentualDiscount must be positive.");
        } else if (orderDto.getTotalValue() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: totalValue must be positive.");
        }

        Optional<Order> orderOptional = orderService.findById(id);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        Order Order = new Order();
        BeanUtils.copyProperties(orderDto, Order);
        Order.setId(orderOptional.get().getId());

        return ResponseEntity.status(HttpStatus.OK).body(orderService.save(Order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id) {
        Optional<Order> orderOptional = orderService.findById(id);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        orderService.delete(orderOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body("Order successfully deleted.");
    }

    @GetMapping
    public ResponseEntity<Object> list() {
        List<Order> orders = orderService.findAll();

        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "There are no orders registered.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @PutMapping("/{orderId}/close")
    public ResponseEntity<Object> applyDiscount(@PathVariable(value = "orderId") UUID id,
            @RequestBody OrderDto orderDto) {

        Optional<Order> orderOptional = orderService.findById(id);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } else if (!orderDto.getOrder().equals(orderOptional.get().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: order not found.");
        } else if (orderDto.getPercentualDiscount() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: percentualDiscount must be positive.");
        } else if (orderOptional.get().getOrderItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: order without items.");
        }

        List<OrderItems> orderItems = orderItemsService.findAllByOrderId(id);
        Double totalValue = 0.0d;

        for (OrderItems orderItem : orderItems) {
            if (orderItem.getItem().getType() == 'P') {
                Double discountValue = orderItem.getTotalValue() * (orderDto.getPercentualDiscount() / 100);
                totalValue += orderItem.getTotalValue() - discountValue;
            } else {
                totalValue += orderItem.getTotalValue();
            }
        }

        Order order = new Order();
        BeanUtils.copyProperties(orderDto, order);
        order.setId(orderOptional.get().getId());
        order.setNumber(orderOptional.get().getNumber());
        order.setDate(orderOptional.get().getDate());
        order.setTotalValue(totalValue);
        order.setOrderItems(orderOptional.get().getOrderItems());

        return ResponseEntity.status(HttpStatus.OK).body(orderService.save(order));
    }

}