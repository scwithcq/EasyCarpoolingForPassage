package com.chat.mapper;

import com.chat.entity.ForumCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ForumCategoryMapper {

    //查询所有启用的帖子分类，按排序值升序
    @Select("select * from chat_app.forum_category where status = 1 order by sort asc")
    List<ForumCategory> getCategoryList();

    //管理端：查询全部分类（含已停用）
    @Select("select * from chat_app.forum_category order by sort asc")
    List<ForumCategory> getAllList();

    //根据id查询分类
    @Select("select * from chat_app.forum_category where id = #{id}")
    ForumCategory getById(Long id);

    //新增分类
    @Insert("insert into chat_app.forum_category (name, sort, status) values (#{name}, #{sort}, #{status})")
    void insert(ForumCategory forumCategory);

    //修改分类
    void update(ForumCategory forumCategory);

    //删除分类
    @Delete("delete from chat_app.forum_category where id = #{id}")
    void deleteById(Long id);
}