package com.fzb.blog.model;

import com.fzb.blog.util.ParseUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对应数据里面的comment表，用于存放文章对应的评论信息。
 */
public class Comment extends Model<Comment> {
    public static final Comment dao = new Comment();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Map<String, Object> getCommentsByPage(int page, int pageSize) {
        Map<String, Object> data = new HashMap<String, Object>();
        String sql = "select commentId as id,userComment,header,commTime,userMail,userHome,userIp,userName,hide,logId from comment order by commTime desc limit ?,?";
        data.put(
                "rows",
                find(sql,
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(page, pageSize, "from comment", data, new Object[0]);
        return data;
    }

    public Long getCommentCount() {
        String sql = "select count(1) from comment";
        return Db.findFirst(sql).get("count(1)");
    }

    public Long getToDayCommentCount() {
        String sql = "select count(1) from comment where DATE_FORMAT(commTime,'%Y_%m_%d')=?";
        return Db.findFirst(sql, new SimpleDateFormat("yyyy_MM_dd").format(new Date())).get("count(1)");
    }

    public Map<String, Object> noRead(int page, int pageSize) {
        Map<String, Object> data = new HashMap<String, Object>();
        String sql = "select commentId as id,userComment,header,userMail,userHome,userIp,userName,hide,logId from comment order by commTime desc limit ?,?";
        data.put(
                "rows",
                find(sql,
                        ParseUtil.getFirstRecord(page,
                                pageSize), pageSize));
        fillData(page, pageSize, "from comment", data, new Object[0]);
        return data;
    }

    private void fillData(int page, int pageSize, String where,
                          Map<String, Object> data, Object[] obj) {
        if (((List) data.get("rows")).size() > 0) {
            data.put("page", page);
            long count = findFirst("select count(commentId) cnt "
                    + where, obj).getLong("cnt");
            data.put("total",
                    ParseUtil.getTotalPate(count, pageSize));
            data.put("records", count);
        } else {
            data.clear();
        }
    }

    public List<Comment> getCommentsByLogId(int logId) {
        return find("select * from comment where logId=?", logId);
    }
}
