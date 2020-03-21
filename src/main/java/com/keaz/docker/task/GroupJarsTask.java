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
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;

import java.util.*;
import java.util.stream.Collectors;

public class GroupJarsTask implements MavenTask {


    private final MavenProject project;
    private final MavenSession session;

    public GroupJarsTask(MavenProject project, MavenSession session) {
        this.project = project;
        this.session = session;
    }

    @Override
    public void execute() {
        Map<String, Artifact> artifactMap = project.getArtifactMap();
        Set<Artifact> artifacts = project.getArtifacts();

        LinkedList<Artifact> sortedArtifacts = new LinkedList(artifacts);
        Collections.sort(sortedArtifacts, (artifact1, artifact2) -> {
            if (artifact1.getGroupId().equals(artifact2.getGroupId())) {
                return 0;
            }
            return artifact1.getGroupId().compareTo(artifact2.getGroupId());

        });

        Map<String, List<Artifact>> collect = artifacts.stream()
                .collect(Collectors.groupingBy(Artifact::getVersion));


        ProjectDependencyGraph projectDependencyGraph = session.getProjectDependencyGraph();

    }

    @Override
    public void rollBack() {

    }

    @Override
    public void complete() {

    }
}
