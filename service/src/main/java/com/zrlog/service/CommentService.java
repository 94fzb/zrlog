package com.zrlog.service;

import com.zrlog.common.Constants;
import com.zrlog.common.request.CreateCommentRequest;
import com.zrlog.common.request.PageableRequest;
import com.zrlog.common.request.ReadCommentRequest;
import com.zrlog.common.response.CreateCommentResponse;
import com.zrlog.common.response.StandardResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.util.ParseUtil;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.Date;
import java.util.Map;

public class CommentService {

    public boolean isAllowComment() {
        return !Constants.getBooleanByFromWebSite("disable_comment_status");
    }

    private boolean isAllowComment(int articleId) {
        Log log = Log.dao.findByIdOrAlias(articleId);
        return (log != null && log.getBoolean("canComment")) && isAllowComment();
    }

    public CreateCommentResponse save(CreateCommentRequest createCommentRequest) {
        CreateCommentResponse createCommentResponse = new CreateCommentResponse();
        if (createCommentRequest.getLogId() != null && createCommentRequest.getComment() != null) {
            if (isAllowComment(Integer.valueOf(createCommentRequest.getLogId()))) {
                String comment = Jsoup.clean(createCommentRequest.getComment(), Whitelist.basic());
                if (comment.length() > 0 && !ParseUtil.isGarbageComment(comment)) {
                    new Comment().set("userHome", createCommentRequest.getUserHome())
                            .set("userMail", createCommentRequest.getComment())
                            .set("userIp", createCommentRequest.getIp())
                            .set("userName", createCommentRequest.getUserName())
                            .set("logId", createCommentRequest.getLogId())
                            .set("userComment", comment)
                            .set("user_agent", createCommentRequest.getUserAgent())
                            .set("reply_id", createCommentRequest.getReplyId())
                            .set("commTime", new Date()).set("hide", 1).save();
                } else {
                    createCommentResponse.setError(1);
                    createCommentResponse.setMessage("");
                }
            } else {
                createCommentResponse.setError(1);
                createCommentResponse.setMessage("");
            }
        } else {
            createCommentResponse.setError(1);
            createCommentResponse.setMessage("");
        }
        Log log = Log.dao.findByIdOrAlias(createCommentRequest.getLogId());
        if (log != null) {
            createCommentResponse.setAlias(log.getStr("alias"));
        }
        return createCommentResponse;
    }

    public StandardResponse delete(String[] ids) {
        for (String id : ids) {
            Comment.dao.deleteById(id);
        }
        return new StandardResponse();
    }

    public UpdateRecordResponse read(ReadCommentRequest commentRequest) {
        Comment.dao.doRead(commentRequest.getId());
        return new UpdateRecordResponse();
    }

    public Map page(PageableRequest pageable) {
        return Comment.dao.find(pageable);
    }
}
