package com.keaz;


import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;

import java.io.File;

public class MyMojoTest extends AbstractMojoTestCase {


    @Rule
    public MojoRule rule = new MojoRule()
    {
        @Override
        protected void before() throws Throwable
        {
        }

        @Override
        protected void after()
        {
        }
    };

    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }

    /**
     * @throws Exception if any
     */
//    @Test
    public void testSomething() throws Exception {

        File pom = new File(getBasedir(),"src/test/resources/project-to-test/pom.xml");
        assertNotNull( pom );
        assertTrue( pom.exists() );

        rule.lookupMojo("docker-build",pom);

    }


}

