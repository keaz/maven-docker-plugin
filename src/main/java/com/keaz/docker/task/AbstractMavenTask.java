package com.keaz.docker.task;

import lombok.AllArgsConstructor;
import org.apache.maven.plugin.logging.Log;

@AllArgsConstructor
public abstract class AbstractMavenTask implements MavenTask {

    private Log log;


    public Log getLog()
    {
        return log;
    }


}
