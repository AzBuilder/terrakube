FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 17.0.6-tem && sdk default java 17.0.6-tem && \
    sdk install maven 3.9.0 && sdk default maven 3.9.0"