package com.itextpdf.signatures.validation;

import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class CountryServiceContext implements IServiceContext {

    private List<Certificate> certificates;

    private String serviceType;

    //It is expected that service statuses are ordered starting from the newest one.
    private final List<ServiceStatusInfo> serviceStatusInfos = new ArrayList<>();

    CountryServiceContext() {
        //empty constructor
    }

    @Override
    public List<Certificate> getCertificates() {
        return new ArrayList<>(certificates);
    }

    @Override
    public void addCertificate(Certificate certificate) {
        if (certificates == null) {
            certificates = new ArrayList<>();
        }

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