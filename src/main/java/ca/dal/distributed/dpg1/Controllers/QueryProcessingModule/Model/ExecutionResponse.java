package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Model;

/**
 * @author Ankush Mudgal
 * Execution Response - Object to store the status of the execution of current query.
 */
public class ExecutionResponse {

    private boolean isExecutionSuccess = false;
    private String executionResponseMsg = "Execution Failed";

    /**
     * @author Ankush Mudgal
     * Instantiates a new Execution response.
     *
     * @param isExecutionSuccess   the is execution success
     * @param executionResponseMsg the execution response msg
     */
    public ExecutionResponse(boolean isExecutionSuccess, String executionResponseMsg) {

        this.isExecutionSuccess = isExecutionSuccess;
        this.executionResponseMsg = executionResponseMsg;
    }

    @Override
    public String toString() {
        return "ExecutionResponse { " + "isExecutionSuccess=" + isExecutionSuccess + "\n" +
                ", executionResponseMsg='" + executionResponseMsg + '}';
    }

    public boolean isExecutionSuccess() {
        return isExecutionSuccess;
    }
}
