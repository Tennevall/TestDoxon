package testdoxon.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "testdoxon")
public class testdoxon extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Hello testdox World");
		
		// Check javadoc config file
		
		// Read in all methodnames from test classes
		
		// Generate HTML
		
		// Find all non test classes
		
		// Modify javadoc menu
		
		// Modify javadoc class html
		
	}

}
