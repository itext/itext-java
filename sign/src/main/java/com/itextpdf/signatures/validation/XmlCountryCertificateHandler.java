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
package com.itextpdf.signatures.validation;


import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class XmlCountryCertificateHandler extends AbstractXmlCertificateHandler {

    private static final List<String> INFORMATION_TAGS = new ArrayList<>();

    static {
        INFORMATION_TAGS.add(XmlTagConstants.SERVICE_TYPE);
        INFORMATION_TAGS.add(XmlTagConstants.SERVICE_STATUS);
        INFORMATION_TAGS.add(XmlTagConstants.X509CERTIFICATE);
        INFORMATION_TAGS.add(XmlTagConstants.SERVICE_STATUS_STARTING_TIME);
    }

    private final List<Certificate> certificateList = new ArrayList<>();
    private final List<CountryServiceContext> serviceContextList = new ArrayList<>();
    private StringBuilder information;
    private CountryServiceContext currentServiceContext = null;
    private ServiceStatusInfo currentServiceStatusInfo = null;

    XmlCountryCertificateHandler() {
        //empty constructor
    }

    private static String removeWhitespacesAndBreakLines(String data) {
        return data.replace(" ", "").replace("\n", "");
    }

    @Override
    public void startElement(String uri, String localName, String qName, HashMap<String, String> attributes) {
        if (XmlTagConstants.TSP_SERVICE.equals(localName)) {
            startProvider();
        } else if (XmlTagConstants.SERVICE_HISTORY_INSTANCE.equals(localName)
                || XmlTagConstants.SERVICE_INFORMATION.equals(localName)) {
            currentServiceStatusInfo = new ServiceStatusInfo();
        } else if (INFORMATION_TAGS.contains(localName)) {
            information = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (localName) {
            case XmlTagConstants.TSP_SERVICE:
                endProvider();
                break;
            case XmlTagConstants.X509CERTIFICATE:
                addCertificateToContext(information.toString());
                information = null;
                break;
            case XmlTagConstants.SERVICE_STATUS:
                if (currentServiceContext != null) {
                    currentServiceStatusInfo.setServiceStatus(information.toString());
                }

                information = null;
                break;
            case XmlTagConstants.SERVICE_TYPE:
                if (currentServiceContext != null) {
                    currentServiceContext.setServiceType(information.toString());
                }

                information = null;
                break;
            case XmlTagConstants.SERVICE_STATUS_STARTING_TIME:
                if (currentServiceContext != null) {
                    currentServiceStatusInfo.setServiceStatusStartingTime(information.toString());
                }

                information = null;
                break;
            case XmlTagConstants.SERVICE_INFORMATION:
            case XmlTagConstants.SERVICE_HISTORY_INSTANCE:
                if (currentServiceContext != null) {
                    currentServiceContext.addNewServiceStatus(currentServiceStatusInfo);
                }

                currentServiceStatusInfo = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (information != null) {
            information.append(ch, start, length);
        }
    }

    @Override
    IServiceContext getServiceContext(Certificate certificate) {
        for (CountryServiceContext context : serviceContextList) {
            if (context.getCertificates().contains(certificate)) {
                return context;
            }
        }

        return null;
    }

    @Override
    List<Certificate> getCertificateList() {
        return certificateList;
    }

    void startProvider() {
        currentServiceContext = new CountryServiceContext();
    }

    void addCertificateToContext(String certificateString) {
        if (currentServiceContext == null) {
            return;
        }

        Certificate certificate = getCertificateFromEncodedData(removeWhitespacesAndBreakLines(certificateString));
        currentServiceContext.addCertificate(certificate);
        certificateList.add(certificate);
    }

    void endProvider() {
        serviceContextList.add(currentServiceContext);
        currentServiceContext = null;
    }

    @Override
    void clear() {
        certificateList.clear();
        serviceContextList.clear();
    }
}
