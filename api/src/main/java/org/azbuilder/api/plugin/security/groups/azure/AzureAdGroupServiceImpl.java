package org.azbuilder.api.plugin.security.groups.azure;

import com.microsoft.graph.models.DirectoryObjectCheckMemberGroupsParameterSet;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.*;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class AzureAdGroupServiceImpl implements GroupService {

    @Autowired
    private GraphServiceClient graphServiceClient;

    @Override
    public boolean isMember(String userName, String groupName) {
        return isUserMemberGroup(getUserId(userName), getGroupId(groupName));
    }

    public String getGroupId(String groupName) {
        List<Option> requestOptions = new ArrayList<Option>();
        requestOptions.add(new QueryOption("$filter", "displayName eq '" + groupName + "'"));

        GroupCollectionPage groupCollectionPage = graphServiceClient.groups().buildRequest(requestOptions).get();
        if (groupCollectionPage.getCurrentPage().size() == 1) {
            return groupCollectionPage.getCurrentPage().get(0).id;
        } else {
            return null;
        }
    }

    public String getUserId(String userName) {
        List<Option> requestOptions = new ArrayList<Option>();
        requestOptions.add(new QueryOption("$filter", "userPrincipalName eq '" + userName + "'"));

        UserCollectionPage userCollectionPage = graphServiceClient.users().buildRequest(requestOptions).get();
        if (userCollectionPage.getCurrentPage().size() == 1) {
            return userCollectionPage.getCurrentPage().get(0).id;
        } else {
            return null;
        }
    }

    public boolean isUserMemberGroup(String userId, String groupId) {

        List<Option> requestOptions = new ArrayList<Option>();

        LinkedList<String> groupIdsList = new LinkedList<String>();
        groupIdsList.add(groupId);

        DirectoryObjectCheckMemberGroupsCollectionPage directoryObjectCheckMemberGroupsCollectionPage = graphServiceClient.users(userId)
                .checkMemberGroups(DirectoryObjectCheckMemberGroupsParameterSet
                        .newBuilder()
                        .withGroupIds(groupIdsList)
                        .build())
                .buildRequest()
                .post();

        return directoryObjectCheckMemberGroupsCollectionPage.getCurrentPage().size() == 1 ? true : false;

    }
}
