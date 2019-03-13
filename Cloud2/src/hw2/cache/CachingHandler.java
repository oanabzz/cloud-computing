package hw2.cache;

import hw2.handler.util.Response;

import java.util.HashMap;
import java.util.Map;

public class CachingHandler {
    private static Map<Long, String> timeRouteMapping = new HashMap<>();
    private static Map<String, Response> pathResMapping = new HashMap<>();
    private static final Double interval = 10000000000.0;

    public static boolean check(String path) {
        System.out.println(timeRouteMapping);
        System.out.println(pathResMapping);
        // if there are less than 10 entries

        // check if you have it
        if (pathResMapping.containsKey(path)) {
            // check if it is sorta new
            Long currentTime = System.nanoTime();
            for (Long oldTime : timeRouteMapping.keySet()) {
                if (path.equals(timeRouteMapping.get(oldTime))) {
                    if (currentTime - oldTime > interval) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }

        } else {
            if (timeRouteMapping.size() < 10) {
                return false;
            }
        }
        return false;
    }

    public static Response get(String path) {
        return pathResMapping.get(path);
    }

    public static void add(String path, Response response) {
        if (pathResMapping.size() == 10) {
            // update the maps
            // check if you have an entry with the path
            if (pathResMapping.containsKey(path)) {
                for (Long oldTime : timeRouteMapping.keySet()) {
                    if (timeRouteMapping.get(oldTime).equals(path)) {
                        timeRouteMapping.remove(oldTime);
                        timeRouteMapping.put(System.nanoTime(), path);
                        pathResMapping.replace(path, response);
                    }
                }
            } else {
                // remove the oldest

                // compute min time == oldest
                Long min = Long.MAX_VALUE;
                for (Long oldTime : timeRouteMapping.keySet()) {
                    if (oldTime < min) {
                        min = oldTime;
                    }
                }
                // remove the oldest
                String route = timeRouteMapping.get(min);
                timeRouteMapping.remove(min);
                pathResMapping.remove(route);

                // add the new one yay
                pathResMapping.put(path, response);
                timeRouteMapping.put(System.nanoTime(), path);
            }

        } else {
            // there are less than 10 elements in the maps
            // add new elements
            // BUT FIRST DELETE THE OLD ENTRY IF SUCH ENTRY EXISTS OMG CUM AM PUTUT SA NU FAC ASTA
            for (Long oldTime : timeRouteMapping.keySet()) {
                if (timeRouteMapping.get(oldTime).equals(path)) {
                    timeRouteMapping.remove(oldTime);
                }
            }
            pathResMapping.put(path, response);
            timeRouteMapping.put(System.nanoTime(), path);
        }
    }
}
