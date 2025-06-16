package com.itextpdf.signatures.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class ServiceStatusInfo {

    private String serviceStatus;

    //Local time is used here because it is required to use UTC in a trusted lists, so no offset shall be presented.
    private LocalDateTime serviceStatusStartingTime;

    private final DateTimeFormatter statusStartDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    ServiceStatusInfo() {
        // empty constructor
    }

    ServiceStatusInfo(String serviceStatus, LocalDateTime serviceStatusStartingTime) {
        this.serviceStatus = serviceStatus;
        this.serviceStatusStartingTime = serviceStatusStartingTime;
    }

    void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    String getServiceStatus() {
        return serviceStatus;
    }

    void setServiceStatusStartingTime(String timeString) {
        this.serviceStatusStartingTime = statusStartDateFormat.parse(timeString, LocalDateTime::from);
    }


    void setServiceStatusStartingTime(LocalDateTime serviceStatusStartingTime) {
        this.serviceStatusStartingTime = serviceStatusStartingTime;
    }

    LocalDateTime getServiceStatusStartingTime() {
        return serviceStatusStartingTime;
    }
}
