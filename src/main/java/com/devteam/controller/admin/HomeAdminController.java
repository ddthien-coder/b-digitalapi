package com.devteam.controller.admin;


import com.devteam.annotation.OperationLogger;
import com.devteam.entity.Home;
import com.devteam.model.vo.Result;
import com.devteam.service.HomeService;
import com.devteam.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class HomeAdminController {

    @Autowired
    HomeService homeService;

    @GetMapping("/homes")
    public Result homes(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "id desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<Home> pageInfo = new PageInfo<>(homeService.getList());
        return Result.ok("Request succeeded", pageInfo);
    }

    @OperationLogger("add homes")
    @PostMapping("/home")
    public Result saveHome(@RequestBody Home home) {
        return getResult(home, "save");
    }

    public Result getResult(Home home, String type) {
        if(StringUtils.isEmpty(home.getTitle())) {
            return Result.error("Title cannot be empty");
        }
        if("save".equals(type)) {
            homeService.saveHome(home);
            return Result.ok("add successfully");
        }else  {
            homeService.updateHome(home);
            return Result.ok("updated successfully");
        }
    }

    @OperationLogger("update homes")
    @PutMapping("/home")
    public Result updateHome(@RequestBody Home home) {
        return getResult(home, "update");
    }

    @OperationLogger("delete home")
    @DeleteMapping("/home")
    public Result delete(@RequestParam Long id) {
        homeService.deleteHomeById(id);
        return Result.ok("Successfully deleted");
    }
}
