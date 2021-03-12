package org.acm.auth.utils;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;

public class ReactionRoles {
    public static HashMap<String, HashMap<String, Role>> roles = new HashMap<>();

    public ReactionRoles() {}

    public static void addReactionRole(String msg, String emoji, Role  role) {
        HashMap<String, Role> map;
        if (!roles.containsKey(msg)) {
            map = new HashMap<>();
        } else {
            map = roles.get(msg);
        }

        map.put(emoji, role);
        roles.put(msg, map);
    }

    public static Role getReactionRole(String msg, String emoji) {
        return roles.get(msg).get(emoji);
    }
}
