package org.azbuilder.api.plugin.security.groups;

public interface GroupService {

    boolean isMember(String user, String group);

    boolean isServiceMember(String application, String group);
}
