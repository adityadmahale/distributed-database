package ca.dal.distributed.dpg1.Utils;

/**
 * @author Ankush Mudgal
 * The type Local utils.
 *
 */
public class LocalUtils {

    /**
     * @author Ankush Mudgal
     * Gets local host ip.
     *
     * @return the local host ip
     */
    public static String getLocalHostIp() {

        String localIp = System.getenv(LocalConstants.LOCAL_HOST_ENV_IP);
        if (localIp == null || localIp.isEmpty()) {
            throw new IllegalStateException(LocalConstants.ERROR_LOCAL_ENV_VARIABLE + LocalConstants.LOCAL_HOST_ENV_IP);
        }
        return localIp;
    }
}
