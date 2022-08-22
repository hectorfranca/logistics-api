package com.seniorsystems.logisticsapi.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.seniorsystems.logisticsapi.models.OrderItems;
import com.seniorsystems.logisticsapi.repositories.OrderItemsRepository;
import com.seniorsystems.logisticsapi.repositories.OrderRepository;

@Service
public class OrderItemsService {
    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Transactional
    public OrderItems save(OrderItems orderItems) {
        return orderItemsRepository.save(orderItems);
    }

    public Optional<OrderItems> findById(UUID id) {
        return orderItemsRepository.findById(id);
    }

    @Transactional
    public void delete(OrderItems orderItems) {
        orderItemsRepository.delete(orderItems);
    }

    public List<OrderItems> findAll() {
        return orderItemsRepository.findAll();
    }

    public List<OrderItems> findAllByOrderId(UUID orderId) {
        return orderItemsRepository.findAllByOrderId(orderId);
    }

    public Optional<OrderItems> findByOrderId(UUID orderId) {
        return orderItemsRepository.findByOrderId(orderId);
    }

    public Optional<OrderItems> findByIdAndOrderId(UUID orderId, UUID orderItemsId) {
        return orderItemsRepository.findByIdAndOrderId(orderId, orderItemsId);
    }

}
