package com.jobpulse.jobcreationlisting.dto.repository.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResult {
    private boolean success;
    private String message;
}
