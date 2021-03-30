package org.acm.auth.utils;

import java.util.HashMap;

public class ReactionRoles {
    private static HashMap<String, HashMap<String, String>> roles = new HashMap<>();

    public static AddRRstatus addReactionRole(String msg, String emoji, String  role) {
        HashMap<String, String> map;
        if (!roles.containsKey(msg)) {
            map = new HashMap<>();
        } else {
            map = roles.get(msg);
        }

        //prevents from assigning 2nd emoji to a role on the same message
        if (roles.containsKey(msg) && roles.get(msg).containsValue(role)) {
            return AddRRstatus.ROLE_EXISTS;
        } else if (roles.containsKey(msg) && roles.get(msg).containsKey(emoji)) {
            return AddRRstatus.REACTION_EXISTS;
        }

        map.put(emoji, role);
        roles.put(msg, map);

        return AddRRstatus.OK;
    }

    public static String getReactionRole(String msg, String emoji) {
        if (!roles.containsKey(msg)) {
            return null;
        }

        return roles.get(msg).get(emoji);
    }

    public static String getEmojiUnicode(String msg, String roleId) {
        for (String emoji : roles.get(msg).keySet()) {
            if (roleId.equals(roles.get(msg).get(emoji)))
                return emoji;
        }

        return null;
    }

    public static boolean msgExists(String msg) {
        return roles.containsKey(msg);
    }

    public enum AddRRstatus {
        OK,
        ROLE_EXISTS,
        REACTION_EXISTS,
        ERROR
    }

//    private static
}
