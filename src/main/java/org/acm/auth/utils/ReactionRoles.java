package org.acm.auth.utils;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class ReactionRoles {
    private static HashMap<String, HashMap<String, Role>> roles = new HashMap<>();

    public static boolean addReactionRole(String msg, String emoji, Role  role) {
        HashMap<String, Role> map;
        if (!roles.containsKey(msg)) {
            map = new HashMap<>();
        } else {
            map = roles.get(msg);
        }

        //prevents from assigning 2nd emoji to a role on the same message
        if (roles.containsKey(msg) && roles.get(msg).containsValue(role)) {
            return false;
        }

        map.put(emoji, role);
        roles.put(msg, map);

        return true;
    }

    public static Role getReactionRole(String msg, String emoji) {
        return roles.get(msg).get(emoji);
    }

//    private static
}
