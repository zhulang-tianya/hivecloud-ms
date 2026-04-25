package com.hivecloud.common.core.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> items;
    private Long total;
    private Integer page;
    private Integer size;

    public PageResult() {
    }

    public PageResult(List<T> items, Long total, Integer page, Integer size) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public static <T> PageResult<T> of(List<T> items, Long total, Integer page, Integer size) {
        return new PageResult<>(items, total, page, size);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0L, 1, 10);
    }

    public Integer getTotalPages() {
        if (size == null || size == 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / size);
    }

    public boolean hasNext() {
        return page != null && page < getTotalPages();
    }

    public boolean hasPrevious() {
        return page != null && page > 1;
    }
}
