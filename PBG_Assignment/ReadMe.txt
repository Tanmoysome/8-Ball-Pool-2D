-Open the project either in Eclipse or IntelliJ
-Run the class named Main.
-The jar file is located in PBG_Assignment\out\artifacts\PBG_Assignment_jar\PBG_Assignment.jar .
-double click on the .jar file to run the game.
-Open the command prompt and run the following command

 java -jar PBG_Assignment.jar

- If there is an error like UnsupportedClassVersionError (in cmd prompt) or "A JNI error has occured" (when double clicked on the .jar executable) then check the java version by entering the following command in the command prompt
java -version

-If the version is lower than 11 then install the latest version i.e version 16 from https://www.oracle.com/java/technologies/javase-jdk16-downloads.html

-If you want to run the file using the classpath then 
-Open the folder containing the project (It should contain folders like out, src, .idea)
-launch cmd prompt and enter the following command in the cmd prompt

 java -classpath "out\production\PBG_Assignment;jbox2d-library-2.2.1.1.jar" PBG_Assignment.Main

An eclipse project file is also included called .project along with other files like .classpath and PBG_Assignment.userlibraries which was exported from IntelliJ IDE
I don't have eclipse installed so couldn't check if it works or not.