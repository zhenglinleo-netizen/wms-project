package com.example.wms.mapper;

import com.example.wms.entity.PurchaseUrge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PurchaseUrgeMapper {
    
    PurchaseUrge selectById(Long id);
    
    List<PurchaseUrge> selectByOrderId(Long orderId);
    
    List<PurchaseUrge> selectPendingByOrderId(Long orderId);
    
    List<PurchaseUrge> selectList(@Param("orderId") Long orderId, 
                                   @Param("userId") Long userId,
                                   @Param("status") String status);
    
    int insert(PurchaseUrge urge);
    
    int updateById(PurchaseUrge urge);
    
    int updateResponse(@Param("id") Long id, 
                       @Param("responseContent") String responseContent,
                       @Param("status") String status);
    
    int deleteById(Long id);
}
