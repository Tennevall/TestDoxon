# TestDoxon
TestDoxon is an open-source test tool project for Eclipse, IntelliJ and maven site.
Here follows guides for developers on how to get started with each of the plugins.
## Eclipse plugin
 - Pull the code from git and open the project in Eclipse IDE. 
 - Go to the Eclipse marketplace and install “Eclipse PDE 3.10 Luna”. 
 - Right-click the project and select “Build path” -> “Add libraries”. 
 - Choose “Plug-in dependencies” -> “Next” -> “Finish”.
 - Right-click the project. Select “run as” -> “Run configurations”. 
 - Double click “Eclipse-Application” -> “Run”. 

	When you run the project as an eclipse application 
	a new IDE window will appear with the plugin installed to for you to test your changes.

## IntelliJ plugin
 - Start by pulling the code from git and open it in the intellij IDE.
 - Run the program as a gradle project with the task “:runIde”. 
 - Go to the dropdown at the top right of the IDE and select “Edit Configurations”. 
 - Under “Default” choose “gradle” and open the TestDoxon project.
 - Then set the task to “:runIde”. 

	Now you can modify the code and test your solution.

## Maven-site plugin
 - Make sure JAVA_HOME is set to jdk 1.8
 - Install maven Apache maven 3.5.3
 - Pull the code from git and open it in a IDE of your choice.
 - Run Install.bat