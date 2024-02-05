package com.zrlog.model;

import com.hibegin.dao.DAO;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 对应数据里面的comment表，用于存放文章对应的评论信息。
 */
public class Comment extends DAO {

    public static final String TABLE_NAME = "comment";

    public Comment() {
        this.tableName = TABLE_NAME;
        this.pk = "commentId";
    }

    public PageData<Map<String, Object>> find(PageRequest page) throws SQLException {
        PageData<Map<String, Object>> data = new PageData<>();
        String sql = "select commentId as id,userComment,header,commTime,userMail,userHome,userIp,userName,hide,logId from " + tableName + " order by commTime desc limit ?,?";
        data.setRows(queryListWithParams(sql, page.getOffset(), page.getSize()));
        ModelUtil.fillPageData(this, "from " + tableName, data, new Object[0]);
        data.getRows().forEach(e -> e.put("commTime", ((LocalDateTime) e.get("commTime")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        return data;
    }

    public Long count() throws SQLException {
        String sql = "select count(1) from " + tableName;
        return (Long) queryFirstObj(sql);
    }

    public Long countToDayComment() throws SQLException {
        String sql = "select count(1) from " + tableName + " where DATE_FORMAT(commTime,'%Y_%m_%d')=?";
        return (Long) queryFirstObj(sql, new SimpleDateFormat("yyyy_MM_dd").format(new Date()));
    }

    public List<Map<String, Object>> findHaveReadIsFalse() throws SQLException {
        String sql = "select commentId as id,userComment,header,userMail,userHome,userIp,userName,hide,logId from " + tableName + " where have_read = ?  order by commTime desc ";
        return queryListWithParams(sql, false);
    }

    public List<Map<String, Object>> findAllByLogId(int logId) throws SQLException {
        return queryListWithParams("select * from " + tableName + " where logId=?", logId);
    }

    public void doRead(long id) {
        try {
            new Comment().set("have_read", true).updateById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
