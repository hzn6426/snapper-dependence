package com.baomibing.authority.controller;

import com.baomibing.authority.dto.GateLimitDto;
import com.baomibing.authority.service.SysGateLimitService;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * GateLimitController
 *
 * @author zening 2023/8/21 14:40
 * @version 1.0.0
 **/
@RestController
@RequestMapping(path = {"/api/gateLimit"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class GateLimitController extends MBaseController<GateLimitDto> {

    @Autowired private SysGateLimitService gateLimitService;


    @PostMapping("search")
    public R<GateLimitDto> search(PageQuery<GateLimitDto> pageQuery) {
        return R.build(gateLimitService.searchGateLimit(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize()));
    }

    @GetMapping("/{id}")
    public GateLimitDto getGateLimit(@PathVariable("id") String id) {
        return gateLimitService.getGateLimitById(id);
    }

    @PostMapping
    public void saveGateLimit(@RequestBody GateLimitDto limit) {
        gateLimitService.saveGateLimit(limit);
    }

    @PutMapping
    public void updateGateLimit(@RequestBody GateLimitDto limit) {
        gateLimitService.updateGateLimit(limit);
    }

    @DeleteMapping
    public void deleteGateLimit(@RequestBody Set<String> ids) {
        gateLimitService.deleteLimits(ids);
    }

    @PostMapping("use")
    public void use(@RequestBody Set<String> ids) {
        gateLimitService.doUse(ids);
    }

    @PostMapping("stop")
    public void stop(@RequestBody Set<String> ids) {
        gateLimitService.doStop(ids);
    }

    @PostMapping("refreshCache")
    public void refreshCache() {
        gateLimitService.refreshLimitCache();
    }
}
