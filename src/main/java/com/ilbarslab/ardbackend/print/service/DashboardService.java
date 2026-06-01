package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.DashboardStatsResponse;
import com.ilbarslab.ardbackend.print.dto.response.DashboardStatsResponse.*;
import com.ilbarslab.ardbackend.print.entity.Order;
import com.ilbarslab.ardbackend.print.entity.OrderItem;
import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import com.ilbarslab.ardbackend.print.repository.OrderRepository;
import com.ilbarslab.ardbackend.print.repository.ProductTypeRepository;
import com.ilbarslab.ardbackend.print.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductTypeRepository productTypeRepository;

    private static final Set<OrderStatus> PENDING_STATUSES = Set.of(
            OrderStatus.PENDING, OrderStatus.PAID,
            OrderStatus.REVIEWING, OrderStatus.PRINTING
    );

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        List<Order> all = orderRepository.findAllByOrderByCreatedAtDesc();

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);            // son 7 gün dahil
        LocalDate monthStart = today.withDayOfMonth(1);

        // ─── Toplam metrikler ───
        int todayOrders = 0, weekOrders = 0, monthOrders = 0;
        BigDecimal todayRev = BigDecimal.ZERO, weekRev = BigDecimal.ZERO, monthRev = BigDecimal.ZERO;

        for (Order o : all) {
            if (o.getCreatedAt() == null) continue;
            LocalDate d = o.getCreatedAt().toLocalDate();
            BigDecimal price = o.getTotalPrice() == null ? BigDecimal.ZERO : o.getTotalPrice();

            if (d.equals(today))       { todayOrders++; todayRev = todayRev.add(price); }
            if (!d.isBefore(weekAgo))  { weekOrders++;  weekRev  = weekRev.add(price); }
            if (!d.isBefore(monthStart)){ monthOrders++; monthRev = monthRev.add(price); }
        }

        // ─── Durum dağılımı ───
        Map<OrderStatus, Integer> statusBreakdown = new EnumMap<>(OrderStatus.class);
        for (OrderStatus s : OrderStatus.values()) statusBreakdown.put(s, 0);
        for (Order o : all) {
            statusBreakdown.merge(o.getStatus(), 1, Integer::sum);
        }
        int pendingActions = PENDING_STATUSES.stream()
                .mapToInt(s -> statusBreakdown.getOrDefault(s, 0))
                .sum();

        // ─── Son 7 günlük seri ───
        Map<LocalDate, int[]> dayCount = new TreeMap<>();    // [count]
        Map<LocalDate, BigDecimal> dayRev = new TreeMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = weekAgo.plusDays(i);
            dayCount.put(d, new int[]{0});
            dayRev.put(d, BigDecimal.ZERO);
        }
        for (Order o : all) {
            if (o.getCreatedAt() == null) continue;
            LocalDate d = o.getCreatedAt().toLocalDate();
            if (dayCount.containsKey(d)) {
                dayCount.get(d)[0]++;
                dayRev.merge(d, o.getTotalPrice() == null ? BigDecimal.ZERO : o.getTotalPrice(), BigDecimal::add);
            }
        }
        List<DailySales> last7 = dayCount.keySet().stream()
                .map(d -> DailySales.builder()
                        .date(d)
                        .orders(dayCount.get(d)[0])
                        .revenue(dayRev.get(d))
                        .build())
                .toList();

        // ─── Son 5 sipariş ───
        List<RecentOrder> recent = all.stream().limit(5).map(o -> RecentOrder.builder()
                .id(o.getId())
                .customerName(o.getUser() != null ? o.getUser().getName() : "—")
                .status(o.getStatus())
                .totalPrice(o.getTotalPrice())
                .createdAt(o.getCreatedAt())
                .build()).toList();

        // ─── En çok satan ürünler (top 5) ───
        Map<String, int[]> productCount = new HashMap<>();      // slug → [count]
        Map<String, BigDecimal> productRev = new HashMap<>();
        for (Order o : all) {
            if (o.getItems() == null) continue;
            for (OrderItem it : o.getItems()) {
                String slug = it.getProductType();
                productCount.computeIfAbsent(slug, k -> new int[]{0})[0] += it.getQuantity();
                BigDecimal line = it.getUnitPrice() == null
                        ? BigDecimal.ZERO
                        : it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
                productRev.merge(slug, line, BigDecimal::add);
            }
        }
        List<TopProduct> top = productCount.entrySet().stream()
                .map(e -> TopProduct.builder()
                        .slug(e.getKey())
                        .orderCount(e.getValue()[0])
                        .revenue(productRev.getOrDefault(e.getKey(), BigDecimal.ZERO))
                        .build())
                .sorted(Comparator.comparing(TopProduct::getOrderCount).reversed())
                .limit(5)
                .toList();

        return DashboardStatsResponse.builder()
                .todayOrders(todayOrders)
                .todayRevenue(todayRev)
                .weekOrders(weekOrders)
                .weekRevenue(weekRev)
                .monthOrders(monthOrders)
                .monthRevenue(monthRev)
                .totalCustomers((int) userRepository.count())
                .totalProducts((int) productTypeRepository.count())
                .activeProducts(productTypeRepository.findByIsActiveTrue().size())
                .pendingActionsCount(pendingActions)
                .statusBreakdown(statusBreakdown)
                .last7Days(last7)
                .recentOrders(recent)
                .topProducts(top)
                .build();
    }
}