package com.tqnee.KamS3r.Interfaces;

import com.tqnee.KamS3r.Model.CategoriesModel;

/**
 * Created by ramzy on 9/19/2017.
 */

public interface ICategoryItemClicked {
    void onCategoryItemClicked(CategoriesModel categoriesModel, Boolean isCategorySelected);
}
