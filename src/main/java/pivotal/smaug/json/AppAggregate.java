package pivotal.smaug.json;

public class AppAggregate {
	
	public float TotalMemory, TotalDisk, RunningMemory, RunningDisk;
	public int TotalInstanceCount ,
	TotalRunningCount  ,
	TotalAppCount      ,
	JavaBPCount        ,
	RubyBPCount        ,
	NodeBPCount        ,
	GOBPCount          ,
	PythonBPCount      ,
	PHPBPCount         ,
	ExternalBPCount,
	StaticFileBPCount,
	BinaryBPCount,
	OtherBPCount       ,
	StoppedStateCount  ,
	StartedStateCount  ,
	FailedInStoppedStateCount  ,
	DiegoAppsCount     ,
	orgCount,
	spaceCount
	;
	
	public AppAggregate() {}
}
