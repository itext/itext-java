/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.signatures.validation.lotl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ServiceChronologicalInfo {
    static final String GRANTED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/granted";
    static final String GRANTED_NATIONALLY =
            "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/recognisedatnationallevel";
    static final String ACCREDITED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accredited";
    static final String SET_BY_NATIONAL_LAW = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/setbynationallaw";
    private static final Set<String> validStatuses = new HashSet<>();
    private final List<AdditionalServiceInformationExtension> extensions = new ArrayList<>();
    private final DateTimeFormatter statusStartDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private String serviceStatus;
    //Local time is used here because it is required to use UTC in a trusted lists, so no offset shall be presented.
    private LocalDateTime serviceStatusStartingTime;

    static {
        validStatuses.add(GRANTED);
        validStatuses.add(GRANTED_NATIONALLY);
        validStatuses.add(ACCREDITED);
        validStatuses.add(SET_BY_NATIONAL_LAW);
    }

    ServiceChronologicalInfo() {
        // empty constructor
    }

    ServiceChronologicalInfo(String serviceStatus, LocalDateTime serviceStatusStartingTime) {
        this.serviceStatus = serviceStatus;
        this.serviceStatusStartingTime = serviceStatusStartingTime;
    }

    static boolean isStatusValid(String status) {
        return validStatuses.contains(status);
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public LocalDateTime getServiceStatusStartingTime() {
        return serviceStatusStartingTime;
    }

    void setServiceStatusStartingTime(String timeString) {
        this.serviceStatusStartingTime = statusStartDateFormat.parse(timeString, LocalDateTime::from);
    }

    void setServiceStatusStartingTime(LocalDateTime serviceStatusStartingTime) {
        this.serviceStatusStartingTime = serviceStatusStartingTime;
    }

    void addExtension(AdditionalServiceInformationExtension extension) {
        extensions.add(extension);
    }

    public List<AdditionalServiceInformationExtension> getExtensions() {
        return extensions;
    }
}
