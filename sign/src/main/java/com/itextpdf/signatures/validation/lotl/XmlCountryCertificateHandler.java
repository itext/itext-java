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


import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.validation.lotl.criteria.CertSubjectDNAttributeCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.CriteriaList;
import com.itextpdf.signatures.validation.lotl.criteria.ExtendedKeyUsageCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.KeyUsageCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.PolicySetCriteria;

import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

class XmlCountryCertificateHandler extends AbstractXmlCertificateHandler {
    private static final Set<String> INFORMATION_TAGS = new HashSet<>();
    private final Set<String> serviceTypes;
    private StringBuilder information;
    private CountryServiceContext currentServiceContext = null;
    private ServiceChronologicalInfo currentServiceChronologicalInfo = null;
    private AdditionalServiceInformationExtension currentServiceExtension = null;
    private QualifierExtension currentQualifierExtension = null;
    private final LinkedList<CriteriaList> criteriaListQueue = new LinkedList<>();
    private PolicySetCriteria currentPolicySetCriteria = null;
    private CertSubjectDNAttributeCriteria currentCertSubjectDNAttributeCriteria = null;
    private ExtendedKeyUsageCriteria currentExtendedKeyUsageCriteria = null;
    private KeyUsageCriteria currentKeyUsageCriteria = null;
    private String currentKeyUsageBitName = null;

    static {
        INFORMATION_TAGS.add(XmlTagConstants.SERVICE_TYPE);
        INFORMATION_TAGS.add(XmlTagConstants.SERVICE_STATUS);
        INFORMATION_TAGS.add(XmlTagConstants.X509CERTIFICATE);
        INFORMATION_TAGS.add(XmlTagConstants.SERVICE_STATUS_STARTING_TIME);
        INFORMATION_TAGS.add(XmlTagConstants.URI);
        INFORMATION_TAGS.add(XmlTagConstants.IDENTIFIER);
        INFORMATION_TAGS.add(XmlTagConstants.KEY_PURPOSE_ID);
        INFORMATION_TAGS.add(XmlTagConstants.KEY_USAGE_BIT);
    }

    XmlCountryCertificateHandler(Set<String> serviceTypes) {
        this.serviceTypes = new HashSet<>(serviceTypes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName, HashMap<String, String> attributes) {
        if (INFORMATION_TAGS.contains(localName)) {
            information = new StringBuilder();
        }
        if (XmlTagConstants.TSP_SERVICE.equals(localName)) {
            startProvider();
        } else if (XmlTagConstants.SERVICE_HISTORY_INSTANCE.equals(localName)
                || XmlTagConstants.SERVICE_INFORMATION.equals(localName)) {
            currentServiceChronologicalInfo = new ServiceChronologicalInfo();
        } else if (XmlTagConstants.ADDITIONAL_INFORMATION_EXTENSION.equals(localName)) {
            currentServiceExtension = new AdditionalServiceInformationExtension();
        } else if (XmlTagConstants.QUALIFICATION_ELEMENT.equals(localName)) {
            currentQualifierExtension = new QualifierExtension();
        } else if (XmlTagConstants.QUALIFIER.equals(localName)) {
            currentQualifierExtension.addQualifier(attributes.get(XmlTagConstants.URI_ATTRIBUTE));
        } else if (XmlTagConstants.CRITERIA_LIST.equals(localName)) {
            CriteriaList criteriaList = new CriteriaList(attributes.get(XmlTagConstants.ASSERT));
            if (criteriaListQueue.isEmpty()) {
                currentQualifierExtension.setCriteriaList(criteriaList);
            } else {
                criteriaListQueue.peekLast().addCriteria(criteriaList);
            }
            criteriaListQueue.add(criteriaList);
        } else if (XmlTagConstants.POLICY_SET.equals(localName)) {
            currentPolicySetCriteria = new PolicySetCriteria();
        } else if (XmlTagConstants.CERT_SUBJECT_DN_ATTRIBUTE.equals(localName)) {
            currentCertSubjectDNAttributeCriteria = new CertSubjectDNAttributeCriteria();
        } else if (XmlTagConstants.EXTENDED_KEY_USAGE.equals(localName)) {
            currentExtendedKeyUsageCriteria = new ExtendedKeyUsageCriteria();
        } else if (XmlTagConstants.KEY_USAGE.equals(localName)) {
            currentKeyUsageCriteria = new KeyUsageCriteria();
        } else if (XmlTagConstants.KEY_USAGE_BIT.equals(localName)) {
            currentKeyUsageBitName = attributes.get(XmlTagConstants.NAME);
        }
    }

    /**
     * {@inheritDoc}
     */
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
                if (currentServiceChronologicalInfo != null) {
                    currentServiceChronologicalInfo.setServiceStatus(information.toString());
                }

                information = null;
                break;
            case XmlTagConstants.SERVICE_TYPE:
                if (currentServiceContext != null) {
                    if (serviceTypes.isEmpty() || serviceTypes.contains(information.toString())) {
                        currentServiceContext.setServiceType(information.toString());
                    } else {
                        // If this service type is not among those which were requested, we should skip such service.
                        currentServiceContext = null;
                    }
                }

                information = null;
                break;
            case XmlTagConstants.SERVICE_STATUS_STARTING_TIME:
                if (currentServiceChronologicalInfo != null) {
                    currentServiceChronologicalInfo.setServiceStatusStartingTime(information.toString());
                }

                information = null;
                break;
            case XmlTagConstants.SERVICE_INFORMATION:
            case XmlTagConstants.SERVICE_HISTORY_INSTANCE:
                if (currentServiceContext != null) {
                    currentServiceContext.addServiceChronologicalInfo(currentServiceChronologicalInfo);
                }

                currentServiceChronologicalInfo = null;
                break;
            case XmlTagConstants.URI:
                if (currentServiceExtension != null) {
                    currentServiceExtension.setUri(information.toString());
                }
                break;
            case XmlTagConstants.ADDITIONAL_INFORMATION_EXTENSION:
                if (currentServiceChronologicalInfo != null) {
                    currentServiceChronologicalInfo.addServiceExtension(currentServiceExtension);
                }
                currentServiceExtension = null;
                break;
            case XmlTagConstants.QUALIFICATION_ELEMENT:
                if (currentServiceChronologicalInfo != null) {
                    currentServiceChronologicalInfo.addQualifierExtension(currentQualifierExtension);
                }
                currentQualifierExtension = null;
                break;
            case XmlTagConstants.CRITERIA_LIST:
                criteriaListQueue.pollLast();
                break;
            case XmlTagConstants.POLICY_SET:
                if (criteriaListQueue.peekLast() != null) {
                    criteriaListQueue.peekLast().addCriteria(currentPolicySetCriteria);
                }
                currentPolicySetCriteria = null;
                break;
            case XmlTagConstants.CERT_SUBJECT_DN_ATTRIBUTE:
                if (criteriaListQueue.peekLast() != null) {
                    criteriaListQueue.peekLast().addCriteria(currentCertSubjectDNAttributeCriteria);
                }
                currentCertSubjectDNAttributeCriteria = null;
                break;
            case XmlTagConstants.EXTENDED_KEY_USAGE:
                if (criteriaListQueue.peekLast() != null) {
                    criteriaListQueue.peekLast().addCriteria(currentExtendedKeyUsageCriteria);
                }
                currentExtendedKeyUsageCriteria = null;
                break;
            case XmlTagConstants.KEY_USAGE:
                if (criteriaListQueue.peekLast() != null) {
                    criteriaListQueue.peekLast().addCriteria(currentKeyUsageCriteria);
                }
                currentKeyUsageCriteria = null;
                break;
            case XmlTagConstants.IDENTIFIER:
                if (currentPolicySetCriteria != null) {
                    currentPolicySetCriteria.addRequiredPolicyId(information.toString());
                } else if (currentCertSubjectDNAttributeCriteria != null) {
                    currentCertSubjectDNAttributeCriteria.addRequiredAttributeId(information.toString());
                }
                break;
            case XmlTagConstants.KEY_PURPOSE_ID:
                if (currentExtendedKeyUsageCriteria != null) {
                    currentExtendedKeyUsageCriteria.addRequiredExtendedKeyUsage(information.toString());
                }
                break;
            case XmlTagConstants.KEY_USAGE_BIT:
                if (currentKeyUsageCriteria != null) {
                    currentKeyUsageCriteria.addKeyUsageBit(currentKeyUsageBitName, information.toString());
                }
                currentKeyUsageBitName = null;
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (information != null) {
            information.append(ch, start, length);
        }
    }

    void startProvider() {
        currentServiceContext = new CountryServiceContext();
    }

    void addCertificateToContext(String certificateString) {
        if (currentServiceContext == null) {
            return;
        }
        Certificate certificate =
                CertificateUtil.createCertificateFromEncodedData(removeWhitespacesAndBreakLines(certificateString));
        currentServiceContext.addCertificate(certificate);
    }

    void endProvider() {
        if (currentServiceContext != null) {
            serviceContextList.add(currentServiceContext);
            currentServiceContext = null;
        }
    }

    private static String removeWhitespacesAndBreakLines(String data) {
        return data.replace(" ", "").replace("\n", "");
    }
}
