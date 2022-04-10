import ca.dal.distributed.dpg1.Utils.RemoteUtils;

public class DistributedDatabase {
    public static void main(String[] args) {
        if (RemoteUtils.isInternalCommand(args)) {
            RemoteUtils.executeInternalCommand(args);
        }

        // Download example
//        if (args[0].equals("remote")) {
//            RemoteUtils.download(RemoteConstants.DUMP_LOCATION + "db1", RemoteConstants.DUMP_LOCATION + "db1");
//        }
    }
}
