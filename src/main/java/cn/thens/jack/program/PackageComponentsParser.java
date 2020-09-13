package cn.thens.jack.program;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;

import java.util.List;
import java.util.Map;

class PackageComponentsParser {
    public static PackageComponents parse(PackageInfo packageInfo, AssetManager assetManager) throws Exception {
        PackageComponents result = new PackageComponents(packageInfo.applicationInfo);
        Map<String, List<IntentFilter>> filterMap = ManifestParser.parse(assetManager);
        for (ActivityInfo item : packageInfo.activities) {
            List<IntentFilter> filters = filterMap.get(item.name);
            result.getActivities().put(item.name, new PackageComponents.Entry<>(item, filters));
        }
        for (ServiceInfo item : packageInfo.services) {
            List<IntentFilter> filters = filterMap.get(item.name);
            result.getServices().put(item.name, new PackageComponents.Entry<>(item, filters));
        }
        for (ActivityInfo item : packageInfo.receivers) {
            List<IntentFilter> filters = filterMap.get(item.name);
            result.getReceivers().put(item.name, new PackageComponents.Entry<>(item, filters));
        }
        for (ProviderInfo item : packageInfo.providers) {
            List<IntentFilter> filters = filterMap.get(item.authority);
            result.getProviders().put(item.authority, new PackageComponents.Entry<>(item, filters));
        }
        return result;
    }
}
