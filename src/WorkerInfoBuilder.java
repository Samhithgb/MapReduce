import workerstate.WorkerInfo;
import workerstate.WorkerState;
import workerstate.WorkerType;

public class WorkerInfoBuilder {
    WorkerInfo info;

    public WorkerInfoBuilder(){
       info = new WorkerInfo();
    }

    public WorkerInfoBuilder setWorkerState(WorkerState state){
        info.setState(state);
        return this;
    }

    public WorkerInfoBuilder setWorkerType(WorkerType type){
        info.setType(type);
        return this;
    }

    public WorkerInfoBuilder setWorkerProcess(Process process){
        info.setWorkerProcess(process);
        return this;
    }

    public WorkerInfoBuilder setWorkerId(int id){
        info.setWorkerId(id);
        return this;
    }

    public WorkerInfo build(){
        if(info.getWorkerProcess()!=null && info.getState()!=null && info.getType()!=null) {
            return info;
        } else {
            throw new IllegalStateException("Worker State has missing entries. Unable to build");
        }
    }

}
