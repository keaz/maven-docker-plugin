package com.keaz.docker.task;

public interface MavenTask {

    void execute();

    void rollBack();

    void complete();

}
