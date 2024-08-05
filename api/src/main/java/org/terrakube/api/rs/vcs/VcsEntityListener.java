package org.terrakube.api.rs.vcs;

import org.springframework.beans.factory.annotation.Autowired;
import org.terrakube.api.plugin.vcs.StandAloneTokenService;

import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VcsEntityListener {
    @Autowired
    StandAloneTokenService standAloneTokenService;

    @PostPersist
    public void postPersist(Vcs vcs) {
        log.info("Post persist hook for VCS: {}", vcs.getId());
        updatePersistentHook(vcs);
    }
    
    private void updatePersistentHook(Vcs vcs) {
        if (vcs.getConnectionType() == VcsConnectionType.OAUTH) return;
        try {
            standAloneTokenService.generateAccessTokenTask(vcs.getId().toString());
            log.info("Successfully created a schedule task to generate installation tokens for VCS: {}", vcs.getId());
        } catch (Exception e) {
            log.error("Failed to create a schedule task to generate installation tokens for vc: {}, error {}", vcs.getId(), e);
        }
    }
}
