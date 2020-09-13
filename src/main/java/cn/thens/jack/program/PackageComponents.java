package cn.thens.jack.program;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageComponents {
    private static final String ACTION_MAIN = "android.intent.action.MAIN";
    private static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";

    private final ApplicationInfo application;
    private final Map<String, Entry<ActivityInfo>> activities = new HashMap<>();
    private final Map<String, Entry<ServiceInfo>> services = new HashMap<>();
    private final Map<String, Entry<ActivityInfo>> receivers = new HashMap<>();
    private final Map<String, Entry<ProviderInfo>> providers = new HashMap<>();

    PackageComponents(ApplicationInfo application) {
        this.application = application;
    }

    public ApplicationInfo getApplication() {
        return application;
    }

    public Map<String, Entry<ActivityInfo>> getActivities() {
        return activities;
    }

    public Map<String, Entry<ServiceInfo>> getServices() {
        return services;
    }

    public Map<String, Entry<ActivityInfo>> getReceivers() {
        return receivers;
    }

    public Map<String, Entry<ProviderInfo>> getProviders() {
        return providers;
    }

    public List<Entry<ActivityInfo>> getLauncherActivities() {
        List<Entry<ActivityInfo>> result = new ArrayList<>();
        Collection<Entry<ActivityInfo>> items = activities.values();
        for (Entry<ActivityInfo> item : items) {
            for (IntentFilter filter : item.intentFilters) {
                if (filter.hasAction(ACTION_MAIN) && filter.hasCategory(CATEGORY_LAUNCHER)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public static class Entry<T extends ComponentInfo> {
        private final T component;
        private final List<IntentFilter> intentFilters;

        public Entry(T component, List<IntentFilter> intentFilters) {
            this.component = component;
            this.intentFilters = intentFilters;
        }

        public T getComponent() {
            return component;
        }

        public List<IntentFilter> getIntentFilters() {
            return intentFilters;
        }
    }
}
