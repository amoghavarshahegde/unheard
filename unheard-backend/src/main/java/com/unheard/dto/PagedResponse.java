package com.unheard.dto;

import java.util.List;

public class PagedResponse<T> {
    private List<T> content;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;

    public PagedResponse() {}

    public PagedResponse(List<T> content, Integer page, Integer size, Long totalElements, Integer totalPages, Boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private List<T> content;
        private Integer page;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
        private Boolean last;

        public Builder<T> content(List<T> content) { this.content = content; return this; }
        public Builder<T> page(Integer page) { this.page = page; return this; }
        public Builder<T> size(Integer size) { this.size = size; return this; }
        public Builder<T> totalElements(Long totalElements) { this.totalElements = totalElements; return this; }
        public Builder<T> totalPages(Integer totalPages) { this.totalPages = totalPages; return this; }
        public Builder<T> last(Boolean last) { this.last = last; return this; }

        public PagedResponse<T> build() {
            return new PagedResponse<>(content, page, size, totalElements, totalPages, last);
        }
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    public Boolean getLast() { return last; }
    public void setLast(Boolean last) { this.last = last; }
}
