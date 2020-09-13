package cn.thens.jack.program;

import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.os.PatternMatcher;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.thens.jack.func.Values;

class ManifestParser {
    private static final String MANIFEST_FILE = "AndroidManifest.xml";
    private static final String NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";
    private static final String MANIFEST = "manifest";
    private static final String ACTIVITY = "activity";
    private static final String SERVICE = "service";
    private static final String RECEIVER = "receiver";
    private static final String INTENT_FILTER = "intent-filter";
    private static final String ACTION = "action";
    private static final String CATEGORY = "category";
    private static final String DATA = "data";
    private static final String NAME = "name";
    private static final String PACKAGE = "package";
    private static final String SCHEME = "scheme";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String PATH = "path";
    private static final String PATH_PREFIX = "pathPrefix";
    private static final String PATH_PATTERN = "pathPattern";
    private static final String MIME_TYPE = "mimeType";

    public static Map<String, List<IntentFilter>> parse(AssetManager assetManager) throws Exception {
        XmlResourceParser parser = null;
        Map<String, List<IntentFilter>> intentFilterMap = new HashMap<>();
        try {
            String packageName = "";
            IntentFilter intentFilter = new IntentFilter();
            List<IntentFilter> intentFilters = new ArrayList<>();
            parser = assetManager.openXmlResourceParser(MANIFEST_FILE);
            int tagType = parser.next();
            while (tagType != XmlResourceParser.END_DOCUMENT) {
                if (tagType == XmlResourceParser.START_TAG) {
                    switch (parser.getName()) {
                        case MANIFEST: {
                            packageName = parser.getAttributeValue(null, PACKAGE);
                            break;
                        }
                        case ACTIVITY:
                        case SERVICE:
                        case RECEIVER: {
                            String className = getFullClassName(packageName, android(parser, NAME));
                            intentFilters = new ArrayList<>();
                            intentFilterMap.put(className, intentFilters);
                            break;
                        }
                        case INTENT_FILTER: {
                            intentFilter = new IntentFilter();
                            intentFilters.add(intentFilter);
                            break;
                        }
                        case ACTION: {
                            intentFilter.addAction(android(parser, NAME));
                            break;
                        }
                        case CATEGORY: {
                            intentFilter.addCategory(android(parser, NAME));
                            break;
                        }
                        case DATA: {
                            String scheme = android(parser, SCHEME);
                            if (!TextUtils.isEmpty(scheme)) {
                                intentFilter.addDataScheme(scheme);
                            }

                            String host = android(parser, HOST);
                            String port = android(parser, PORT);
                            if (!TextUtils.isEmpty(scheme) && !TextUtils.isEmpty(port)) {
                                intentFilter.addDataAuthority(host, port);
                            }

                            String path = android(parser, PATH);
                            if (!TextUtils.isEmpty(path)) {
                                String pathPrefix = android(parser, PATH_PREFIX);
                                String pathPattern = android(parser, PATH_PATTERN);
                                intentFilter.addDataPath(path, patternMatcherType(pathPrefix, pathPattern));
                            }

                            String mimeType = android(parser, MIME_TYPE);
                            if (!TextUtils.isEmpty(mimeType)) {
                                intentFilter.addDataType(mimeType);
                            }
                            break;
                        }
                    }
                }
                tagType = parser.next();
            }
        } finally {
            if (parser != null) parser.close();
        }
        return intentFilterMap;
    }

    private static String android(XmlResourceParser parser, String name) {
        return Values.elvis(parser.getAttributeValue(NAMESPACE_ANDROID, name), "");
    }

    private static String getFullClassName(String packageName, String className) {
        if (className.startsWith(".")) return packageName + className;
        if (className.contains(".")) return className;
        return packageName + "." + className;
    }

    private static int patternMatcherType(String pathPrefix, String pathPattern) {
        if (pathPrefix.isEmpty() && pathPattern.isEmpty()) return PatternMatcher.PATTERN_LITERAL;
        if (!pathPrefix.isEmpty()) return PatternMatcher.PATTERN_PREFIX;
        return PatternMatcher.PATTERN_SIMPLE_GLOB;
    }
}
