package com.ilbarslab.ardbackend.print.dto.request;

import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotNull
    private OrderStatus status;

    private String note;
}