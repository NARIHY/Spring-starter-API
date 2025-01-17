package com.base_spring_boot.com.tmoto.base.utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class SortUtils {
    public static Sort convertSortParameter(String sortParams) {
        if (StringUtils.isEmpty(sortParams)) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order orderT;
        String[] tokenArray = sortParams.split(",");
        for (String s : tokenArray) {
            orderT = getStringAsOrder(s);
            orders.add(orderT);
        }
        return Sort.by(orders);
    }

    private static Sort.Order getStringAsOrder(String string) {
        Sort.Order order = null;
        if (string.startsWith("-")) {
            // descending order
            order = new Sort.Order(Sort.Direction.DESC, string.substring(1));
        } else if (string.startsWith("+")) {
            order = Sort.Order.by(string.substring(1));
        } else {
            order = Sort.Order.by(string);
        }
        return order;
    }

}
