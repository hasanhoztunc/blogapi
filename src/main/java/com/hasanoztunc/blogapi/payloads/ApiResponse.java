package com.hasanoztunc.blogapi.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
final public class ApiResponse {
    public String message;
    private boolean status;
}