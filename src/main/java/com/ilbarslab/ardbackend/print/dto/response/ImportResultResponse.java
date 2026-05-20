package com.ilbarslab.ardbackend.print.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultResponse {
    private int totalRows;
    private int imported;
    private int updated;
    private int warnings;
    private int errors;
    private List<String> errorMessages;
    private List<String> warningMessages;
}
