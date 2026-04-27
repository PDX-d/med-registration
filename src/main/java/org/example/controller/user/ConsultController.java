package org.example.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.UserDTO;
import org.example.service.ConsultService;
import org.example.common.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "咨询文章")
@Slf4j
@RestController("userConsult")
@RequestMapping("/consult")
public class ConsultController {

	@Resource
	private ConsultService consultService;

	@GetMapping("/article/list")
	@ApiOperation(value = "文章列表", notes = "分页获取咨询文章列表")
	public Result listArticles(@RequestParam(defaultValue = "1") Long page,
							   @RequestParam(defaultValue = "10") Long pageSize,
							   @RequestParam(required = false) String keyword) {
		log.info("获取文章列表, page: {}, pageSize: {}", page, pageSize);
		return consultService.listArticles(page, pageSize, keyword);
	}

	@GetMapping("/article/{id}")
	@ApiOperation(value = "文章详情", notes = "获取文章详细信息")
	public Result getArticleDetail(@PathVariable Long id) {
		log.info("获取文章详情, id: {}", id);
		return consultService.getArticleDetail(id);
	}

	@PostMapping("/article/{id}/like")
	@RequirePermission("consult:article:like")
	@ApiOperation(value = "点赞/取消点赞", notes = "对文章进行点赞或取消点赞")
	public Result likeArticle(@PathVariable Long id) {
		log.info("点赞文章, id: {}", id);
		return consultService.likeArticle(id);
	}

	@PostMapping("/article/{id}/favorite")
	@RequirePermission("consult:article:favorite")
	@ApiOperation(value = "收藏/取消收藏", notes = "对文章进行收藏或取消收藏")
	public Result favoriteArticle(@PathVariable Long id) {
		log.info("收藏文章, id: {}", id);
		return consultService.favoriteArticle(id);
	}

	@GetMapping("/article/{id}/comments")
	@RequirePermission("consult:article:comments")
	@ApiOperation(value = "评论列表", notes = "获取文章的评论列表")
	public Result getComments(@PathVariable Long id,
							  @RequestParam(defaultValue = "1") Long page,
							  @RequestParam(defaultValue = "10") Long pageSize) {
		log.info("获取评论列表, articleId: {}", id);
		return consultService.getComments(id, page, pageSize);
	}

	@PostMapping("/article/{id}/comment")
	@RequirePermission("consult:article:comments")
	@ApiOperation(value = "发表评论", notes = "对文章发表评论")
	public Result addComment(@PathVariable Long id,
							 @RequestBody CommentRequest request) {
		log.info("发表评论, articleId: {}", id);
		return consultService.addComment(id, request.getContent(), request.getParentId());
	}

	@DeleteMapping("/comment/{id}")
	@ApiOperation(value = "删除评论", notes = "删除自己的评论")
	public Result deleteComment(@PathVariable Long id) {
		log.info("删除评论, id: {}", id);
		return consultService.deleteComment(id);
	}

	@GetMapping("/user/favorites")
	@ApiOperation(value = "我的收藏", notes = "获取当前用户收藏的文章列表")
	public Result getUserFavorites(@RequestParam(defaultValue = "1") Long page,
								   @RequestParam(defaultValue = "10") Long pageSize) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail("未登录");
		}
		log.info("获取我的收藏, userId: {}", user.getId());
		return consultService.getUserFavorites(user.getId(), page, pageSize);
	}

	@GetMapping("/user/comments")
	@ApiOperation(value = "我的评论", notes = "获取当前用户的评论列表")
	public Result getUserComments(@RequestParam(defaultValue = "1") Long page,
								  @RequestParam(defaultValue = "10") Long pageSize) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail("未登录");
		}
		log.info("获取我的评论, userId: {}", user.getId());
		return consultService.getUserComments(user.getId(), page, pageSize);
	}

	/**
	 * 评论请求 DTO
	 */
	@Data
	static class CommentRequest {
		private String content;
		private Long parentId;
	}
}
