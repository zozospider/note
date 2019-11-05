
# link

---

# 核心源码

  * __HDFS__
  * __MapReduce__
    * __Job__
      * Job `C` | _waitForCompletion()_ `a1`, _submit()_ `a11`, _connect()_ `a111`, _monitorAndPrintJob()_ `a12`
      * Cluster `C` | _initialize()_ `a1111`
      * JobSubmitter `C` | _submitJobInternal()_ `a112`, _copyAndConfigureFiles()_ `a1121`, _writeSplits()_ `a1122`, _writeConf()_ `a1123`, _submitJob()_ `a1124`
    * __LocalJobRunner__
      * LocalJobRunner `C` > ClientProtocol `I` | _submitJob()_ `a11241`
        * Job `IC` | _Job()_ `a11242` > `b`, _run()_ `b1`, _runTasks()_ `b11` > `c`, `e`
          * MapTaskRunnable `IC` | _run()_ `c1`
          * ReduceTaskRunnable `IC` | _run()_ `e1`
    * __MapTask__, __Mapper__, __ReduceTask__, __Reducer__
      * MapTask `C` > Task `AC` | _run()_ `c11`, _runNewMapper()_ `c111`, _createSortingCollector()_ `c11111`
        * NewOutputCollector `IC` > RecordWriter `I` | _NewOutputCollector()_ `c1111`, _write()_ `c111221`
        * MapOutputBuffer `IC` > MapOutputCollector `I` | _init()_ `c111111` > `d`, _sortAndSpill()_ `d11`, _collect()_ `c1112211`
          * SpillThread `IC` | _run()_ `d1`
      * Mapper `C` | _run()_ `c1112`, _setup()_`c11121`, _map()_ `c11122`, _cleanup()_ `c11122`
      * ReduceTask `C`
        * NewTrackingRecordWriter `IC` > RecordWriter `I`
      * Reducer `C` | _run()_
    * __InputFormat__
      * InputFormat `I` | _getSplits()_

---
