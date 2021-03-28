package workerstate;

public class WorkerInfo {

    private int workerId;
    private Process workerProcess;              //unable to get process ID from ProcessBuilder in Java 8. Working aroudn this issue until upgrade to Java 9.
    private WorkerType type;
    private WorkerState state;

    WorkerInfo(Process workerProcess, WorkerType type, WorkerState state){
        this.workerProcess = workerProcess;
        this.type = type;
        this.state = state;
    }

    public WorkerInfo(){}

    public WorkerType getType() {
        return type;
    }

    public void setType(WorkerType type) {
        this.type = type;
    }

    public Process getWorkerProcess() {
        return workerProcess;
    }

    public void setWorkerId(int id){
        this.workerId = id;
    }

    public void setWorkerProcess(Process workerId) {
        this.workerProcess = workerId;
    }

    public WorkerState getState() {
        return state;
    }

    public void setState(WorkerState state) {
        this.state = state;
    }

    public int getWorkerId() {
        return workerId;
    }
}
