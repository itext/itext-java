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

import com.itextpdf.commons.utils.DateTimeUtil;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class CountryServiceContext implements IServiceContext {
    private final List<Certificate> certificates = new ArrayList<>();

    private String serviceType;

    //It is expected that service statuses are ordered starting from the newest one.
    private final List<ServiceStatusInfo> serviceStatusInfos = new ArrayList<>();

    CountryServiceContext() {
        // Empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Certificate> getCertificates() {
        return new ArrayList<>(certificates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCertificate(Certificate certificate) {
        certificates.add(certificate);
    }

    void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    String getServiceType() {
        return serviceType;
    }

    void addNewServiceStatus(ServiceStatusInfo serviceStatusInfo) {
        serviceStatusInfos.add(serviceStatusInfo);
    }

    String getServiceStatusByDate(long milliseconds) {
        return getServiceStatusByDate(DateTimeUtil.getTimeFromMillis(milliseconds));
    }

    String getServiceStatusByDate(LocalDateTime time) {
        for (ServiceStatusInfo serviceStatusInfo: serviceStatusInfos) {
            if (serviceStatusInfo.getServiceStatusStartingTime().isBefore(time)) {
                return serviceStatusInfo.getServiceStatus();
            }
        }

        return null;
    }

    ServiceStatusInfo getCurrentStatusInfo() {
        return serviceStatusInfos.get(0);
    }

    int getServiceStatusInfosSize() {
        return serviceStatusInfos.size();
    }
}