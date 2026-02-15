package com.example.wms.utils;

/**
 * 缓存键生成工具类
 * 用于统一管理Redis缓存键的生成规则
 */
public class CacheKeyUtil {
    
    // 缓存键前缀
    private static final String PRODUCT_PREFIX = "product:";
    private static final String MATERIAL_PREFIX = "material:";
    private static final String WAREHOUSE_PREFIX = "warehouse:";
    private static final String INVENTORY_PREFIX = "inventory:";
    private static final String SUPPLIER_PREFIX = "supplier:";
    private static final String CATEGORY_PREFIX = "category:";
    
    // 列表缓存后缀
    private static final String LIST_SUFFIX = ":list";
    private static final String ALL_SUFFIX = ":all";
    
    // 过期时间（分钟）
    public static final int DEFAULT_EXPIRE_TIME = 30;
    public static final int SHORT_EXPIRE_TIME = 5;
    public static final int LONG_EXPIRE_TIME = 60;
    
    /**
     * 生成商品缓存键
     */
    public static String getProductKey(Long productId) {
        return PRODUCT_PREFIX + productId;
    }
    
    /**
     * 生成商品列表缓存键
     */
    public static String getProductListKey() {
        return PRODUCT_PREFIX + ALL_SUFFIX + LIST_SUFFIX;
    }
    
    /**
     * 生成商品按条件查询缓存键
     */
    public static String getProductByConditionKey(String condition) {
        return PRODUCT_PREFIX + "condition:" + condition;
    }
    
    /**
     * 生成商品文件哈希缓存键
     */
    public static String getProductByFileHashKey(String fileHash) {
        return PRODUCT_PREFIX + "filehash:" + fileHash;
    }
    
    /**
     * 生成仓库缓存键
     */
    public static String getWarehouseKey(Long warehouseId) {
        return WAREHOUSE_PREFIX + warehouseId;
    }
    
    /**
     * 生成仓库列表缓存键
     */
    public static String getWarehouseListKey() {
        return WAREHOUSE_PREFIX + ALL_SUFFIX + LIST_SUFFIX;
    }
    
    /**
     * 生成缓存键的通用方法
     */
    public static String getKey(String prefix, String suffix) {
        return prefix + suffix;
    }
    
    /**
     * 清除缓存时使用的模式
     */
    public static String getPattern(String prefix) {
        return prefix + "*";
    }
}
