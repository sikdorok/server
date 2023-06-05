package com.ddd.chulsi.infrastructure.config.datasource;

import java.util.List;

// 여러개의 DataSource를 순서대로 사용하여 로드벨런싱 하기 위해
public class CircularList<T> {

    private List<T> list;
    private Integer counter = 0;

    public CircularList(List<T> list) {
        this.list = list;
    }

    public T getOne() {
        if (counter + 1 >= list.size()) {
            counter = -1;
        }
        return list.get(++counter);
    }

}