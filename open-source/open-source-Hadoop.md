
# link

---

# 核心源码

  * __HDFS__
  * __MapReduce__
    * __Job__
      * Job `C`
    * __LocalJobRunner__
      * LocalJobRunner `C`
        * Job `IC` | _Job()_, _run()_, _runTasks()_
          * MapTaskRunnable `IC` | _run()_
          * ReduceTaskRunnable `IC` | _run()_
    * __MapTask__, __ReduceTask__
      * MapTask `C` > Task `AC` | _run()_, _runNewMapper()_, _createSortingCollector()_
        * NewOutputCollector `IC` > RecordWriter `I` | _NewOutputCollector()_, _write()_
        * MapOutputBuffer `IC` > MapOutputCollector `I` | _init()_, _collect()_, _sortAndSpill()_
          * SpillThread `IC` | _run()_
      * ReduceTask
        * NewTrackingRecordWriter `IC` > RecordWriter `I`

---
