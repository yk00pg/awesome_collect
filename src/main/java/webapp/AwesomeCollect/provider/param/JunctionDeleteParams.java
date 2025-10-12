package webapp.AwesomeCollect.provider.param;

import java.util.List;

public record JunctionDeleteParams(
    String tableName, String columnName, List<Integer> actionIdList) {}
