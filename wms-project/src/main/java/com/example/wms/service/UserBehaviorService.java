package com.example.wms.service;

import com.example.wms.entity.UserBehavior;
import java.util.List;

public interface UserBehaviorService {
    void recordBehavior(Long userId, Long materialId, String behaviorType);

    List<UserBehavior> getUserBehaviors(Long userId);

    List<Long> getSimilarUsers(Long userId);

    List<Long> getRecommendedMaterials(Long userId);
}
