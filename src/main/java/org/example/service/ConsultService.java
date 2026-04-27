package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.result.Result;
import org.example.pojo.entity.Post;

public interface ConsultService extends IService<Post> {
	Result addArticle(Post post);

	Result updateArticle(Post post);

	Result deleteArticle(Long id);

	Result listArticles(Long page, Long pageSize, String keyword);

	Result getArticleDetail(Long id);

	Result likeArticle(Long id);

	Result favoriteArticle(Long id);

	Result getComments(Long postId, Long page, Long pageSize);

	Result addComment(Long postId, String content, Long parentId);

	Result deleteComment(Long commentId);

	Result getUserFavorites(Long userId, Long page, Long pageSize);

	Result getUserComments(Long userId, Long page, Long pageSize);
}
