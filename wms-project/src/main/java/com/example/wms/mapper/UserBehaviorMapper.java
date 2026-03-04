package com.example.wms.mapper;

import com.example.wms.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserBehaviorMapper {
    int insert(UserBehavior userBehavior);

    List<UserBehavior> selectByUserId(@Param("userId") Long userId);

    List<UserBehavior> selectByMaterialId(@Param("materialId") Long materialId);

    List<UserBehavior> selectByUserIdAndType(@Param("userId") Long userId, @Param("behaviorType") String behaviorType);

    int countByUserIdAndMaterialId(@Param("userId") Long userId, @Param("materialId") Long materialId, @Param("behaviorType") String behaviorType);

    List<Long> getUsersWithSimilarBehavior(@Param("userId") Long userId, @Param("materialIds") List<Long> materialIds);

    List<Long> getMaterialsByUserIds(@Param("userIds") List<Long> userIds, @Param("excludeUserId") Long excludeUserId);
}
