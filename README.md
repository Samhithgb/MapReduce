# p1_mapreduce-team-88
Please watch the below video and make the corresponding changes in your files (Ex: Config.txt, input_data_paths.txt) and IDEA Configurations.
Video of the working verification: https://youtu.be/FEQfV0Rkg-8

Design Document: https://docs.google.com/document/d/1hNqLwp2Pkl96p7PXzyFD9lZ7Ty_EMTx9MQFKLOLHsOc/edit?usp=sharing


# Map/Reduce Project: 

Initial Step: Please update input_data_paths.txt with the paths to the input files with each path being in a new line. 
Also, update the paths and variables accordingly in the Config.txt.


For running all tests, please check out the project, and perform the following actions in IDEA: 

1. IDEA's working directory should be the project folder (where out, src and inputData folders are located)
2. Compile RunTests and add the following argumet to the configuration `./src/Config.txt`
3. Run the `RunTests` compiled file

  [NOTE]: Give the correct path of the Config.txt



Notes : 

The implementation currently already takes care of multiple worker processes and their communication of the states to master. The master periodically checks for the status of the workers.
This is a multi-process environment and each worker is a new process.
After the Mappers are successfully launched and completed, we start the reducers.



Verification:  The test cases that are run include : 
1. Word Count: Count the number of occurrences of a word across multiple files. 
2. Character Count: Compute the character count of all the words occurring across all the input files. 
3. Vowel Count: Compute the number of vowels occurring in all the words appending across the input files.


For each test, we also test fault tolerance by putting 1-2 workers in an infinite loop. Master detects when this happens and restarts the workers. The amount of time the master waits before restarting workers can be set by modifying the `worker_threshold` variable in `Config.txt`. Master will attempt to restart the failed worker for a fixed number of times as specified by the `relaunch_times` variable in `Config.txt`

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

# Architecture Design

<img width="843" alt="Screenshot 2021-04-28 at 7 39 31 PM" src="https://user-images.githubusercontent.com/29397962/116418319-a8a6ee00-a859-11eb-8163-0dae8da621c7.png">

<img width="785" alt="Screenshot 2021-04-28 at 7 40 10 PM" src="https://user-images.githubusercontent.com/29397962/116418336-ac3a7500-a859-11eb-9afd-bfcffa7cce1c.png">
