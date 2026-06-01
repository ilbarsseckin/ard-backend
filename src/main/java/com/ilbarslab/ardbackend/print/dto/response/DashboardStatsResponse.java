package com.ilbarslab.ardbackend.print.dto.response;

import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private int todayOrders;
    private BigDecimal todayRevenue;
    private int weekOrders;
    private BigDecimal weekRevenue;
    private int monthOrders;
    private BigDecimal monthRevenue;

    private int totalCustomers;
    private int totalProducts;
    private int activeProducts;

    private int pendingActionsCount;          // PENDING + PAID + REVIEWING + PRINTING

    private Map<OrderStatus, Integer> statusBreakdown;
    private List<DailySales> last7Days;
    private List<RecentOrder> recentOrders;
    private List<TopProduct> topProducts;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DailySales {
        private LocalDate date;
        private int orders;
        private BigDecimal revenue;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RecentOrder {
        private UUID id;
        private String customerName;
        private OrderStatus status;
        private BigDecimal totalPrice;
        private java.time.LocalDateTime createdAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopProduct {
        private String slug;
        private int orderCount;
        private BigDecimal revenue;
    }
}