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

import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.util.Arrays;

public class CreateJarTask implements MavenTask {

    private static final String[] DEFAULT_INCLUDES = new String[]{"**/**"};
    private static final String[] DEFAULT_EXCLUDES = new String[]{"**/package.html"};

    private final String jarFinalName;
    private final File classDirectory;
    private final File dockerDirectory;
    private final String[] includes;
    private final String[] excludes;

    /**
     * ssss.
     *
     * @param jarFinalName    s
     * @param classDirectory  s
     * @param dockerDirectory s
     * @param includes        s
     * @param excludes        s
     */
    public CreateJarTask(String jarFinalName, File classDirectory, File dockerDirectory,
                         String[] includes, String[] excludes) {
        this.jarFinalName = jarFinalName;
        this.classDirectory = classDirectory;
        this.dockerDirectory = dockerDirectory;
        this.includes = includes;
        this.excludes = excludes;
    }

    @Override
    public void execute() {
        final File jarFile = createJarFile();
        FileSetManager fileSetManager = new FileSetManager();
        FileSet classFileSet = new FileSet();
        classFileSet.setDirectory(classDirectory.getAbsolutePath());
        classFileSet.setIncludes(Arrays.asList(getIncludes()));
        classFileSet.setExcludes(Arrays.asList(getExcludes()));

        String[] includedFiles = fileSetManager.getIncludedFiles(classFileSet);

        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(new JarArchiver());
        archiver.setOutputFile(jarFile);

    }

    @Override
    public void rollBack() {

    }

    @Override
    public void complete() {

    }


    private File createJarFile() {
        if (jarFinalName == null) {
            throw new IllegalArgumentException("Final name is cannot be null");
        }

        return new File(dockerDirectory, jarFinalName + ".jar");
    }

    private String[] getIncludes() {
        if (includes != null && includes.length > 0) {
            return includes;
        }
        return DEFAULT_INCLUDES;
    }

    private String[] getExcludes() {
        if (excludes != null && excludes.length > 0) {
            return excludes;
        }
        return DEFAULT_EXCLUDES;
    }
}
