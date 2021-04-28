# p1_mapreduce-team-88

Video of the working verification : https://drive.google.com/file/d/1PrxqZTZ-MAvtWpT0qOtF2XvZS8zfv7KX/view?usp=sharing

Design Document : https://docs.google.com/document/d/1hNqLwp2Pkl96p7PXzyFD9lZ7Ty_EMTx9MQFKLOLHsOc/edit?usp=sharing


# Map/Reduce Project: 

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
This is a multi-process environment and each worker is a new process.
After the Mappers are succesfully launched and completed, we start the reducers.


Verification :  The test cases that are run include : 
1. Word Count : Count the number of occurences of a word accross multiple files. 
2. Character Count : Compute the character count of all the words occuring across all the input files. 
3. Vowel Count : Compute the number of vowels occuring in all the words appending across the input files.


# Summary/ Brief Description:

MapReduce:
It’s a massive Parallel Processing technique for processing data that is distributed on a commodity server. Therefore, this helps to fasten the processing of some action. There are two main phases in the map-reduce:
1. Map
2. Reduce

The whole data is split into chunks based on some factor so that different machines can perform its action individually! So, if you split your jobs and data, in the map-reduce format you can make use of parallel processing. The computation takes a set of input key/value pairs and produces a set of output key/value pairs. The user of the MapReduce library expresses the computation as two functions: Map and Reduce.
1. Map, written by the user, takes an input pair and pro- duces a set of intermediate key/value pairs. The MapReduce library groups together all intermediate values associated with the same intermediate key I and passes them to the Reduce function.
2. The Reduce function, also written by the user, accepts an intermediate key I and a set of values for that key. It merges these values to form a possibly smaller set of values. Typically just zero or one output value is produced per Reduce invocation. The intermediate values are supplied to the user’s reduce function via an iterator. This allows us to handle lists of values that are too large to fit in memory.
```
map (k1,v1)
reduce (k2,list(v2)) → list(k2,v2) → list(v2)
```
Overall Flow of MapReduce:
1. User Program is first split into M pieces.
2. There is 1 master, and the rest are workers. There are M map tasks and R
reduce tasks
3. A map task worker reads input and performs some function and produces
intermediate results
4. These intermediate results are stored and in the buffer and their location is
sent to the master so that reduce worker can be assigned
5. When the reducer worker is notified, it sorts based on the intermediate
values
6. The reduce worker iterates over the sorted intermediate data and for each
unique intermediate key encountered, it passes the key and the corresponding set of intermediate values to the user’s Reduce function. The output of the Reduce function is appended to a final output file for this reduce partition.
7. When all map tasks and reduce tasks have been completed, the master wakes up the user program. At this point, the MapReduce call in the user program returns to the user code.

Strengths:
1. Parallel Processing can be used
2. Fast
3. Cheaper than a single machine which computes sequentially in the same time
4. Higher Throughput
5. Availability
6. Scalability
