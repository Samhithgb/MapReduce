# p1_mapreduce-team-88

Video of the working verification : https://drive.google.com/file/d/1PrxqZTZ-MAvtWpT0qOtF2XvZS8zfv7KX/view?usp=sharing

Design Document : https://docs.google.com/document/d/1hNqLwp2Pkl96p7PXzyFD9lZ7Ty_EMTx9MQFKLOLHsOc/edit?usp=sharing


# Milestone 1: 

For running all tests in the milestone 1, please checkout the project, and run the following commands in the command line:  \


For Unix-based systems : 

```
java *.java 
java RunTests <path_to>/input_data_paths.txt
```
For Windows : 
```
java *.java 
java RunTests <path_to>\input_data_paths.txt

```
Notes : 

- The implementation currently arleady takes care of multiple worker processes and their communication of the states to master. The master periodically checks for the status of the workers


Verification :  The test cases that are run include : 
1. Word Count : Count the number of occurences of a word accross multiple files. 
2. Character Count : Compute the character count of all the words occuring across all the input files. 
3. Vowel Count : Compute the number of vowels occuring in all the words appeading across the input files
