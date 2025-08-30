package webapp.AwesomeCollect.analysis;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import webapp.AwesomeCollect.awesome.AwesomePointCalculator;
import webapp.AwesomeCollect.dto.DashboardDto;
import webapp.AwesomeCollect.dto.analysis.TagLearningHoursDto;
import webapp.AwesomeCollect.dto.analysis.TotalLearningHoursDto;
import webapp.AwesomeCollect.service.LearningHoursService;

@Component
public class DashboardHelper {

  private final AwesomePointCalculator awesomePointCalculator;
  private final LearningHoursService learningHoursService;

  private static final String UNCATEGORIZED = "(未設定)";

  public DashboardHelper(
      AwesomePointCalculator awesomePointCalculator, LearningHoursService learningHoursService){

    this.awesomePointCalculator = awesomePointCalculator;
    this.learningHoursService = learningHoursService;
  }

  public DashboardDto prepareDashboardData(int userId, HttpSession session){
    DashboardDto dto = prepareLearningHoursData(session, userId);
    dto.setTotalAwesome(prepareAwesomePointData(session, userId));

    return dto;
  }

  private Integer prepareAwesomePointData(HttpSession session, int userId) {
    Integer awesomePoint = (Integer) session.getAttribute("cachedAwesome");
    Boolean hasNewRecord = (Boolean) session.getAttribute("hasNewRecord");

    if(hasNewRecord == null || hasNewRecord){
      awesomePoint = awesomePointCalculator.calculateAwesomePoint(userId);
      session.setAttribute("cachedAwesome", awesomePoint);
      session.setAttribute("hasNewRecord", false);
    }

    return awesomePoint;
  }

  private DashboardDto prepareLearningHoursData(HttpSession session, int userId) {
    BigDecimal totalHours = (BigDecimal) session.getAttribute("cachedTotalHours");
    List<TotalLearningHoursDto> sevenDaysHoursList =
        (List<TotalLearningHoursDto>) session.getAttribute("cachedDailyHoursList");
    List<TotalLearningHoursDto> sixMonthHoursList =
        (List<TotalLearningHoursDto>) session.getAttribute("cachedMonthlyHoursList");
    List<TagLearningHoursDto> totalHoursByTag =
        (List<TagLearningHoursDto>) session.getAttribute("cachedTotalHoursByTag");

    Boolean hasNewHours = (Boolean) session.getAttribute("hasNewHours");
    if(hasNewHours == null || hasNewHours) {

      totalHours = learningHoursService.getTotalHours(userId);

      LocalDate today = LocalDate.now();
      sevenDaysHoursList = getSevenDaysHoursList(userId, today);
      sixMonthHoursList = getSixMonthHoursList(userId, today);

      totalHoursByTag = getTotalHoursByTag(userId);

      session.setAttribute("cachedTotalHours", totalHours);
      session.setAttribute("cachedDailyHoursList", sevenDaysHoursList);
      session.setAttribute("cachedMonthlyHoursList", sixMonthHoursList);
      session.setAttribute("cachedTotalHoursByTag", totalHoursByTag);
      session.setAttribute("hasNewHours", false);
    }

    DashboardDto dto = new DashboardDto();
    dto.setTotalHours(totalHours);
    dto.setDailyHoursList(sevenDaysHoursList);
    dto.setMonthlyHoursList(sixMonthHoursList);
    dto.setTotalHoursByTag(totalHoursByTag);

    return dto;
  }

  private @NotNull List<TotalLearningHoursDto> getSevenDaysHoursList(int userId, LocalDate today) {
    LocalDate fromDate = today.minusDays(6);

    List<LocalDate> dateList =
        IntStream.rangeClosed(0, 6)
            .mapToObj(i -> today.minusDays(6 - i))
            .toList();

    List<TotalLearningHoursDto> dailyHoursList =
        learningHoursService.getDailyTotalHours(userId, fromDate, today);

    Map<LocalDate, BigDecimal> hoursMap = dailyHoursList.stream()
        .collect(Collectors.toMap(TotalLearningHoursDto ::getDate, TotalLearningHoursDto ::getTotalHours));

    return dateList.stream()
        .map(d -> {
          BigDecimal hours = hoursMap.getOrDefault(d, BigDecimal.ZERO);
          return new TotalLearningHoursDto(d, hours);
        })
        .toList();
  }

  private @NotNull List<TotalLearningHoursDto> getSixMonthHoursList(int userId, LocalDate today) {
    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate fromDate = firstDayOfThisMonth.minusMonths(5);

    List<LocalDate> monthList =
        IntStream.rangeClosed(0, 5)
            .mapToObj(i -> firstDayOfThisMonth.minusMonths(5 - i))
            .toList();

    List<TotalLearningHoursDto> monthlyHoursList =
        learningHoursService.getMonthlyTotalHours(userId, fromDate, today);

    Map<LocalDate, BigDecimal> hoursMap = monthlyHoursList.stream()
        .collect(Collectors.toMap(TotalLearningHoursDto ::getDate, TotalLearningHoursDto ::getTotalHours));

    return monthList.stream()
        .map(m -> {
          BigDecimal hours = hoursMap.getOrDefault(m, BigDecimal.ZERO);
          return new TotalLearningHoursDto(m, hours);
        })
        .toList();
  }

  private @NotNull List<TagLearningHoursDto> getTotalHoursByTag(int userId) {
    List<TagLearningHoursDto> tagHoursList = learningHoursService.getTotalHoursByTag(userId);

    return  tagHoursList.stream()
        .sorted((a, b) -> {
          if (UNCATEGORIZED.equals(a.getTagName()) && !UNCATEGORIZED.equals(b.getTagName())) {
            return 1;
          } else if (!UNCATEGORIZED.equals(a.getTagName()) && UNCATEGORIZED.equals(b.getTagName())) {
            return -1;
          } else {
            return b.getTotalHours().compareTo(a.getTotalHours());
          }
        })
        .limit(10)
        .toList();
  }

  public DashboardDto prepareAllTagHoursView(HttpSession session, int userId) {
    List<TagLearningHoursDto> totalHoursByAllTag =
        (List<TagLearningHoursDto>) session.getAttribute("cachedTotalHoursByAllTag");

    if(totalHoursByAllTag == null){
      List<TagLearningHoursDto> tagHoursList = learningHoursService.getTotalHoursByTag(userId);

      totalHoursByAllTag = tagHoursList.stream()
          .sorted((a, b) -> {
            if (UNCATEGORIZED.equals(a.getTagName()) && !UNCATEGORIZED.equals(b.getTagName())) {
              return 1;
            } else if (!UNCATEGORIZED.equals(a.getTagName()) && UNCATEGORIZED.equals(b.getTagName())) {
              return -1;
            } else {
              return b.getTotalHours().compareTo(a.getTotalHours());
            }
          })
          .toList();

      session.setAttribute("cachedTotalHoursByAllTag", totalHoursByAllTag);
    }

    DashboardDto dto = new DashboardDto();
    dto.setTotalHoursByTag(totalHoursByAllTag);

    return dto;
  }
}
