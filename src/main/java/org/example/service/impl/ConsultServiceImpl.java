package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.CommentMapper;
import org.example.mapper.ConsultMapper;
import org.example.mapper.UserFavoriteMapper;
import org.example.mapper.UserLikeMapper;
import org.example.common.result.Result;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.Comment;
import org.example.pojo.entity.Post;
import org.example.pojo.entity.UserFavorite;
import org.example.pojo.entity.UserLike;
import org.example.service.ConsultService;
import org.example.common.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.common.constants.MessageConstant.USER_NOT_LOGIN;

@Service
@Slf4j
public class ConsultServiceImpl extends ServiceImpl<ConsultMapper, Post> implements ConsultService {

	@Resource
	private ConsultMapper consultMapper;

	@Resource
	private CommentMapper commentMapper;

	@Resource
	private UserLikeMapper userLikeMapper;

	@Resource
	private UserFavoriteMapper userFavoriteMapper;

	@Override
	@Transactional
	public Result addArticle(Post post) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		post.setUserId(user.getId());
		post.setViewCount(0);
		post.setLikeCount(0);
		post.setFavoriteCount(0);
		post.setCommentCount(0);
		post.setStatus(post.getStatus() == null ? 1 : post.getStatus());
		post.setCreateTime(LocalDateTime.now());
		post.setUpdateTime(LocalDateTime.now());
		consultMapper.insert(post);
		return Result.ok();
	}

	@Override
	@Transactional
	public Result updateArticle(Post post) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		post.setUpdateTime(LocalDateTime.now());
		int count = consultMapper.updateById(post);
		if (count <= 0) {
			return Result.fail("更新失败");
		}
		return Result.ok();
	}

	@Override
	@Transactional
	public Result deleteArticle(Long id) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		Post post = consultMapper.selectById(id);
		if (post == null) {
			return Result.fail("文章不存在");
		}
		post.setStatus(2); // 标记为已删除
		post.setUpdateTime(LocalDateTime.now());
		consultMapper.updateById(post);
		return Result.ok();
	}

	@Override
	public Result listArticles(Long page, Long pageSize, String keyword) {
		Page<Post> pageInfo = new Page<>(page, pageSize);
		LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Post::getStatus, 1); // 只查询已发布的
		if (keyword != null && !keyword.trim().isEmpty()) {
			wrapper.like(Post::getTitle, keyword)
					.or()
					.like(Post::getContent, keyword);
		}
		wrapper.orderByDesc(Post::getCreateTime);
		IPage<Post> result = consultMapper.selectPage(pageInfo, wrapper);
		return Result.ok(result.getRecords(), result.getTotal());
	}

	@Override
	public Result getArticleDetail(Long id) {
		Post post = consultMapper.selectById(id);
		if (post == null || post.getStatus() != 1) {
			return Result.fail("文章不存在");
		}
		// 增加浏览量
		post.setViewCount(post.getViewCount() + 1);
		consultMapper.updateById(post);
		return Result.ok(post);
	}

	@Override
	@Transactional
	public Result likeArticle(Long id) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		Post post = consultMapper.selectById(id);
		if (post == null) {
			return Result.fail("文章不存在");
		}

		// 检查是否已经点赞
		LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(UserLike::getUserId, user.getId())
				.eq(UserLike::getPostId, id)
				.eq(UserLike::getType, 1);
		UserLike existLike = userLikeMapper.selectOne(wrapper);

		if (existLike != null) {
			// 取消点赞
			userLikeMapper.deleteById(existLike.getId());
			post.setLikeCount(post.getLikeCount() - 1);
		} else {
			// 点赞
			UserLike userLike = new UserLike();
			userLike.setUserId(user.getId());
			userLike.setPostId(id);
			userLike.setType(1);
			userLike.setCreateTime(LocalDateTime.now());
			userLikeMapper.insert(userLike);
			post.setLikeCount(post.getLikeCount() + 1);
		}
		consultMapper.updateById(post);
		return Result.ok();
	}

	@Override
	@Transactional
	public Result favoriteArticle(Long id) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		Post post = consultMapper.selectById(id);
		if (post == null) {
			return Result.fail("文章不存在");
		}

		// 检查是否已经收藏
		LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(UserFavorite::getUserId, user.getId())
				.eq(UserFavorite::getPostId, id);
		UserFavorite existFavorite = userFavoriteMapper.selectOne(wrapper);

		if (existFavorite != null) {
			// 取消收藏
			userFavoriteMapper.deleteById(existFavorite.getId());
			post.setFavoriteCount(post.getFavoriteCount() - 1);
		} else {
			// 收藏
			UserFavorite userFavorite = new UserFavorite();
			userFavorite.setUserId(user.getId());
			userFavorite.setPostId(id);
			userFavorite.setCreateTime(LocalDateTime.now());
			userFavoriteMapper.insert(userFavorite);
			post.setFavoriteCount(post.getFavoriteCount() + 1);
		}
		consultMapper.updateById(post);
		return Result.ok();
	}

	@Override
	public Result getComments(Long postId, Long page, Long pageSize) {
		Page<Comment> pageInfo = new Page<>(page, pageSize);
		LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Comment::getPostId, postId)
				.eq(Comment::getStatus, 1)
				.isNull(Comment::getParentId) // 只查询一级评论
				.orderByDesc(Comment::getCreateTime);
		IPage<Comment> result = commentMapper.selectPage(pageInfo, wrapper);

		// 查询每个一级评论的子评论
		List<Map<String, Object>> commentList = result.getRecords().stream().map(comment -> {
			Map<String, Object> map = new HashMap<>();
			map.put("comment", comment);

			// 查询子评论
			LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
			childWrapper.eq(Comment::getParentId, comment.getId())
					.eq(Comment::getStatus, 1)
					.orderByAsc(Comment::getCreateTime);
			List<Comment> childComments = commentMapper.selectList(childWrapper);
			map.put("replies", childComments);

			return map;
		}).collect(Collectors.toList());

		return Result.ok(commentList, result.getTotal());
	}

	@Override
	@Transactional
	public Result addComment(Long postId, String content, Long parentId) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		Post post = consultMapper.selectById(postId);
		if (post == null) {
			return Result.fail("文章不存在");
		}

		Comment comment = new Comment();
		comment.setPostId(postId);
		comment.setUserId(user.getId());
		comment.setContent(content);
		comment.setParentId(parentId);
		comment.setLikeCount(0);
		comment.setStatus(1);
		comment.setCreateTime(LocalDateTime.now());
		comment.setUpdateTime(LocalDateTime.now());
		commentMapper.insert(comment);

		// 更新文章评论数
		post.setCommentCount(post.getCommentCount() + 1);
		consultMapper.updateById(post);

		return Result.ok();
	}

	@Override
	@Transactional
	public Result deleteComment(Long commentId) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		Comment comment = commentMapper.selectById(commentId);
		if (comment == null) {
			return Result.fail("评论不存在");
		}

		// 软删除
		comment.setStatus(0);
		comment.setUpdateTime(LocalDateTime.now());
		commentMapper.updateById(comment);

		// 更新文章评论数
		Post post = consultMapper.selectById(comment.getPostId());
		if (post != null) {
			post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
			consultMapper.updateById(post);
		}

		return Result.ok();
	}

	@Override
	public Result getUserFavorites(Long userId, Long page, Long pageSize) {
		Page<UserFavorite> pageInfo = new Page<>(page, pageSize);
		LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(UserFavorite::getUserId, userId)
				.orderByDesc(UserFavorite::getCreateTime);
		IPage<UserFavorite> result = userFavoriteMapper.selectPage(pageInfo, wrapper);

		// 获取文章详情
		List<Long> postIds = result.getRecords().stream()
				.map(UserFavorite::getPostId)
				.collect(Collectors.toList());

		List<Post> posts = postIds.isEmpty() ? List.of() : consultMapper.selectBatchIds(postIds);
		return Result.ok(posts, result.getTotal());
	}

	@Override
	public Result getUserComments(Long userId, Long page, Long pageSize) {
		Page<Comment> pageInfo = new Page<>(page, pageSize);
		LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Comment::getUserId, userId)
				.eq(Comment::getStatus, 1)
				.orderByDesc(Comment::getCreateTime);
		IPage<Comment> result = commentMapper.selectPage(pageInfo, wrapper);
		return Result.ok(result.getRecords(), result.getTotal());
	}
}
