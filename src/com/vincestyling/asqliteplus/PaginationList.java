/**
 * Copyright (C) 2015 Vince Styling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vincestyling.asqliteplus;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link java.util.ArrayList} with four pagination informations, used to
 * return the SQL's resultset and pagination info as additional.
 *
 * @param <T> The generic type for datasource.
 * @see com.vincestyling.asqliteplus.DBOverseer#getPaginationList
 */
public class PaginationList<T> extends ArrayList<T> {
    private int totalPageCount;
    private int totalItemCount;
    private int pageItemCount;
    private int curPageNo;

    public PaginationList() {
        super();
    }

    public PaginationList(int capacity) {
        super(capacity);
    }

    public PaginationList(int pageNo, int pageItemCount, int totalItemCount) {
        this(0);
        setPagination(pageNo, pageItemCount, totalItemCount);
    }

    public PaginationList(List<T> ts, int pageNo, int pageItemCount, int totalItemCount) {
        setPagination(pageNo, pageItemCount, totalItemCount);
        addAll(ts);
    }

    public PaginationList<T> setPagination(int pageNo, int pageItemCount, int totalItemCount) {
        this.curPageNo = pageNo;
        this.pageItemCount = pageItemCount;
        this.totalItemCount = totalItemCount;
        // calculate the number of pages
        this.totalPageCount = (totalItemCount - 1) / pageItemCount + 1;
        return this;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public int getPageItemCount() {
        return pageItemCount;
    }

    public void setPageItemCount(int pageItemCount) {
        this.pageItemCount = pageItemCount;
    }

    public int getCurPageNo() {
        return curPageNo;
    }

    public void setCurPageNo(int curPageNo) {
        this.curPageNo = curPageNo;
    }

    public boolean hasNextPage() {
        return curPageNo < totalPageCount;
    }
}
