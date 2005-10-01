package org.lamsfoundation.lams.tool.mc;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class McContent implements Serializable {

    /** identifier field */
    private Long uid;

    /** persistent field */
    private Long mcContentId;

    /** nullable persistent field */
    private String title;

    /** nullable persistent field */
    private String instructions;

    /** nullable persistent field */
    private String creationDate;

    /** nullable persistent field */
    private Date updateDate;

    /** nullable persistent field */
    private boolean questionsSequenced;

    /** nullable persistent field */
    private boolean usernameVisible;

    /** nullable persistent field */
    private long createdBy;

    /** nullable persistent field */
    private String monitoringReportTitle;

    /** nullable persistent field */
    private String reportTitle;

    /** nullable persistent field */
    private boolean runOffline;

    /** nullable persistent field */
    private boolean defineLater;

    /** nullable persistent field */
    private boolean synchInMonitor;

    /** nullable persistent field */
    private String offlineInstructions;

    /** nullable persistent field */
    private String onlineInstructions;

    /** nullable persistent field */
    private String endLearningMessage;

    /** nullable persistent field */
    private boolean contentInUse;

    /** nullable persistent field */
    private Integer passMark;

    /** nullable persistent field */
    private boolean showFeedback;

    /** nullable persistent field */
    private boolean showTopUsers;

    /** persistent field */
    private Set mcSessions;

    /** persistent field */
    private Set mcQueContents;

    /** full constructor */
    public McContent(Long mcContentId, String title, String instructions, String creationDate, Date updateDate, boolean questionsSequenced, boolean usernameVisible, long createdBy, String monitoringReportTitle, String reportTitle, boolean runOffline, boolean defineLater, boolean synchInMonitor, String offlineInstructions, String onlineInstructions, String endLearningMessage, boolean contentInUse, Integer passMark, boolean showFeedback, boolean showTopUsers, Set mcSessions, Set mcQueContents) {
        this.mcContentId = mcContentId;
        this.title = title;
        this.instructions = instructions;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.questionsSequenced = questionsSequenced;
        this.usernameVisible = usernameVisible;
        this.createdBy = createdBy;
        this.monitoringReportTitle = monitoringReportTitle;
        this.reportTitle = reportTitle;
        this.runOffline = runOffline;
        this.defineLater = defineLater;
        this.synchInMonitor = synchInMonitor;
        this.offlineInstructions = offlineInstructions;
        this.onlineInstructions = onlineInstructions;
        this.endLearningMessage = endLearningMessage;
        this.contentInUse = contentInUse;
        this.passMark = passMark;
        this.showFeedback = showFeedback;
        this.showTopUsers = showTopUsers;
        this.mcSessions = mcSessions;
        this.mcQueContents = mcQueContents;
    }

    /** default constructor */
    public McContent() {
    }

    /** minimal constructor */
    public McContent(Long mcContentId, Set mcSessions, Set mcQueContents) {
        this.mcContentId = mcContentId;
        this.mcSessions = mcSessions;
        this.mcQueContents = mcQueContents;
    }

    public Long getUid() {
        return this.uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getMcContentId() {
        return this.mcContentId;
    }

    public void setMcContentId(Long mcContentId) {
        this.mcContentId = mcContentId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isQuestionsSequenced() {
        return this.questionsSequenced;
    }

    public void setQuestionsSequenced(boolean questionsSequenced) {
        this.questionsSequenced = questionsSequenced;
    }

    public boolean isUsernameVisible() {
        return this.usernameVisible;
    }

    public void setUsernameVisible(boolean usernameVisible) {
        this.usernameVisible = usernameVisible;
    }

    public long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public String getMonitoringReportTitle() {
        return this.monitoringReportTitle;
    }

    public void setMonitoringReportTitle(String monitoringReportTitle) {
        this.monitoringReportTitle = monitoringReportTitle;
    }

    public String getReportTitle() {
        return this.reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public boolean isRunOffline() {
        return this.runOffline;
    }

    public void setRunOffline(boolean runOffline) {
        this.runOffline = runOffline;
    }

    public boolean isDefineLater() {
        return this.defineLater;
    }

    public void setDefineLater(boolean defineLater) {
        this.defineLater = defineLater;
    }

    public boolean isSynchInMonitor() {
        return this.synchInMonitor;
    }

    public void setSynchInMonitor(boolean synchInMonitor) {
        this.synchInMonitor = synchInMonitor;
    }

    public String getOfflineInstructions() {
        return this.offlineInstructions;
    }

    public void setOfflineInstructions(String offlineInstructions) {
        this.offlineInstructions = offlineInstructions;
    }

    public String getOnlineInstructions() {
        return this.onlineInstructions;
    }

    public void setOnlineInstructions(String onlineInstructions) {
        this.onlineInstructions = onlineInstructions;
    }

    public String getEndLearningMessage() {
        return this.endLearningMessage;
    }

    public void setEndLearningMessage(String endLearningMessage) {
        this.endLearningMessage = endLearningMessage;
    }

    public boolean isContentInUse() {
        return this.contentInUse;
    }

    public void setContentInUse(boolean contentInUse) {
        this.contentInUse = contentInUse;
    }

    public Integer getPassMark() {
        return this.passMark;
    }

    public void setPassMark(Integer passMark) {
        this.passMark = passMark;
    }

    public boolean isShowFeedback() {
        return this.showFeedback;
    }

    public void setShowFeedback(boolean showFeedback) {
        this.showFeedback = showFeedback;
    }

    public boolean isShowTopUsers() {
        return this.showTopUsers;
    }

    public void setShowTopUsers(boolean showTopUsers) {
        this.showTopUsers = showTopUsers;
    }

    public Set getMcSessions() {
        return this.mcSessions;
    }

    public void setMcSessions(Set mcSessions) {
        this.mcSessions = mcSessions;
    }

    public Set getMcQueContents() {
        return this.mcQueContents;
    }

    public void setMcQueContents(Set mcQueContents) {
        this.mcQueContents = mcQueContents;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("uid", getUid())
            .toString();
    }

}
