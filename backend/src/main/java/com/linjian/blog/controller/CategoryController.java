package com.linjian.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linjian.blog.common.Result;
import com.linjian.blog.entity.Category;
import com.linjian.blog.entity.Tag;
import com.linjian.blog.mapper.CategoryMapper;
import com.linjian.blog.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类/标签接口
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)));
    }

    @GetMapping("/tags")
    public Result<List<Tag>> tags() {
        return Result.success(tagMapper.selectList(null));
    }
}
