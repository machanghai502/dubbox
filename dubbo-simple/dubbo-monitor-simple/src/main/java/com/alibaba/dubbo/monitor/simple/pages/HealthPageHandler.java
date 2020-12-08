package com.alibaba.dubbo.monitor.simple.pages;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.container.page.Menu;
import com.alibaba.dubbo.container.page.Page;
import com.alibaba.dubbo.container.page.PageHandler;
import com.alibaba.dubbo.monitor.simple.RegistryContainer;
import com.alibaba.fastjson.JSON;

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

    //private static Logger logger = LoggerFactory.getLogger(HealthPageHandler.class);

    //private static Properties p = Properties.

    @Override
    public Page handle(URL url) {
        List<String> sss = new ArrayList<String>();
        Properties properties = new Properties();
        try {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/applications.properties");
            properties.load(resourceAsStream);
            for (Object o : properties.keySet()) {
                sss.add(o.toString());
            }
            for (Object value : properties.values()) {
                sss.add(value.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Set<String> applications = RegistryContainer.getInstance().getApplications();
        if (applications != null && applications.size() > 0) {
            for (String application : applications) {
                List<String> row = new ArrayList<String>();
                row.add(application);

                List<URL> providers = RegistryContainer.getInstance().getProvidersByApplication(application);
                List<URL> consumers = RegistryContainer.getInstance().getConsumersByApplication(application);

                if (providers != null && providers.size() > 0
                        || consumers != null && consumers.size() > 0) {
                    URL provider = (providers != null && providers.size() > 0 ? providers.iterator().next() : consumers.iterator().next());
                    row.add(provider.getParameter("owner", "") + (provider.hasParameter("organization") ? " (" + provider.getParameter("organization") + ")" : ""));
                } else {
                    row.add("");
                }

                /*int providersSize = providers == null ? 0 : providers.size();
                providersCount += providersSize;
                row.add(providersSize == 0 ? "<font color=\"blue\">No provider</font>" : "<a href=\"providers.html?application=" + application + "\">Providers(" + providersSize + ")</a>");

                int consumersSize = consumers == null ? 0 : consumers.size();
                consumersCount += consumersSize;
                row.add(consumersSize == 0 ? "<font color=\"blue\">No consumer</font>" : "<a href=\"consumers.html?application=" + application + "\">Consumers(" + consumersSize + ")</a>");

                Set<String> efferents = RegistryContainer.getInstance().getDependencies(application, false);
                int efferentSize = efferents == null ? 0 : efferents.size();
                efferentCount += efferentSize;
                row.add(efferentSize == 0 ? "<font color=\"blue\">No dependency</font>" : "<a href=\"dependencies.html?application=" + application + "\">Depends On(" + efferentSize + ")</a>");

                Set<String> afferents = RegistryContainer.getInstance().getDependencies(application, true);
                int afferentSize = afferents == null ? 0 : afferents.size();
                afferentCount += afferentSize;
                row.add(afferentSize == 0 ? "<font color=\"blue\">No used</font>" : "<a href=\"dependencies.html?application=" + application + "&reverse=true\">Used By(" + afferentSize + ")</a>");
                rows.add(row);*/
            }
        }
        /*return new Page("Applications", "Applications (" + rows.size() + ")",
                new String[]{"Application Name:", "Owner", "Providers(" + providersCount + ")", "Consumers(" + consumersCount + ")", "Depends On(" + efferentCount + ")", "Used By(" + afferentCount + ")"}, rows);

*/


        System.out.println(JSON.toJSONString(sss));

        Page resultPage = new Page("","","", JSON.toJSONString(sss));
        return resultPage;
    }
}
