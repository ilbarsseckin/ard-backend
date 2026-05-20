package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.entity.Order;
import com.ilbarslab.ardbackend.print.entity.OrderStatusHistory;
import com.ilbarslab.ardbackend.print.entity.User;
import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import com.ilbarslab.ardbackend.print.repository.OrderRepository;
import com.ilbarslab.ardbackend.print.repository.OrderStatusHistoryRepository;
import com.ilbarslab.ardbackend.print.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderTrackingService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;

    // Sipariş durumunu güncelle ve geçmişe kaydet
    @Transactional
    public Order updateStatus(UUID orderId, OrderStatus newStatus, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        order.setStatus(newStatus);
        orderRepository.save(order);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .note(note)
                .build();
        historyRepository.save(history);

        log.info("Sipariş durumu güncellendi: {} → {}", orderId, newStatus);
        return order;
    }

    // Sipariş durum geçmişini getir
    public List<OrderStatusHistory> getHistory(String email, UUID orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu sipariş size ait değil");
        }

        return historyRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
    }
}