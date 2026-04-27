package org.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.entity.Post;
import org.example.service.ConsultService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "咨询文章管理")
@Slf4j
@RestController("admin")
@RequestMapping("/consult")
public class ConsultController {

	@Resource
	private ConsultService consultService;

	@PostMapping("/article")
	@RequirePermission("consult:article:add")
	@ApiOperation(value = "发布文章", notes = "管理员发布咨询文 章")
	public Result addArticle(@RequestBody Post post) {
		log.info("发布文章: {}", post.getTitle());
		return consultService.addArticle(post);
	}

	@PutMapping("/article/{id}")
	@RequirePermission("consult:article:update")
	@ApiOperation(value = "编辑文章", notes = "管理员编辑咨询文章")
	public Result updateArticle(@PathVariable Long id, @RequestBody Post post) {
		log.info("编辑文章, id: {}", id);
		post.setId(id);
		return consultService.updateArticle(post);
	}

	@DeleteMapping("/article/{id}")
	@RequirePermission("consult:article:delete")
	@ApiOperation(value = "删除文章", notes = "管理员删除咨询文章")
	public Result deleteArticle(@PathVariable Long id) {
		log.info("删除文章, id: {}", id);
		return consultService.deleteArticle(id);
	}
}
