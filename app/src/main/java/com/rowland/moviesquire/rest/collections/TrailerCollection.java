/*
 * Copyright 2015 Oti Rowland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package com.rowland.moviesquire.rest.collections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rowland.moviesquire.rest.models.Trailer;

import java.util.List;

/**
 * Created by Rowland on 12/11/2015.
 */
public class TrailerCollection {

    // Gson annotations
    @SerializedName("results")
    @Expose
    public List<Trailer> results;
    // Gson annotations
    @SerializedName("id")
    @Expose
    private Integer id;
    // Gson annotations
    @SerializedName("page")
    @Expose
    private Integer page;
    // Gson annotations
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    // Gson annotations
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;

    /**
     * @return The results
     */
    public List<Trailer> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(List<Trailer> results) {
        this.results = results;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return The totalResults
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     * @param totalResults The total_results
     */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    /**
     * @return The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

}
