package com.keaz.docker.task;


import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CopyJarFileTask implements MavenTask {

    private final String finalName;
    private final String buildDirectory;
    private final String dockerDirectory;


    public CopyJarFileTask(String finalName, String buildDirectory, String dockerDirectory) {
        this.finalName = finalName;
        this.buildDirectory = buildDirectory;
        this.dockerDirectory = dockerDirectory;
    }

    @Override
    public void execute() {
        String jarFileName = finalName + ".jar";
        try {
            Files.copy(Paths.get(buildDirectory,jarFileName),Paths.get(dockerDirectory, jarFileName), StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollBack() {

    }

    @Override
    public void complete() {

    }
}
