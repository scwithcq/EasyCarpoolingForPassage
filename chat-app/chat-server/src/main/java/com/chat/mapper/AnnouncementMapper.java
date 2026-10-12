package com.chat.mapper;

import com.chat.dto.AnnouncementPageQueryDTO;
import com.chat.entity.Announcement;
import com.chat.vo.AnnouncementVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AnnouncementMapper {

    //用户端：分页查询已发布公告（置顶优先）
    Page<AnnouncementVO> getPublishedList();

    //用户端：查询已发布公告详情
    AnnouncementVO getPublishedById(@Param("id") Long id);

    //管理端：分页查询公告
    Page<Announcement> pageQuery(AnnouncementPageQueryDTO announcementPageQueryDTO);

    //根据id查询公告
    @Select("select * from chat_app.announcement where id = #{id}")
    Announcement getById(Long id);

    //新增公告
    void insert(Announcement announcement);

    //修改公告
    void update(Announcement announcement);

    //修改公告状态（1已发布 0已下线）
    @Update("update chat_app.announcement set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    //删除公告
    @Delete("delete from chat_app.announcement where id = #{id}")
    void deleteById(Long id);
}