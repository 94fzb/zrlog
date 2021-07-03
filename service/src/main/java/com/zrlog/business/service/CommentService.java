package com.zrlog.business.service;

import com.hibegin.common.util.StringUtils;
import com.zrlog.business.rest.request.CreateCommentRequest;
import com.zrlog.business.rest.response.CreateCommentResponse;
import com.zrlog.common.Constants;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.util.ParseUtil;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.Date;

public class CommentService {

    private static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean isAllowComment(int articleId) {
        Log log = new Log().findByIdOrAlias(articleId);
        return (log != null && log.getBoolean("canComment")) && Constants.isAllowComment();
    }

    public CreateCommentResponse save(CreateCommentRequest createCommentRequest) {
        CreateCommentResponse createCommentResponse = new CreateCommentResponse();
        if (createCommentRequest.getLogId() != null && createCommentRequest.getComment() != null) {
            if (isAllowComment(Integer.parseInt(createCommentRequest.getLogId()))) {
                String comment = Jsoup.clean(createCommentRequest.getComment(), Whitelist.basic());
                String email = createCommentRequest.getMail();
                if (StringUtils.isNotEmpty(email) && !isValidEmailAddress(email)) {
                    throw new IllegalArgumentException(email + "not email address");
                }
                String nickname = createCommentRequest.getUserName();
                if (StringUtils.isEmpty(nickname)) {
                    throw new IllegalArgumentException("nickname not block");
                }
                nickname = Jsoup.clean(createCommentRequest.getUserName(), Whitelist.basic());
                String userHome = createCommentRequest.getUserHome();
                if (StringUtils.isNotEmpty(userHome)) {
                    userHome = Jsoup.clean(createCommentRequest.getUserHome(), Whitelist.basic());
                }
                if (comment.length() > 0 && !ParseUtil.isGarbageComment(comment)) {
                    new Comment().set("userHome", userHome)
                            .set("userMail", email)
                            .set("userIp", createCommentRequest.getIp())
                            .set("userName", nickname)
                            .set("logId", createCommentRequest.getLogId())
                            .set("userComment", comment)
                            .set("user_agent", createCommentRequest.getUserAgent())
                            .set("reply_id", createCommentRequest.getReplyId())
                            .set("commTime", new Date()).set("hide", 1).save();
                }
            }
        }
        Log log = new Log().findByIdOrAlias(createCommentRequest.getLogId());
        if (log != null) {
            createCommentResponse.setAlias(log.getStr("alias"));
        }
        return createCommentResponse;
    }
}
