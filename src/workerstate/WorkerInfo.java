package workerstate;

import java.time.LocalTime;
import java.util.Objects;

public class WorkerInfo {

    private int workerId;
    private Process workerProcess;              //unable to get process ID from ProcessBuilder in Java 8. Working aroudn this issue until upgrade to Java 9.
    private WorkerType type;
    private WorkerState state;
    private LocalTime startTime;

    WorkerInfo(Process workerProcess, WorkerType type, WorkerState state){
        this.workerProcess = workerProcess;
        this.type = type;
        this.state = state;

        workerProcess.destroy();
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerInfo that = (WorkerInfo) o;
        return workerId == that.workerId && type == that.type;
    }
}
