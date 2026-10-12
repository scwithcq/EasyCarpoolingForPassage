package com.chat.controller.admin;

import com.chat.dto.ForumCommentPageQueryDTO;
import com.chat.dto.ForumPostPageQueryDTO;
import com.chat.entity.ForumCategory;
import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.AdminForumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端 - 论坛管理
 */
@RestController
@RequestMapping("/admin/forum")
@Slf4j
@Api(tags = "管理端-论坛管理接口")
public class AdminForumController {

    @Autowired
    private AdminForumService adminForumService;

    @GetMapping("/post/page")
    @ApiOperation("分页查询帖子")
    public Result<PageResult> pagePost(ForumPostPageQueryDTO forumPostPageQueryDTO) {
        log.info("管理端分页查询帖子：{}", forumPostPageQueryDTO);
        return Result.success(adminForumService.pageQueryPost(forumPostPageQueryDTO));
    }

    @DeleteMapping("/post/{postId}")
    @ApiOperation("删除帖子")
    public Result deletePost(@PathVariable String postId) {
        log.info("管理端删除帖子：{}", postId);
        adminForumService.deletePost(postId);
        return Result.success();
    }

    @PostMapping("/post/status/{status}")
    @ApiOperation("修改帖子状态")
    public Result updatePostStatus(@PathVariable Integer status, String postId) {
        log.info("管理端修改帖子{}状态为：{}", postId, status);
        adminForumService.updatePostStatus(postId, status);
        return Result.success();
    }

    @GetMapping("/comment/page")
    @ApiOperation("分页查询评论")
    public Result<PageResult> pageComment(ForumCommentPageQueryDTO forumCommentPageQueryDTO) {
        log.info("管理端分页查询评论：{}", forumCommentPageQueryDTO);
        return Result.success(adminForumService.pageQueryComment(forumCommentPageQueryDTO));
    }

    @DeleteMapping("/comment/{commentId}")
    @ApiOperation("删除评论")
    public Result deleteComment(@PathVariable String commentId) {
        log.info("管理端删除评论：{}", commentId);
        adminForumService.deleteComment(commentId);
        return Result.success();
    }

    @PostMapping("/comment/status/{status}")
    @ApiOperation("修改评论状态")
    public Result updateCommentStatus(@PathVariable Integer status, String commentId) {
        log.info("管理端修改评论{}状态为：{}", commentId, status);
        adminForumService.updateCommentStatus(commentId, status);
        return Result.success();
    }

    @GetMapping("/category/list")
    @ApiOperation("查询全部分类")
    public Result<List<ForumCategory>> categoryList() {
        return Result.success(adminForumService.getCategoryList());
    }

    @PostMapping("/category")
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody ForumCategory forumCategory) {
        log.info("管理端新增分类：{}", forumCategory);
        adminForumService.addCategory(forumCategory);
        return Result.success();
    }

    @PutMapping("/category")
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody ForumCategory forumCategory) {
        log.info("管理端修改分类：{}", forumCategory);
        adminForumService.updateCategory(forumCategory);
        return Result.success();
    }

    @DeleteMapping("/category/{id}")
    @ApiOperation("删除分类")
    public Result deleteCategory(@PathVariable Long id) {
        log.info("管理端删除分类：{}", id);
        adminForumService.deleteCategory(id);
        return Result.success();
    }
}