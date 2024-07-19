package cn.codest.mybatispager.model;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 分页查询对象
 */
public class Pager<T> implements Serializable {

    private static final long serialVersionUID = -6461578698889066416L;

    /**
     * 页码
     */
    private Integer no;

    /**
     * 每页数据量
     */
    private Integer size;

    private static final Integer DEFAULT_SIZE = 10;

    /**
     * 总数据量
     */
    private Long total;

    /**
     * 数据集合
     */
    private List<T> data;

    public Pager() {
    }

    protected Pager(Integer no, Integer size) {
        this.no = no;
        this.size = size;
    }

    public static Pager of(Integer no, Integer size) {
        size = Optional.ofNullable(size).isPresent() && size > 0? size : DEFAULT_SIZE;
        return new Pager(no, size);
    }

    public static Pager of(Integer no) {
        return new Pager(no, DEFAULT_SIZE);
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Pager{" +
                "no=" + no +
                ", size=" + size +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}
