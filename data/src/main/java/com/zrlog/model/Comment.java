package com.zrlog.model;

import com.hibegin.common.dao.BasePageableDAO;
import com.hibegin.common.dao.ResultValueConvertUtils;
import com.hibegin.common.dao.dto.PageData;
import com.hibegin.common.dao.dto.PageRequest;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 对应数据里面的comment表，用于存放文章对应的评论信息。
 */
public class Comment extends BasePageableDAO {

    public static final String TABLE_NAME = "comment";

    public Comment() {
        this.tableName = TABLE_NAME;
        this.pk = "commentId";
    }

    public PageData<Map<String, Object>> find(PageRequest page) throws SQLException {
        String sql = "select commentId as id,userComment,header,commTime,userMail,userHome,userIp,userName,hide,logId from " + tableName + " order by commTime desc";
        PageData<Map<String, Object>> data = queryPageData(sql, page, new Object[0]);
        data.getRows().forEach(e -> e.put("commTime", ResultValueConvertUtils.formatDate(e.get("commTime"), "yyyy-MM-dd")));
        return data;
    }

    public Long count() throws SQLException {
        String sql = "select count(1) from " + tableName;
        return ((Number) queryFirstObj(sql)).longValue();
    }

    public Long countToDayComment() throws SQLException {
        String sql = "select count(1) from " + tableName + " where commTime>?";
        return ((Number) queryFirstObj(sql, new SimpleDateFormat("yyyy-MM-dd").format(new Date()))).longValue();
    }

    public List<Map<String, Object>> findHaveReadIsFalse() throws SQLException {
        String sql = "select commentId as id,userComment,header,userMail,userHome,userIp,userName,hide,logId from " + tableName + " where have_read = ?  order by commTime desc ";
        return queryListWithParams(sql, false);
    }

    public List<Map<String, Object>> findAllByLogId(int logId) throws SQLException {
        List<Map<String, Object>> comments = queryListWithParams("select * from " + tableName + " where logId=?", logId);
        for (Map<String, Object> comment : comments) {
            comment.put("commTime", ResultValueConvertUtils.formatDate(comment.get("commTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        return comments;
    }

    public void doRead(long id) {
        try {
            new Comment().set("have_read", true).updateById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
