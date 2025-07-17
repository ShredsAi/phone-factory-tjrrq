package ai.shreds.application.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * Calculator for determining expected delivery dates based on supplier lead times and priorities.
 */
@Component
@Slf4j
public class ApplicationDeliveryDateCalculator {

    // Holiday dates (in a real implementation this would come from a holiday service)
    private static final Set<LocalDate> HOLIDAYS = Set.of(
            LocalDate.of(2024, 1, 1),   // New Year's Day
            LocalDate.of(2024, 7, 4),   // Independence Day
            LocalDate.of(2024, 12, 25)  // Christmas
    );
    
    /**
     * Calculates the expected delivery date based on supplier lead time and priority.
     * 
     * @param supplierLeadTime The supplier's lead time in days
     * @param priority The priority of the order (HIGH, MEDIUM, LOW)
     * @return The calculated expected delivery date
     */
    public LocalDateTime calculateExpectedDelivery(Integer supplierLeadTime, String priority) {
        log.debug("Calculating expected delivery with lead time {} days and priority {}", supplierLeadTime, priority);
        
        // Start with current date
        LocalDate baseDate = LocalDate.now();
        
        // Adjust lead time based on priority
        int adjustedLeadTime = adjustLeadTimeForPriority(supplierLeadTime, priority);
        
        // Add lead time to base date
        LocalDate expectedDate = baseDate.plusDays(adjustedLeadTime);
        
        // Adjust for weekends
        expectedDate = adjustForWeekends(expectedDate);
        
        // Adjust for holidays
        expectedDate = adjustForHolidays(expectedDate);
        
        // Return as datetime with standard delivery time (9 AM)
        LocalDateTime result = expectedDate.atTime(LocalTime.of(9, 0));
        
        log.debug("Calculated expected delivery date: {}", result);
        return result;
    }
    
    /**
     * Adjusts lead time based on order priority.
     * 
     * @param baseLeadTime The base lead time in days
     * @param priority The order priority
     * @return The adjusted lead time
     */
    private int adjustLeadTimeForPriority(Integer baseLeadTime, String priority) {
        if (baseLeadTime == null || baseLeadTime <= 0) {
            baseLeadTime = 7; // Default to 7 days if not specified
        }
        
        switch (priority != null ? priority.toUpperCase() : "MEDIUM") {
            case "HIGH":
                // High priority orders get expedited delivery (reduce lead time by 2 days, minimum 1 day)
                return Math.max(1, baseLeadTime - 2);
            case "LOW":
                // Low priority orders can take longer (add 1 day)
                return baseLeadTime + 1;
            case "MEDIUM":
            default:
                // Medium priority uses standard lead time
                return baseLeadTime;
        }
    }
    
    /**
     * Adjusts the delivery date to avoid weekends by moving to the next weekday.
     * 
     * @param baseDate The base delivery date
     * @return The adjusted date avoiding weekends
     */
    public LocalDate adjustForWeekends(LocalDate baseDate) {
        LocalDate adjustedDate = baseDate;
        
        // Move to next Monday if delivery falls on weekend
        if (adjustedDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            adjustedDate = adjustedDate.plusDays(2);
        } else if (adjustedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            adjustedDate = adjustedDate.plusDays(1);
        }
        
        return adjustedDate;
    }
    
    /**
     * Adjusts the delivery date to avoid holidays by moving to the next business day.
     * 
     * @param baseDate The base delivery date
     * @return The adjusted date avoiding holidays
     */
    public LocalDate adjustForHolidays(LocalDate baseDate) {
        LocalDate adjustedDate = baseDate;
        
        // Keep moving forward until we find a non-holiday weekday
        while (HOLIDAYS.contains(adjustedDate) || 
               adjustedDate.getDayOfWeek() == DayOfWeek.SATURDAY || 
               adjustedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            adjustedDate = adjustedDate.plusDays(1);
        }
        
        return adjustedDate;
    }
    
    /**
     * Calculates the expected delivery date with a specific base date (useful for testing).
     * 
     * @param baseDate The base date to calculate from
     * @param supplierLeadTime The supplier's lead time in days
     * @param priority The priority of the order
     * @return The calculated expected delivery date
     */
    public LocalDateTime calculateExpectedDeliveryFromDate(LocalDate baseDate, Integer supplierLeadTime, String priority) {
        int adjustedLeadTime = adjustLeadTimeForPriority(supplierLeadTime, priority);
        LocalDate expectedDate = baseDate.plusDays(adjustedLeadTime);
        expectedDate = adjustForWeekends(expectedDate);
        expectedDate = adjustForHolidays(expectedDate);
        
        return expectedDate.atTime(LocalTime.of(9, 0));
    }
}