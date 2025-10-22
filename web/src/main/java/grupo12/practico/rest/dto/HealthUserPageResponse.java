package grupo12.practico.rest.dto;

import java.util.List;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;

public class HealthUserPageResponse {
    private List<HealthUserDTO> items;
    private int page;
    private int size;
    private long totalItems;
    private long totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public HealthUserPageResponse() {
    }

    public HealthUserPageResponse(List<HealthUserDTO> items, int page, int size, long totalItems, long totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        updateNavigationFlags();
    }

    public List<HealthUserDTO> getItems() {
        return items;
    }

    public void setItems(List<HealthUserDTO> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        updateNavigationFlags();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
        updateNavigationFlags();
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    private void updateNavigationFlags() {
        boolean hasPages = totalPages > 0;
        this.hasPrevious = hasPages && page > 1;
        this.hasNext = hasPages && page < totalPages;
    }
}
