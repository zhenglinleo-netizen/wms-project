package com.example.wms.mapper;

import com.example.wms.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PurchaseOrderMapper {
    
    PurchaseOrder selectById(Long id);
    
    List<PurchaseOrder> selectList(@Param("keyword") String keyword, 
                                   @Param("status") String status, 
                                   @Param("creatorId") Long creatorId,
                                   @Param("offset") Integer offset, 
                                   @Param("pageSize") Integer pageSize);
    
    int selectCount(@Param("keyword") String keyword, 
                    @Param("status") String status, 
                    @Param("creatorId") Long creatorId);
    
    PurchaseOrder selectByRequirementId(Long requirementId);
    
    int insert(PurchaseOrder purchaseOrder);
    
    int updateById(PurchaseOrder purchaseOrder);
    
    int deleteById(Long id);
    
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
