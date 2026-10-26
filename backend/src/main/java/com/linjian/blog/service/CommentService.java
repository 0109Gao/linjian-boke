package com.linjian.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linjian.blog.common.BusinessException;
import com.linjian.blog.dto.CommentRequest;
import com.linjian.blog.dto.CommentVO;
import com.linjian.blog.entity.Comment;
import com.linjian.blog.entity.SiteConfig;
import com.linjian.blog.entity.User;
import com.linjian.blog.mapper.CommentMapper;
import com.linjian.blog.mapper.SiteConfigMapper;
import com.linjian.blog.mapper.UserMapper;
import com.linjian.blog.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务（支持盖楼）
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final SiteConfigMapper siteConfigMapper;

    /**
     * 获取某文章的评论树。公开用户只看 APPROVED，作者/站长看全部。
     */
    public List<CommentVO> listByPost(Long postId) {
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .orderByAsc(Comment::getCreatedAt));

        // 组装树
        Map<Long, List<Comment>> childrenMap = comments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<CommentVO> roots = new ArrayList<>();
        for (Comment comment : comments) {
            if (comment.getParentId() == null) {
                CommentVO vo = toVO(comment);
                vo.setChildren(buildChildren(comment.getId(), childrenMap, comments));
                roots.add(vo);
            }
        }
        return roots;
    }

    private List<CommentVO> buildChildren(Long parentId, Map<Long, List<Comment>> childrenMap,
                                          List<Comment> all) {
        List<Comment> children = childrenMap.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }
        List<CommentVO> result = new ArrayList<>();
        for (Comment child : children) {
            CommentVO vo = toVO(child);
            vo.setChildren(buildChildren(child.getId(), childrenMap, all));
            result.add(vo);
        }
        return result;
    }

    /**
     * 发表评论。根据站点 comment_moderation 开关决定状态。
     */
    public CommentVO create(Long postId, CommentRequest request) {
        User current = UserContext.get();
        if (current == null) {
            throw new BusinessException(401, "请先登录");
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(current.getId());
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());

        // 评论审核开关：false=即时可见（APPROVED），true=待审核（PENDING）
        boolean moderation = isCommentModeration();
        comment.setStatus(moderation && !"ADMIN".equals(current.getRole()) ? "PENDING" : "APPROVED");

        commentMapper.insert(comment);

        CommentVO vo = toVO(comment);
        vo.setChildren(new ArrayList<>());
        return vo;
    }

    /**
     * 站长审核评论。
     */
    public void audit(Long id, String status) {
        if (!"ADMIN".equals(UserContext.get().getRole())) {
            throw new BusinessException(403, "无权限操作");
        }
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        comment.setStatus(status);
        commentMapper.updateById(comment);
    }

    /**
     * 站长获取待审核评论。
     */
    public List<Comment> listPending() {
        if (!"ADMIN".equals(UserContext.get().getRole())) {
            throw new BusinessException(403, "无权限操作");
        }
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getStatus, "PENDING")
                .orderByDesc(Comment::getCreatedAt));
    }

    private boolean isCommentModeration() {
        SiteConfig config = siteConfigMapper.selectOne(
                new LambdaQueryWrapper<SiteConfig>().eq(SiteConfig::getConfigKey, "comment_moderation"));
        return config != null && "true".equals(config.getConfigValue());
    }

    private CommentVO toVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setStatus(comment.getStatus());
        vo.setCreatedAt(comment.getCreatedAt());
        User author = userMapper.selectById(comment.getUserId());
        if (author != null) {
            vo.setAuthorName(author.getNickname() != null ? author.getNickname() : author.getUsername());
            vo.setAuthorAvatar(author.getAvatar());
        }
        return vo;
    }
}
