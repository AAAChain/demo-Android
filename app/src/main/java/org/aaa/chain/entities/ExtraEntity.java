package org.aaa.chain.entities;

public class ExtraEntity {

    private String startTime;
    private String company;
    private String birthday;
    private String lastWorkingHour;
    private String name;
    private String lastCompany;
    private String sex;
    private String jobContentInfo;
    private String hashId;

    private String lastJobContentInfo;
    private String price;
    private String jobType;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLastWorkingHour() {
        return lastWorkingHour;
    }

    public void setLastWorkingHour(String lastWorkingHour) {
        this.lastWorkingHour = lastWorkingHour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastCompany() {
        return lastCompany;
    }

    public void setLastCompany(String lastCompany) {
        this.lastCompany = lastCompany;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getJobContentInfo() {
        return jobContentInfo;
    }

    public void setJobContentInfo(String jobContentInfo) {
        this.jobContentInfo = jobContentInfo;
    }

    public String getLastJobContentInfo() {
        return lastJobContentInfo;
    }

    public void setLastJobContentInfo(String lastJobContentInfo) {
        this.lastJobContentInfo = lastJobContentInfo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    private long low;
    private long high;
    private boolean unsigned;
    private String type;
    private String data;
    private long checksum;

    public long getLow() {
        return low;
    }

    public void setLow(long low) {
        this.low = low;
    }

    public long getHigh() {
        return high;
    }

    public void setHigh(long high) {
        this.high = high;
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getChecksum() {
        return checksum;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }
}
