package com.mygame.xinyue.common;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DataConfigService {
    private Map<String, Map<String,Object>> dataConfigMap = new HashMap<>();
    /**
     * 初始化配置表，根据自己的业务需求自己实现即可
     * <p>Description: </p>
     * @author wgs 
     * @date  2019年7月7日 下午9:43:42
     *
     */
    public void init() {
        
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getDataConfig(String id,Class<T> clazz) {
        String key = clazz.getName();
        Map<String, Object> valueMap = this.dataConfigMap.get(key);
        if(valueMap == null) {
            return null;
        }
        Object value = valueMap.get(id);
        if(value == null) {
            return null;
        }
        return (T)value;
    }
}
