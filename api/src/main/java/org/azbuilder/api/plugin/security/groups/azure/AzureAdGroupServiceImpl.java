package org.azbuilder.api.plugin.security.groups.azure;

import com.microsoft.graph.models.DirectoryObjectCheckMemberGroupsParameterSet;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.*;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "org.azbuilder.api.groups", name = "type", havingValue = "AZURE")
public class AzureAdGroupServiceImpl implements GroupService {

    @Autowired
    private GraphServiceClient graphServiceClient;

    @Autowired
    CacheManager cacheManager;

    @Override
    @Cacheable(cacheNames = "members")
    public boolean isMember(String userName, String groupName) {
        return isUserMemberGroup(getUserId(userName), getGroupId(groupName));
    }

    private String getGroupId(String groupName) {
        log.info("Search Group Id {}", groupName);
        List<Option> requestOptions = new ArrayList<>();
        requestOptions.add(new QueryOption("$filter", "displayName eq '" + groupName + "'"));

        GroupCollectionPage groupCollectionPage = graphServiceClient.groups().buildRequest(requestOptions).get();
        if (groupCollectionPage.getCurrentPage().size() == 1) {
            return groupCollectionPage.getCurrentPage().get(0).id;
        } else {
            return null;
        }
    }

    private String getUserId(String userName) {
        log.info("Search User Id {}", userName);
        List<Option> requestOptions = new ArrayList<>();
        requestOptions.add(new QueryOption("$filter", "mail eq '" + userName + "'"));

        UserCollectionPage userCollectionPage = graphServiceClient.users().buildRequest(requestOptions).get();
        if (userCollectionPage.getCurrentPage().size() == 1) {
            return userCollectionPage.getCurrentPage().get(0).id;
        } else {
            return null;
        }
    }

    private boolean isUserMemberGroup(String userId, String groupId) {
        LinkedList<String> groupIdsList = new LinkedList<>();
        groupIdsList.add(groupId);

        DirectoryObjectCheckMemberGroupsCollectionPage directoryObjectCheckMemberGroupsCollectionPage = graphServiceClient.users(userId)
                .checkMemberGroups(DirectoryObjectCheckMemberGroupsParameterSet
                        .newBuilder()
                        .withGroupIds(groupIdsList)
                        .build())
                .buildRequest()
                .post();

        return directoryObjectCheckMemberGroupsCollectionPage.getCurrentPage().size() == 1;

    }
}
