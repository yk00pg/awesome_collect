package webapp.AwesomeCollect.provider.param;

import java.time.LocalDate;
import java.util.List;

public record ExpiredUserParams(List<Integer> userIdList, LocalDate expiredDate) {}
