package org.terrakube.api.plugin.security.state;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.terrakube.api.repository.TeamRepository;
import org.terrakube.api.rs.team.Team;

@Service
public class StateService {
   @Autowired
   private TeamRepository teamRepository;

   public boolean hasManageStatePermission(Authentication authentication, String orgnizationId) {
      Object groupNames = ((JwtAuthenticationToken) authentication).getTokenAttributes().get("groups");
      if (groupNames == null) {
         return false;
      }
      @SuppressWarnings("unchecked")
      List<Team> teams = teamRepository.findAllByOrganizationIdAndNameIn(UUID.fromString(orgnizationId), (List<String>) groupNames);
      for (Team team : teams) {
         if (team.isManageState()) {
            return true;
         }
      }
      
      return false;
   } 
}
