package com.fpt.capstone.tourism.dto.general;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagingDTO<T> implements Serializable {
    private int page;
    private int size;
    private long total;
    private List<T> items; // Sửa từ "T items" thành "List<T> items"
}
