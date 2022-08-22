package com.seniorsystems.logisticsapi.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.seniorsystems.logisticsapi.models.Order;
import com.seniorsystems.logisticsapi.repositories.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findById(UUID id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public void delete(Order order) {
        orderRepository.delete(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
