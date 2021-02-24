package com.exploration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ConstantConfig
 * @Author: leisure
 * @CreateDate: 2021/2/23 14:49
 * @Description: 常量配置类
 */
@Component(value = "constantConfig")
@ConfigurationProperties(prefix = "constantconfig")
@PropertySource(value = "classpath:config/constantconfig.properties")
public class ConstantConfig {
    /**
     * 统一时间格式yyyy-MM-dd
     */
    private String dateFormat;
    /**
     * 对date_format的简单正则
     */
    private String dateRegex;
    /**
     * 是否使用代理
     */
    private Boolean jvmProxy;

    /*********************************************************************************/
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateRegex() {
        return dateRegex;
    }

    public void setDateRegex(String dateRegex) {
        this.dateRegex = dateRegex;
    }

    public Boolean getJvmProxy() {
        return jvmProxy;
    }

    public void setJvmProxy(Boolean jvmProxy) {
        this.jvmProxy = jvmProxy;
    }

    @Override
    public String toString() {
        return "ConstantConfig{" +
                "dateFormat='" + dateFormat + '\'' +
                ", dateRegex='" + dateRegex + '\'' +
                ", jvmProxy=" + jvmProxy +
                '}';
    }
}
