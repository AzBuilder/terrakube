package org.azbuilder.registry.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GitServiceImpl implements GitService{
    @Override
    public void getRepositoryTags(String repository) {
        LsRemoteCommand ls = Git.lsRemoteRepository();
        try {
            Map<String,Ref> remoteRefs = ls
                    .setRemote(repository) // true by default, set to false if not interested in refs/heads/*
                    .setTags(true)  // include tags in result
                    .callAsMap();

            remoteRefs.forEach((k,v)->{
                System.out.println(k);
            });
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

    }
}
