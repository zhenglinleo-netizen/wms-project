package com.example.wms.mapper;

import com.example.wms.entity.Requirement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RequirementMapper {

    /**
     * 插入需求
     */
    int insert(Requirement requirement);

    /**
     * 更新需求
     */
    int updateById(Requirement requirement);

    /**
     * 根据ID查询需求
     */
    Requirement selectById(@Param("id") Long id);

    /**
     * 删除需求
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询需求列表
     */
    List<Requirement> selectRequirementList(@Param("keyword") String keyword,
                                           @Param("status") String status,
                                           @Param("priority") String priority,
                                           @Param("creatorId") Long creatorId,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);

    /**
     * 查询需求数量
     */
    int selectRequirementCount(@Param("keyword") String keyword,
                              @Param("status") String status,
                              @Param("priority") String priority,
                              @Param("creatorId") Long creatorId);
    
    /**
     * 检查同一个项目内是否存在同名的采购需求
     */
    int checkRequirementNameExists(@Param("projectId") Long projectId,
                                   @Param("purpose") String purpose,
                                   @Param("excludeId") Long excludeId);
}