package com.example.wms.service.impl;

import com.example.wms.entity.UserBehavior;
import com.example.wms.mapper.UserBehaviorMapper;
import com.example.wms.service.UserBehaviorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {

    private static final Logger log = LoggerFactory.getLogger(UserBehaviorServiceImpl.class);

    private static final Map<String, BigDecimal> BEHAVIOR_WEIGHTS = new HashMap<String, BigDecimal>() {{
        put("browse", new BigDecimal("1.0"));
        put("favorite", new BigDecimal("2.0"));
        put("add_to_scheme", new BigDecimal("3.0"));
        put("purchase", new BigDecimal("5.0"));
    }};

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Override
    public void recordBehavior(Long userId, Long materialId, String behaviorType) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setMaterialId(materialId);
        behavior.setBehaviorType(behaviorType);
        behavior.setWeight(BEHAVIOR_WEIGHTS.getOrDefault(behaviorType, new BigDecimal("1.0")));
        behavior.setCreateTime(LocalDateTime.now());
        userBehaviorMapper.insert(behavior);
        log.info("Recorded user behavior: userId={}, materialId={}, type={}", userId, materialId, behaviorType);
    }

    @Override
    public List<UserBehavior> getUserBehaviors(Long userId) {
        return userBehaviorMapper.selectByUserId(userId);
    }

    @Override
    public List<Long> getSimilarUsers(Long userId) {
        List<UserBehavior> userBehaviors = userBehaviorMapper.selectByUserId(userId);
        if (userBehaviors == null || userBehaviors.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> userMaterialIds = new HashSet<>();
        for (UserBehavior behavior : userBehaviors) {
            if ("favorite".equals(behavior.getBehaviorType()) ||
                "add_to_scheme".equals(behavior.getBehaviorType()) ||
                "purchase".equals(behavior.getBehaviorType())) {
                userMaterialIds.add(behavior.getMaterialId());
            }
        }

        if (userMaterialIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userBehaviorMapper.getUsersWithSimilarBehavior(userId, new ArrayList<>(userMaterialIds));
    }

    @Override
    public List<Long> getRecommendedMaterials(Long userId) {
        List<Long> similarUsers = getSimilarUsers(userId);
        if (similarUsers == null || similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        return userBehaviorMapper.getMaterialsByUserIds(similarUsers, userId);
    }
}
