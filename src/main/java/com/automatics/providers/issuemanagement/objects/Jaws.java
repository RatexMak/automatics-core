/**
 * Copyright 2021 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.10 at 03:10:54 PM IST 
//


package com.automatics.providers.issuemanagement.objects;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="JiraDetails">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="JiraUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                   &lt;element name="JiraUserName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="JiraPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Issues" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="IssueDetails" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ProjectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="ProjectKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Assignee" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IssueSummary" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IssueDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="LabelsList" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="LabelName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Priority" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="WatcherList" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Watcher" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "jiraDetails",
    "issues"
})
@XmlRootElement(name = "Jaws")
public class Jaws {

    @XmlElement(name = "JiraDetails", required = true)
    protected Jaws.JiraDetails jiraDetails;
    @XmlElement(name = "Issues")
    protected Jaws.Issues issues;

    /**
     * Gets the value of the jiraDetails property.
     * 
     * @return
     *     possible object is
     *     {@link Jaws.JiraDetails }
     *     
     */
    public Jaws.JiraDetails getJiraDetails() {
        return jiraDetails;
    }

    /**
     * Sets the value of the jiraDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link Jaws.JiraDetails }
     *     
     */
    public void setJiraDetails(Jaws.JiraDetails value) {
        this.jiraDetails = value;
    }

    /**
     * Gets the value of the issues property.
     * 
     * @return
     *     possible object is
     *     {@link Jaws.Issues }
     *     
     */
    public Jaws.Issues getIssues() {
        return issues;
    }

    /**
     * Sets the value of the issues property.
     * 
     * @param value
     *     allowed object is
     *     {@link Jaws.Issues }
     *     
     */
    public void setIssues(Jaws.Issues value) {
        this.issues = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="IssueDetails" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ProjectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="ProjectKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="Assignee" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="IssueSummary" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="IssueDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="LabelsList" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="LabelName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="Priority" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="WatcherList" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Watcher" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "issueDetails"
    })
    public static class Issues {

        @XmlElement(name = "IssueDetails")
        protected List<Jaws.Issues.IssueDetails> issueDetails;

        /**
         * Gets the value of the issueDetails property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the issueDetails property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getIssueDetails().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Jaws.Issues.IssueDetails }
         * 
         * 
         */
        public List<Jaws.Issues.IssueDetails> getIssueDetails() {
            if (issueDetails == null) {
                issueDetails = new ArrayList<Jaws.Issues.IssueDetails>();
            }
            return this.issueDetails;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="ProjectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="ProjectKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="Assignee" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="IssueSummary" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="IssueDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="LabelsList" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="LabelName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="Priority" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="WatcherList" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Watcher" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "projectName",
            "projectKey",
            "assignee",
            "issueSummary",
            "issueDescription",
            "labelsList",
            "priority",
            "watcherList"
        })
        public static class IssueDetails {

            @XmlElement(name = "ProjectName", required = true)
            protected String projectName;
            @XmlElement(name = "ProjectKey", required = true)
            protected String projectKey;
            @XmlElement(name = "Assignee", required = true)
            protected String assignee;
            @XmlElement(name = "IssueSummary", required = true)
            protected String issueSummary;
            @XmlElement(name = "IssueDescription", required = true)
            protected String issueDescription;
            @XmlElement(name = "LabelsList")
	    protected List<String> labelsList;
            @XmlElement(name = "Priority", required = true)
            protected String priority;
            @XmlElement(name = "WatcherList")
	    protected List<String> watcherList;
 @XmlElement(name = "SearchLabelsList")
	    protected List<String> searchLabelsList;
	    @XmlElement(name = "buildName")
	    protected String buildName;
	    @XmlElement(name = "manualId")
	    protected String manualId;
	    @XmlElement(name = "stepNumber")
	    protected String stepNumber;
	    @XmlElement(name = "automationId")
	    protected String automationId;
	    @XmlElement(name = "attachments")
	    protected List<String> attachments;
	    @XmlElement(name = "jenkinsUrl")
	    protected String jenkinsUrl;
	    @XmlElement(name = "jobId")
	    protected long jobId;
	    @XmlElement(name = "buildAppender")
	    protected String buildAppender;
	    @XmlElement(name = "environmentType")
	    protected String environmentType;

            public String getBuildAppender() {
	        return buildAppender;
	    }

	    public void setBuildAppender(String buildAppender) {
	        this.buildAppender = buildAppender;
	    }

	    public String getEnvironmentType() {
	        return environmentType;
	    }

	    public void setEnvironmentType(String environmentType) {
	        this.environmentType = environmentType;
	    }

	    /**
             * Gets the value of the projectName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getProjectName() {
                return projectName;
            }

            /**
             * Sets the value of the projectName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setProjectName(String value) {
                this.projectName = value;
            }

            /**
             * Gets the value of the projectKey property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getProjectKey() {
                return projectKey;
            }

            /**
             * Sets the value of the projectKey property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setProjectKey(String value) {
                this.projectKey = value;
            }

            /**
             * Gets the value of the assignee property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAssignee() {
                return assignee;
            }

            /**
             * Sets the value of the assignee property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAssignee(String value) {
                this.assignee = value;
            }

            /**
             * Gets the value of the issueSummary property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIssueSummary() {
                return issueSummary;
            }

            /**
             * Sets the value of the issueSummary property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIssueSummary(String value) {
                this.issueSummary = value;
            }

            /**
             * Gets the value of the issueDescription property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIssueDescription() {
                return issueDescription;
            }

            /**
             * Sets the value of the issueDescription property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIssueDescription(String value) {
                this.issueDescription = value;
            }

            /**
             * Gets the value of the labelsList property.
             * 
             * @return
             *     possible object is
             *     {@link Jaws.Issues.IssueDetails.LabelsList }
             *     
             */
	    public List<String> getLabelsList() {
                return labelsList;
            }

            /**
             * Sets the value of the labelsList property.
             * 
             * @param value
             *     allowed object is
             *     {@link Jaws.Issues.IssueDetails.LabelsList }
             *     
             */
	    public void setLabelsList(List<String> value) {
                this.labelsList = value;
            }

            /**
             * Gets the value of the priority property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPriority() {
                return priority;
            }

            /**
             * Sets the value of the priority property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPriority(String value) {
                this.priority = value;
            }

	    public String getBuildName() {
		return buildName;
	    }

	    public void setBuildName(String buildName) {
		this.buildName = buildName;
	    }

	    public String getManualId() {
		return manualId;
	    }

	    public void setManualId(String manualId) {
		this.manualId = manualId;
	    }

	    public String getStepNumber() {
		return stepNumber;
	    }

	    public void setStepNumber(String stepNumber) {
		this.stepNumber = stepNumber;
	    }

	    public String getAutomationId() {
		return automationId;
	    }

	    public void setAutomationId(String automationId) {
		this.automationId = automationId;
	    }

	    public List<String> getAttachments() {
		return attachments;
	    }

	    public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	    }

	    public String getJenkinsUrl() {
		return jenkinsUrl;
	    }

	    public void setJenkinsUrl(String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
	    }

	    public long getJobId() {
		return jobId;
	    }

	    public void setJobId(long jobId) {
		this.jobId = jobId;
	    }

            /**
             * Gets the value of the watcherList property.
             * 
             * @return
             *     possible object is
             *     {@link Jaws.Issues.IssueDetails.WatcherList }
             *     
             */
	    public List<String> getWatcherList() {
                return watcherList;
            }

            /**
             * Sets the value of the watcherList property.
             * 
             * @param value
             *     allowed object is
             *     {@link Jaws.Issues.IssueDetails.WatcherList }
             *     
             */
	    public void setWatcherList(List<String> value) {
                this.watcherList = value;
            }

	    /**
	     * Sets the value of the searchList property.
	     * 
	     * @param value
	     *            allowed object is {@link Jaws.Issues.IssueDetails.SearchList }
	     * 
	     */
	    public void setSearchList(List<String> value) {
		this.searchLabelsList = value;
	    }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="LabelName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "labelName"
            })
            public static class LabelsList {

                @XmlElement(name = "LabelName")
                protected List<String> labelName;

                /**
                 * Gets the value of the labelName property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the labelName property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getLabelName().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 * 
                 * 
                 */
                public List<String> getLabelName() {
                    if (labelName == null) {
                        labelName = new ArrayList<String>();
                    }
                    return this.labelName;
                }

            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="Watcher" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "watcher"
            })
            public static class WatcherList {

                @XmlElement(name = "Watcher")
                protected List<String> watcher;

                /**
                 * Gets the value of the watcher property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the watcher property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getWatcher().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 * 
                 * 
                 */
                public List<String> getWatcher() {
                    if (watcher == null) {
                        watcher = new ArrayList<String>();
                    }
                    return this.watcher;
                }

            }

	    /**
	     * <p>
	     * Java class for anonymous complex type.
	     * 
	     * <p>
	     * The following schema fragment specifies the expected content contained within this class.
	     * 
	     * <pre>
	     * &lt;complexType>
	     *   &lt;complexContent>
	     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	     *       &lt;sequence>
	     *         &lt;element name="Watcher" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
	     *       &lt;/sequence>
	     *     &lt;/restriction>
	     *   &lt;/complexContent>
	     * &lt;/complexType>
	     * </pre>
	     * 
	     * 
	     */
	    @XmlAccessorType(XmlAccessType.FIELD)
	    @XmlType(name = "", propOrder = { "search" })
	    public static class SearchLabelsList {

		@XmlElement(name = "Search")
		protected List<String> searchLabelsList;

		/**
		 * Gets the value of the watcher property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification
		 * you make to the returned list will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the watcher property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getWatcher().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list {@link String }
		 * 
		 * 
		 */
		public List<String> getSearch() {
		    if (searchLabelsList == null) {
			searchLabelsList = new ArrayList<String>();
		    }
		    return this.searchLabelsList;
		}

	    }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="JiraUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *         &lt;element name="JiraUserName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="JiraPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "jiraUrl",
        "jiraUserName",
        "jiraPassword"
    })
    public static class JiraDetails {

        @XmlElement(name = "JiraUrl", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String jiraUrl;
        @XmlElement(name = "JiraUserName", required = true)
        protected String jiraUserName;
        @XmlElement(name = "JiraPassword", required = true)
        protected String jiraPassword;

        /**
         * Gets the value of the jiraUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJiraUrl() {
            return jiraUrl;
        }

        /**
         * Sets the value of the jiraUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJiraUrl(String value) {
            this.jiraUrl = value;
        }

        /**
         * Gets the value of the jiraUserName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJiraUserName() {
            return jiraUserName;
        }

        /**
         * Sets the value of the jiraUserName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJiraUserName(String value) {
            this.jiraUserName = value;
        }

        /**
         * Gets the value of the jiraPassword property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJiraPassword() {
            return jiraPassword;
        }

        /**
         * Sets the value of the jiraPassword property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJiraPassword(String value) {
            this.jiraPassword = value;
        }

    }

}
