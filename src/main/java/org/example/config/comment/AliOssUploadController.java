package org.example.config.comment;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.common.utils.AliOssUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@Api(tags = "上传管理")
@Slf4j
public class AliOssUploadController {

	@Resource
	private AliOssUtil aliOssUtil;

	@PostMapping("/image")
	public Result upload(MultipartFile file) throws IOException {
		log.info("文件上传");
		String fileName = file.getOriginalFilename();
		String substring = fileName.substring(fileName.lastIndexOf("."));
		String objectName = UUID.randomUUID().toString() + substring;
		String url = aliOssUtil.upload(file.getBytes(),objectName);
		return Result.ok(url);
	}

}
