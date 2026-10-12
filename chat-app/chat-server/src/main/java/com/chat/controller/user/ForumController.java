package com.chat.controller.user;

import com.chat.dto.ForumCommentDTO;
import com.chat.dto.ForumPostDTO;
import com.chat.entity.ForumCategory;
import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.ForumService;
import com.chat.vo.ForumCommentVO;
import com.chat.vo.ForumPostVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/forum")
@Slf4j
@Api(tags = "论坛相关接口")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/getCategoryList")
    @ApiOperation("查询帖子分类列表")
    public Result<List<ForumCategory>> getCategoryList() {
        List<ForumCategory> categories = forumService.getCategoryList();
        return Result.success(categories);
    }

    @PostMapping("/addPost")
    @ApiOperation("发布帖子")
    public Result addPost(@RequestBody ForumPostDTO forumPostDTO) {
        log.info("发布帖子：{}", forumPostDTO);
        forumService.addPost(forumPostDTO);
        return Result.success();
    }

    @GetMapping("/getPostList")
    @ApiOperation("分页查询帖子列表")
    public Result<PageResult> getPostList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          Long categoryId) {
        log.info("查询帖子列表：第{}页，每页{}条，分类{}", page, pageSize, categoryId);
        PageResult pageResult = forumService.getPostList(page, pageSize, categoryId);
        return Result.success(pageResult);
    }

    @GetMapping("/getPostById")
    @ApiOperation("查询帖子详情")
    public Result<ForumPostVO> getPostById(String postId) {
        log.info("查询帖子详情：{}", postId);
        return Result.success(forumService.getPostById(postId));
    }

    @DeleteMapping("/deletePost")
    @ApiOperation("删除我发布的帖子")
    public Result deletePost(String postId) {
        log.info("删除帖子：{}", postId);
        forumService.deletePost(postId);
        return Result.success();
    }

    @GetMapping("/getMyPost")
    @ApiOperation("分页查询我发布的帖子")
    public Result<PageResult> getMyPost(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询我发布的帖子：第{}页，每页{}条", page, pageSize);
        return Result.success(forumService.getMyPost(page, pageSize));
    }

    @PostMapping("/addComment")
    @ApiOperation("发表评论或回复")
    public Result addComment(@RequestBody ForumCommentDTO forumCommentDTO) {
        log.info("发表评论：{}", forumCommentDTO);
        forumService.addComment(forumCommentDTO);
        return Result.success();
    }

    @GetMapping("/getCommentList")
    @ApiOperation("查询帖子评论列表")
    public Result<List<ForumCommentVO>> getCommentList(String postId) {
        log.info("查询评论列表：{}", postId);
        return Result.success(forumService.getCommentList(postId));
    }

    @DeleteMapping("/deleteComment")
    @ApiOperation("删除我的评论")
    public Result deleteComment(String commentId) {
        log.info("删除评论：{}", commentId);
        forumService.deleteComment(commentId);
        return Result.success();
    }

    @PostMapping("/like")
    @ApiOperation("点赞或取消点赞")
    public Result like(Integer targetType, String targetId) {
        log.info("点赞操作：类型{}，目标{}", targetType, targetId);
        forumService.like(targetType, targetId);
        return Result.success();
    }
}