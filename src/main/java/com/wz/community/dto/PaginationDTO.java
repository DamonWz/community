package com.wz.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO <T> {

    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showLastPage;
    private Integer page;
    private Integer totalPage;
    private List<Integer> pages = new ArrayList<>();

    public void setPagination(Integer totalPage, Integer page) {
        //总页数
        this.totalPage = totalPage;
        //当前页数
        this.page = page;

        pages.add(page);

        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0, page - i);
            }
            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        //是否展示上一页
        if (page == 1) {
            this.showPrevious = false;
        } else {
            this.showPrevious = true;
        }

        //是否展示下一页
        if (page == totalPage) {
            this.showNext = false;
        } else {
            this.showNext = true;
        }

        //是否展示第一页
        if (pages.contains(1)) {
            this.showFirstPage = false;
        } else {
            this.showFirstPage = true;
        }

        //是否展示最后一页
        if (pages.contains(totalPage)) {
            this.showLastPage = false;
        } else {
            this.showLastPage = true;
        }
    }
}
