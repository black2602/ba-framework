package com.blackangel.baframework.core.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kimjeonghun on 2018. 7. 7..
 */

@Getter
@Setter
@NoArgsConstructor
public class ListModelMetaData {

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("pageableCount")
    private int pageableCount;

    @SerializedName("is_end")
    private boolean isEnd;

}
