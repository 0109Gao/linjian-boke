package com.linjian.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linjian.blog.common.Result;
import com.linjian.blog.entity.SiteConfig;
import com.linjian.blog.mapper.SiteConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点配置接口
 */
@RestController
@RequestMapping("/api/site")
@RequiredArgsConstructor
public class SiteController {

    private final SiteConfigMapper siteConfigMapper;

    @GetMapping("/config")
    public Result<Map<String, String>> config() {
        List<SiteConfig> list = siteConfigMapper.selectList(null);
        Map<String, String> map = new HashMap<>();
        for (SiteConfig config : list) {
            map.put(config.getConfigKey(), config.getConfigValue());
        }
        return Result.success(map);
    }
}
