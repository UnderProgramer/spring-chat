package com.example.springChat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CursorResponse<T> {
    private List<T> data;
    private Long nextCursor;
    private boolean hasNext;
}