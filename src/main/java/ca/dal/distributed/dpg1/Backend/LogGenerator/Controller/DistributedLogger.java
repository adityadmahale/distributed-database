package ca.dal.distributed.dpg1.Backend.LogGenerator.Controller;

import java.time.Instant;

public interface DistributedLogger {
    public void logData(final String message, final Instant instant);
    public void storeQueryLog(final String message, final Instant instant);
}
