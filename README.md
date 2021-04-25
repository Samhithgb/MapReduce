# p1_mapreduce-team-88

Video of the working verification : https://drive.google.com/file/d/1PrxqZTZ-MAvtWpT0qOtF2XvZS8zfv7KX/view?usp=sharing

Design Document : https://docs.google.com/document/d/1hNqLwp2Pkl96p7PXzyFD9lZ7Ty_EMTx9MQFKLOLHsOc/edit?usp=sharing


# Milestone 1: 

Initial Step : Please update input_data_paths.txt with the paths to the input files with each path being in a new line. 
Also update the paths and variables accordingly in the Config.txt.


For running all tests, please checkout the project, and run the following commands in the command line:  \
Firstly, go to the following corresponding location in your system.

1. Go to the src folder. \
  Ex: /Users/ssrigiri/umass/532/Homeworks/project-1/p1_mapreduce-team-88/src
2. Run the following command. \
  For mac based systems:
  ```
  javac -d /Users/ssrigiri/umass/532/Homeworks/project-1/p1_mapreduce-team-88/out/production/project_folder/ *.java
  ```
  [Note]: Change the above path accordingly
  
3. Go to the folder where the .class files are stored \
  Ex: /Users/ssrigiri/umass/532/Homeworks/project-1/p1_mapreduce-team-88/out/production/project_folder
4. Run the following command.\
  For mac based systems:
  ```
  java  RunTests /Users/ssrigiri/umass/532/Homeworks/project-1/p1_mapreduce-team-88/src/Config.txt
  ```
  [NOTE]: Give the correct path of the Config.txt



Notes : 

The implementation currently already takes care of multiple worker processes and their communication of the states to master. The master periodically checks for the status of the workers.


Verification :  The test cases that are run include : 
1. Word Count : Count the number of occurences of a word accross multiple files. 
2. Character Count : Compute the character count of all the words occuring across all the input files. 
3. Vowel Count : Compute the number of vowels occuring in all the words appending across the input files.
