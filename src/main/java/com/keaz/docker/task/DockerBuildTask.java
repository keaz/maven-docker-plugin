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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class DockerBuildTask implements MavenTask {

    private final String dockerTag;
    private final File dockerDirectory;

    public DockerBuildTask(String dockerTag, File dockerDirectory) {
        this.dockerTag = dockerTag;
        this.dockerDirectory = dockerDirectory;
    }

    @Override
    public void execute() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(dockerDirectory);
        processBuilder.command("bash", "-c", "docker build -t " + dockerTag + " .");

        try {
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitValue = process.waitFor();
            if (exitValue == 0) {
                System.out.println("Docker build completed");
            }


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveToDockerBuildFolder() {

    }

    @Override
    public void rollBack() {

    }

    @Override
    public void complete() {

    }
}
