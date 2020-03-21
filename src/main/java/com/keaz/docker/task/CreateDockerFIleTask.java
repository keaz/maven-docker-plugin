package com.keaz.docker.task;


import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CreateDockerFIleTask extends AbstractMavenTask {


    private final String baseImage;
    private final String runDArguments;
    private final String dockerBuildDirectory;
    private final String finalName;
    private final String mainClass;
    private final Map<String, List<Artifact>> groupedArtifacts;

    private Path dockerFile = null;


    public CreateDockerFIleTask(String baseImage, String runDArguments, String dockerBuildDirectory,
                                String finalName, String mainClass, Map<String, List<Artifact>> groupedArtifacts, Log log) {
        super(log);
        this.baseImage = baseImage;
        this.runDArguments = runDArguments;
        this.dockerBuildDirectory = dockerBuildDirectory;
        this.finalName = finalName;
        this.mainClass = mainClass;
        this.groupedArtifacts = groupedArtifacts;

    }

    @Override
    public void execute() {

        Log log = getLog();

        dockerFile = Paths.get(dockerBuildDirectory,"Dockerfile");
        if (Files.exists(dockerFile)) {
            log.info("Deleting existing docker file "+ dockerFile.toAbsolutePath());
            try {
                Files.delete(dockerFile);
            }catch (IOException ioEx){
                log.error("Failed to delete " + dockerFile.toAbsolutePath());
            }
        }

        BufferedWriter bufferedWriter = null;

        try {
            log.info("Creating docker file" + dockerFile.toAbsolutePath());
            Path newDockerFile = Files.createFile(dockerFile);
            log.info("Docker file created " + newDockerFile.toAbsolutePath());

            bufferedWriter = Files.newBufferedWriter(newDockerFile, StandardOpenOption.WRITE);
            bufferedWriter.write("FROM " + baseImage);
            bufferedWriter.newLine();
            bufferedWriter.write("WORKDIR /app");
            bufferedWriter.newLine();
            bufferedWriter.write("RUN mkdir lib");
            bufferedWriter.newLine();

            for (Map.Entry<String,List<Artifact> >groupedArtifact : groupedArtifacts.entrySet()) {
                List<Artifact> artifacts = groupedArtifact.getValue();
                StringBuilder copyCommand = new StringBuilder();
                for (Artifact artifact : artifacts) {
                    copyCommand.append("lib/").append(artifact.getFile().getName()).append(" ");
                }
                bufferedWriter.write("COPY " + copyCommand + " lib/");
                bufferedWriter.newLine();
            }

            bufferedWriter.write("COPY "+finalName+".jar .");
            bufferedWriter.newLine();
            bufferedWriter.write("ENTRYPOINT java " + runDArguments + " -cp \"" + finalName + ".jar:lib/*\" " + mainClass);


        }catch (IOException ex){
            log.error("Failed to create docker file "+dockerFile.toAbsolutePath(),ex);
            throw new RuntimeException(ex);
        }finally {
            if(bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException ioEx) {
                    log.error("Failed to close docker file "+dockerFile.toAbsolutePath(),ioEx);
                }
            }
        }


    }

    @Override
    public void rollBack() {
        complete();
    }

    @Override
    public void complete() {
//        dockerFile.delete();
    }

}
