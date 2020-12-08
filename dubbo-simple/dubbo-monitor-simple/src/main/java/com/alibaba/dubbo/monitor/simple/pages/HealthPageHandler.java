package com.alibaba.dubbo.monitor.simple.pages;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.container.page.Menu;
import com.alibaba.dubbo.container.page.Page;
import com.alibaba.dubbo.container.page.PageHandler;
import com.alibaba.dubbo.monitor.simple.RegistryContainer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @Description : 健康检查
 * @Author : machanghai
 * @Date: 2020-12-07 19:22
 */
@Menu(name = "Health", desc = "Show health status.", order = 1000)
public class HealthPageHandler implements PageHandler {

    @Override
    public Page handle(URL url) {
        JSONObject resultJsonObject = new JSONObject();
        //Green=所有服务健康,Yellow=局部服务健康，Red=所有服务不健康
        resultJsonObject.put("status", "Green");

        List<String>  serviceKeyList = new ArrayList<String>();
        List<String> healthyServiceList = new ArrayList<String>();
        List<String> unHealthyServiceList = new ArrayList<String>();

        Properties properties = new Properties();
        try {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/applications.properties");
            properties.load(resourceAsStream);
            for (Object key : properties.keySet()) {
                serviceKeyList.add(key.toString());
            }
        } catch (Exception ex) {
            System.out.println("加载服务配置文件失败:" + ex.getMessage());
        }

        //从zk上获取所有的已注册的application
        Set<String> applications = RegistryContainer.getInstance().getApplications();
        if (applications == null || applications.size() <= 0) {
            resultJsonObject.put("status", "Yellow");
        } else {
            Set<Object> effectiveServices = properties.keySet();
            for (Object effectiveService : effectiveServices) {
                String effectiveServiceStr = effectiveService.toString();
                if(!applications.contains(effectiveServiceStr)) {
                    unHealthyServiceList.add(effectiveServiceStr);
                    continue;
                }

                List<URL> providers = RegistryContainer.getInstance().getProvidersByApplication(effectiveServiceStr);
                if (providers == null || providers.size() <= 0) {
                    unHealthyServiceList.add(effectiveServiceStr);
                    continue;
                }

                healthyServiceList.add(effectiveServiceStr);
            }

            if (unHealthyServiceList.size() > 0) {
                resultJsonObject.put("status", "Yellow");
            }
        }

        resultJsonObject.put("healthyServiceList", healthyServiceList);
        if (unHealthyServiceList.size() > 0) {
            resultJsonObject.put("unHealthyServiceList", unHealthyServiceList);
        }

        return new Page("","","", JSON.toJSONString(resultJsonObject));
    }
}
