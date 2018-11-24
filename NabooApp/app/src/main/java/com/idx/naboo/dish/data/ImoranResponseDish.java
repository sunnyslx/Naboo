package com.idx.naboo.dish.data;

import com.google.gson.annotations.SerializedName;

/**
 * 第一层json数据
 * Created by sunny on 18-3-21.
 */

public class ImoranResponseDish {
        @SerializedName("data")
        private DishData dishData;

        public DishData getDishData() {
          return dishData;
      }
}
