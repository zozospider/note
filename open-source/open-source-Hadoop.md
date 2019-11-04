
# link

---

# 核心源码

  * __HDFS__
  * __MapReduce__
    * __Job__
      * Job `C`
    * __LocalJobRunner__
      * LocalJobRunner `C`
        * Job \[_Job()_, _run()_, _runTasks()_\] `IC`
          * MapTaskRunnable \[_run()_\] `IC`
          * ReduceTaskRunnable \[_run()_\] `IC`
    * __MapTask__, __ReduceTask__
      * MapTask \[_run()_, _runNewMapper()_, _createSortingCollector()_\] `C` -> Task `AC`
        * NewOutputCollector \[_NewOutputCollector()_, _write()_\] `IC` -> RecordWriter `I`
        * MapOutputBuffer \[_init()_, _collect()_, _sortAndSpill()_\] `IC` -> MapOutputCollector `I`
          * SpillThread \[_run()_\] `IC`
      * ReduceTask
        * NewTrackingRecordWriter `IC` -> RecordWriter `I`

---
