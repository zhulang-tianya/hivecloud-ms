package com.hivecloud.common.core.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果封装类
 * <p>
 * 用于封装分页查询的结果，包含数据列表、总数、页码、页大小等信息
 * 支持泛型数据返回，便于前端统一处理分页逻辑
 * </p>
 *
 * @param <T> 数据类型
 * @author HiveCloud Team
 * @date 2026-04-27
 * @since 1.0.0
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> items;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 默认构造函数
     */
    public PageResult() {
    }

    /**
     * 带参数的构造函数
     *
     * @param items 数据列表
     * @param total 总记录数
     * @param page 当前页码
     * @param size 每页大小
     */
    public PageResult(List<T> items, Long total, Integer page, Integer size) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    /**
     * 创建分页结果
     *
     * @param items 数据列表
     * @param total 总记录数
     * @param page 当前页码
     * @param size 每页大小
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> items, Long total, Integer page, Integer size) {
        return new PageResult<>(items, total, page, size);
    }

    /**
     * 创建空的分页结果
     *
     * @param <T> 数据类型
     * @return 空的分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0L, 1, 10);
    }

    /**
     * 计算总页数
     *
     * @return 总页数
     */
    public Integer getTotalPages() {
        if (size == null || size == 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / size);
    }

    /**
     * 判断是否有下一页
     *
     * @return 有下一页返回 true，否则返回 false
     */
    public boolean hasNext() {
        return page != null && page < getTotalPages();
    }

    /**
     * 判断是否有上一页
     *
     * @return 有上一页返回 true，否则返回 false
     */
    public boolean hasPrevious() {
        return page != null && page > 1;
    }
}
