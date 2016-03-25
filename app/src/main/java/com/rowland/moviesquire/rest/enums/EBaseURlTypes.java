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
 */

package com.rowland.moviesquire.rest.enums;

/**
 * Created by Oti Rowland on 12/12/2015.
 */
public enum EBaseURlTypes {

    // All the url that the application will query
    MOVIE_API_BASE_URL("http://api.themoviedb.org"),
    MOVIE_API_IMAGE_BASE_URL("http://image.tmdb.org/t/p/"),
    YOUTUBE_VIDEO_URL("http://www.youtube.com/watch?v=%s"),
    YOUTUBE_THUMNAIL_URL("http://img.youtube.com/vi/%s/default.jpg");

    private String URLType;

    private EBaseURlTypes(String s) {
        URLType = s;
    }

    // Get the url corresponding to the enum
    public String getUrlType() {
        return URLType;
    }
}
