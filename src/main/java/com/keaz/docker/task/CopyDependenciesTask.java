package com.keaz.docker.task;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class CopyDependenciesTask implements MavenTask {

    public static final String LIB_FOLDER = "lib";

    private final Set<Artifact> artifacts;
    private final String dockerBuildFolder;
    private Path lib = null;

    public CopyDependenciesTask(Set<Artifact> artifacts, String dockerBuildFolder) {
        this.artifacts = artifacts;
        this.dockerBuildFolder = dockerBuildFolder;
    }

    @Override
    public void execute() {

        lib = Paths.get(dockerBuildFolder, LIB_FOLDER);
        if (!Files.isExecutable(lib)) {
            try {
                Files.createDirectories(lib);
            }catch (IOException ex){

            }
        }

        try {
            for (Artifact artifact : artifacts) {
                String scope = artifact.getScope();
                if (scope.equals(Artifact.SCOPE_PROVIDED) || scope.equals(Artifact.SCOPE_SYSTEM) || scope
                        .equals(Artifact.SCOPE_TEST)) {
                    continue;
                }

                String absolutePath = artifact.getFile().getAbsolutePath();
                File jarM2Location = artifact.getFile();
                Files.copy(Paths.get(absolutePath),Paths.get(lib.toAbsolutePath().toString(), jarM2Location.getName()), StandardCopyOption.COPY_ATTRIBUTES);

            }
        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }

    }

    @Override
    public void rollBack() {
        complete();
    }

    @Override
    public void complete() {
//        lib.delete();
    }
}
