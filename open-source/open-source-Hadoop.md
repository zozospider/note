
# link

---

# 核心源码

  * __HDFS__
  * __MapReduce__
    * __Job__
      * Job `C` | _waitForCompletion()_, _submit()_, _connect()_, _monitorAndPrintJob()_
      * Cluster `C` | _initialize()_
      * JobSubmitter `C` | _submitJobInternal()_, _copyAndConfigureFiles()_, _writeSplits()_, _writeConf()_, _submitJob()_
      * InputFormat `I` | _getSplits()_
    * __LocalJobRunner__
      * LocalJobRunner `C` > ClientProtocol `I`
        * Job `IC` | _Job()_, _run()_, _runTasks()_
          * MapTaskRunnable `IC` | _run()_
          * ReduceTaskRunnable `IC` | _run()_
    * __MapTask__, __ReduceTask__, __Mapper__, __Reducer__
      * MapTask `C` > Task `AC` | _run()_, _runNewMapper()_, _createSortingCollector()_
        * NewOutputCollector `IC` > RecordWriter `I` | _NewOutputCollector()_, _write()_
        * MapOutputBuffer `IC` > MapOutputCollector `I` | _init()_, _collect()_, _sortAndSpill()_
          * SpillThread `IC` | _run()_
      * ReduceTask `C`
        * NewTrackingRecordWriter `IC` > RecordWriter `I`
      * Mapper `C` | _run()_
      * Reducer `C` | _run()_

---
