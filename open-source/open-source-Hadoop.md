
# link

---

# 核心源码

  * __HDFS__
  * __MapReduce__
    * __Job__
      * Job `C`
    * __LocalJobRunner__
      * LocalJobRunner `C`
        * Job (_Job()_ _run()_ _runTasks()_) `IC`
          * MapTaskRunnable `IC`
          * ReduceTaskRunnable `IC`
    * __MapTask__, __ReduceTask__
      * MapTask `C`
        * NewOutputCollector `IC` -> RecordWriter `I`
        * MapOutputBuffer `IC` -> MapOutputCollector `I`
          * SpillThread `IC`
      * ReduceTask
        * NewTrackingRecordWriter `IC` -> RecordWriter `I`

---
