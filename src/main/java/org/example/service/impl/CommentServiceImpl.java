package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.CommentMapper;
import org.example.pojo.entity.Comment;
import org.example.service.CommentService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
