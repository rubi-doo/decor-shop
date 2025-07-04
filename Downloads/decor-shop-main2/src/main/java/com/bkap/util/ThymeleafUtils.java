package com.bkap.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bkap.entity.Category;

public class ThymeleafUtils {

    public String buildCategoryPath(Category category) {
        List<String> path = new ArrayList<>();
        Category current = category;
        while (current != null) {
            path.add(current.getName());
            current = current.getParent();
        }
        Collections.reverse(path);
        return String.join(" › ", path);
    }

    public boolean isLeaf(Category category) {
        return category.getChildren() == null || category.getChildren().isEmpty();
    }
}
