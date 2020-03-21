package com.keaz;

import com.keaz.docker.Stage;
import com.keaz.docker.task.*;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Mojo(name = "docker-build", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class DockerBuildInitMojo extends AbstractMojo {

    @Component
    protected PlexusContainer container;
    @Parameter(property = "docker-build.stages")
    private Stage stage;
    @Parameter(property = "docker-build.dockerTag", required = true)
    private String dockerTag;
    @Parameter(property = "docker-build.mainClass", required = true)
    private String mainClass;
    @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
    private String finalName;
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private String buildDirectory;
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;


    @Override
    public void execute() {

        Log log = getLog();

        Path dockerDirectory = Paths.get(buildDirectory, "docker");
        try {
            if (Files.exists(dockerDirectory)) {
                log.info("Deleting existing build directory " + dockerDirectory);
                Files.delete(dockerDirectory);
            }

            log.info("Creating docker build directory " + dockerDirectory);
            Files.createDirectories(dockerDirectory);

        } catch (IOException e) {
            log.error("Error deleting " + dockerDirectory);
        }


        Set<Artifact> artifacts = project.getArtifacts();
        Map<String, List<org.apache.maven.artifact.Artifact>> groupedArtifacts = artifacts.stream()
                .collect(Collectors.groupingBy(Artifact::getGroupId));

        List<MavenTask> mavenTasks = new LinkedList<>();

        mavenTasks.add(new GroupJarsTask(project, session));

        mavenTasks.add(new CopyDependenciesTask(artifacts, dockerDirectory.toAbsolutePath().toString()));
        mavenTasks.add(new CreateDockerFIleTask(stage.getBaseImage(), "", dockerDirectory.toAbsolutePath().toString(), finalName, mainClass, groupedArtifacts, log));
        mavenTasks.add(new CopyJarFileTask(finalName, buildDirectory, dockerDirectory.toAbsolutePath().toString()));
        start(mavenTasks);
        complete(mavenTasks);

    }


    private void start(List<MavenTask> mavenTasks) {
        for (MavenTask mavenTask : mavenTasks) {
            mavenTask.execute();
        }
    }

    private void complete(List<MavenTask> mavenTasks) {
        Collections.reverse(mavenTasks);// Complete task from last to first
        for (MavenTask mavenTask : mavenTasks) {
            mavenTask.complete();
        }
    }


}


